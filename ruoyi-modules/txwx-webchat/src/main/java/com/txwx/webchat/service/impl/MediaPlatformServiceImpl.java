package com.txwx.webchat.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.exception.CheckedException;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.webchat.domain.entity.mysql.MediaPlatform;
import com.txwx.webchat.domain.entity.mysql.UserPlatform;
import com.txwx.webchat.domain.enums.SyncStatusEnum;
import com.txwx.webchat.domain.vo.UserPlatformVo;
import com.txwx.webchat.mapper.MediaPlatformMapper;
import com.txwx.webchat.mapper.UserPlatformMapper;
import com.txwx.webchat.service.IMediaPlatformService;
import com.txwx.webchat.util.WebChatUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@DS("slave")
public class MediaPlatformServiceImpl extends ServiceImpl<MediaPlatformMapper, MediaPlatform> implements IMediaPlatformService {

    @Autowired
    private UserPlatformMapper userPlatformMapper;
    @Autowired
    private MediaPlatformAsyncServiceImpl mediaPlatformAsyncServiceImpl;
    @Autowired
    private MediaPlatformMapper mediaPlatformMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveUpdate(MediaPlatform mediaPlatform) {
        LambdaQueryWrapper<UserPlatform> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserPlatform::getPlatformAppId, mediaPlatform.getAppId());
        queryWrapper.eq(UserPlatform::getUserId, SecurityUtils.getLoginUser().getUserid());
        long count = userPlatformMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new CheckedException("您已绑定该平台,请误重复绑定!");
        }
        String accessToken = WebChatUtil.getAccessToken(mediaPlatform.getAppId(), mediaPlatform.getSecret());
        if (accessToken == null) {
            throw new CheckedException("appId及secret验证失败!");
        }
        if (mediaPlatform.getId() != null) {
            return updateById(mediaPlatform);
        }
        MediaPlatform isExistPlatform = getOne(new LambdaQueryWrapper<MediaPlatform>().eq(
                MediaPlatform::getAppId, mediaPlatform.getAppId()
        ));
        if (isExistPlatform == null){
            // 保存平台
            save(mediaPlatform);
            // 关联关系
            UserPlatform userPlatform = new UserPlatform();
            userPlatform.setPlatformId(mediaPlatform.getId());
            userPlatform.setUserId(SecurityUtils.getLoginUser().getUserid());
            userPlatform.setPlatformAppId(mediaPlatform.getAppId());
            return userPlatformMapper.insert(userPlatform) > 0;
        }else {
            // 是否是平台删除 之后 再添加
            if (Objects.equals(isExistPlatform.getIsDelete(), 1)) {
                mediaPlatform.setId(isExistPlatform.getId());
                mediaPlatform.setIsDelete(0);
                updateById(mediaPlatform);
            }
            // 保存关系
            UserPlatform userPlatform = new UserPlatform();
            userPlatform.setPlatformId(isExistPlatform.getId());
            userPlatform.setUserId(SecurityUtils.getLoginUser().getUserid());
            userPlatform.setPlatformAppId(isExistPlatform.getAppId());
            return userPlatformMapper.insert(userPlatform) > 0;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean dataSync(String appId) throws Exception {
        LambdaQueryWrapper<MediaPlatform> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MediaPlatform::getAppId, appId);
        MediaPlatform mediaPlatform = getOne(queryWrapper);
        if (mediaPlatform == null) {
            throw new CheckedException("平台不存在!");
        }else if ((Objects.equals(mediaPlatform.getSyncStatus(), SyncStatusEnum.SYNCING.getCode())
                || Objects.equals(mediaPlatform.getSyncStatus(), SyncStatusEnum.SYNCED_SUCCESS.getCode()))) {
            throw new CheckedException("数据已同步,请勿重复同步!");
        }
        // todo 校验appid+secret
        String accessToken = WebChatUtil.getAccessToken(appId, mediaPlatform.getSecret());
        if (accessToken == null) {
            throw new CheckedException("appId及secret验证失败!");
        }
        // 状态修改为 同步中
        mediaPlatform.setSyncStatus(SyncStatusEnum.SYNCING.getCode());
        updateById(mediaPlatform);
        // 异步同步数据
        mediaPlatformAsyncServiceImpl.syncPlatformData(accessToken, mediaPlatform);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        // 平台本身不被删除、删除的是关联关系
        // 依据平台id+用户id
        LambdaQueryWrapper<UserPlatform> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserPlatform::getPlatformId, id);
        queryWrapper.eq(UserPlatform::getUserId, SecurityUtils.getLoginUser().getUserid());
        userPlatformMapper.delete(queryWrapper);
        // 查询平台是否有关联关系(若无关联,则假删除平台)
        long count = userPlatformMapper.selectCount(new LambdaQueryWrapper<UserPlatform>()
                .eq(UserPlatform::getPlatformId, id));
        if (count == 0) {
            MediaPlatform mediaPlatform = new MediaPlatform();
            mediaPlatform.setId(id);
            mediaPlatform.setIsDelete(1);
            return updateById(mediaPlatform);
        }
        return true;
    }

    @Override
    public List<UserPlatformVo> selectList() {
        LambdaQueryWrapper<UserPlatform> queryWrapper = new LambdaQueryWrapper<UserPlatform>()
                .eq(UserPlatform::getUserId, SecurityUtils.getLoginUser().getUserid());
        List<UserPlatform> userPlatformList = userPlatformMapper.selectList(queryWrapper);
        if (userPlatformList == null || userPlatformList.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> platformIds = userPlatformList.stream()
                .map(UserPlatform::getPlatformId)
                .distinct()
                .collect(Collectors.toList());

        List<MediaPlatform> mediaPlatforms = mediaPlatformMapper.selectList(
                new LambdaQueryWrapper<MediaPlatform>()
                        .in(MediaPlatform::getId, platformIds)
        );

        Map<Long, MediaPlatform> platformMap = mediaPlatforms.stream()
                .collect(Collectors.toMap(MediaPlatform::getId, Function.identity()));

        List<UserPlatformVo> res = new ArrayList<>(userPlatformList.size());
        for (UserPlatform userPlatform : userPlatformList) {
            UserPlatformVo userPlatformVo = new UserPlatformVo();
            BeanUtils.copyProperties(userPlatform, userPlatformVo);
            userPlatformVo.setMediaPlatform(platformMap.get(userPlatform.getPlatformId()));
            res.add(userPlatformVo);
        }
        return res;
    }

}
