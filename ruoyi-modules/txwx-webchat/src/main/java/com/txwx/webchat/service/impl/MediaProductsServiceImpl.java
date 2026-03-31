package com.txwx.webchat.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.webchat.domain.entity.mysql.MediaPlatform;
import com.txwx.webchat.domain.entity.mysql.MediaProduct;
import com.txwx.webchat.domain.entity.mysql.UserPlatform;
import com.txwx.webchat.domain.vo.MediaProductVo;
import com.txwx.webchat.domain.vo.UserPlatformVo;
import com.txwx.webchat.mapper.MediaPlatformMapper;
import com.txwx.webchat.mapper.MediaProductsMapper;
import com.txwx.webchat.mapper.UserPlatformMapper;
import com.txwx.webchat.service.IMediaProductsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@DS("slave")
public class MediaProductsServiceImpl extends ServiceImpl<MediaProductsMapper, MediaProduct> implements IMediaProductsService {

    @Autowired
    private MediaProductsMapper mediaProductsMapper;
    @Autowired
    private MediaPlatformMapper mediaPlatformMapper;
    @Autowired
    private UserPlatformMapper userPlatformMapper;

    @Override
    public boolean add(MediaProduct mediaProduct) {
        LambdaQueryWrapper<MediaProduct> query = new LambdaQueryWrapper<>();
        query.eq(MediaProduct::getIsDelete, 0);
        int count = count(query);
        if (count > 1) {
            throw new ServiceException("当前只能添加一款产品!");
        }else {
            // save(mediaProduct);
            LambdaQueryWrapper<MediaProduct> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(MediaProduct::getName, mediaProduct.getName());
            queryWrapper.eq(MediaProduct::getIsDelete, 1);
            MediaProduct dbProduct = mediaProductsMapper.selectOne(queryWrapper);
            if (dbProduct != null) {
                mediaProduct.setId(dbProduct.getId());
                mediaProduct.setIsDelete(0);
                updateById(mediaProduct);
            }else {
                save(mediaProduct);
            }
        }
        return true;
    }


    @Override
    public List<MediaProductVo> selectList() {
        List<MediaProduct> productList = mediaProductsMapper.selectList(
                new LambdaQueryWrapper<MediaProduct>()
                        .eq(MediaProduct::getIsDelete, 0)
        );

        if (productList == null || productList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> productIds = productList.stream()
                .map(MediaProduct::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (productIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 用户绑定的平台关系
        List<UserPlatform> userPlatformList = userPlatformMapper.selectList(null);

        if (userPlatformList == null || userPlatformList.isEmpty()) {
            return productList.stream().map(product -> {
                MediaProductVo vo = new MediaProductVo();
                BeanUtils.copyProperties(product, vo);
                vo.setUserPlatformList(Collections.emptyList());
                return vo;
            }).collect(Collectors.toList());
        }

        List<Long> userPlatformIds = userPlatformList.stream()
                .map(UserPlatform::getPlatformId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (userPlatformIds.isEmpty()) {
            return productList.stream().map(product -> {
                MediaProductVo vo = new MediaProductVo();
                BeanUtils.copyProperties(product, vo);
                vo.setUserPlatformList(Collections.emptyList());
                return vo;
            }).collect(Collectors.toList());
        }

        List<MediaPlatform> authPlatformList = mediaPlatformMapper.selectList(
                new LambdaQueryWrapper<MediaPlatform>()
                        .in(MediaPlatform::getProductId, productIds)
                        .in(MediaPlatform::getId, userPlatformIds)
        );

        if (authPlatformList == null) {
            authPlatformList = Collections.emptyList();
        }

        Map<Long, MediaPlatform> platformIdMap = authPlatformList.stream()
                .collect(Collectors.toMap(MediaPlatform::getId, Function.identity(), (a, b) -> a));

        Map<Long, List<MediaPlatform>> productPlatformMap = authPlatformList.stream()
                .collect(Collectors.groupingBy(MediaPlatform::getProductId));

        List<MediaProductVo> result = productList.stream().map(product -> {
            MediaProductVo vo = new MediaProductVo();
            BeanUtils.copyProperties(product, vo);

            List<MediaPlatform> productPlatforms = productPlatformMap.getOrDefault(product.getId(), Collections.emptyList());
            if (productPlatforms.isEmpty()) {
                vo.setUserPlatformList(Collections.emptyList());
                return vo;
            }

            Set<Long> productPlatformIds = productPlatforms.stream()
                    .map(MediaPlatform::getId)
                    .collect(Collectors.toSet());

            List<UserPlatformVo> userPlatformVoList = userPlatformList.stream()
                    .filter(up -> up.getPlatformId() != null && productPlatformIds.contains(up.getPlatformId()))
                    .map(up -> {
                        UserPlatformVo upVo = new UserPlatformVo();
                        BeanUtils.copyProperties(up, upVo);
                        upVo.setMediaPlatform(platformIdMap.get(up.getPlatformId()));
                        return upVo;
                    })
                    .collect(Collectors.toList());

            vo.setUserPlatformList(userPlatformVoList);
            return vo;
        }).collect(Collectors.toList());

        return result;
    }


//    @Override
//    public List<MediaProductVo> selectList() {
//        Long userId = SecurityUtils.getLoginUser().getUserid();
//        // 用户自己的产品
//        List<MediaProduct> productList = mediaProductsMapper.selectList(
//                new LambdaQueryWrapper<MediaProduct>()
//                        .eq(MediaProduct::getIsDelete, 0)
//                //      .eq(MediaProduct::getCreateBy, userId)
//        );
//
//        if (productList == null || productList.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        List<Long> productIds = productList.stream()
//                .map(MediaProduct::getId)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//
//        if (productIds.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        // 用户绑定的平台关系
//        List<UserPlatform> userPlatformList = userPlatformMapper.selectList(
//                new LambdaQueryWrapper<UserPlatform>()
//                        .eq(UserPlatform::getUserId, userId)
//        );
//
//        if (userPlatformList == null || userPlatformList.isEmpty()) {
//            return productList.stream().map(product -> {
//                MediaProductVo vo = new MediaProductVo();
//                BeanUtils.copyProperties(product, vo);
//                vo.setUserPlatformList(Collections.emptyList());
//                return vo;
//            }).collect(Collectors.toList());
//        }
//
//        List<Long> userPlatformIds = userPlatformList.stream()
//                .map(UserPlatform::getPlatformId)
//                .filter(Objects::nonNull)
//                .distinct()
//                .collect(Collectors.toList());
//
//        if (userPlatformIds.isEmpty()) {
//            return productList.stream().map(product -> {
//                MediaProductVo vo = new MediaProductVo();
//                BeanUtils.copyProperties(product, vo);
//                vo.setUserPlatformList(Collections.emptyList());
//                return vo;
//            }).collect(Collectors.toList());
//        }
//
//        List<MediaPlatform> authPlatformList = mediaPlatformMapper.selectList(
//                new LambdaQueryWrapper<MediaPlatform>()
//                        .in(MediaPlatform::getProductId, productIds)
//                        .in(MediaPlatform::getId, userPlatformIds)
//        );
//
//        if (authPlatformList == null) {
//            authPlatformList = Collections.emptyList();
//        }
//
//        Map<Long, MediaPlatform> platformIdMap = authPlatformList.stream()
//                .collect(Collectors.toMap(MediaPlatform::getId, Function.identity(), (a, b) -> a));
//
//        Map<Long, List<MediaPlatform>> productPlatformMap = authPlatformList.stream()
//                .collect(Collectors.groupingBy(MediaPlatform::getProductId));
//
//        List<MediaProductVo> result = productList.stream().map(product -> {
//            MediaProductVo vo = new MediaProductVo();
//            BeanUtils.copyProperties(product, vo);
//
//            List<MediaPlatform> productPlatforms = productPlatformMap.getOrDefault(product.getId(), Collections.emptyList());
//            if (productPlatforms.isEmpty()) {
//                vo.setUserPlatformList(Collections.emptyList());
//                return vo;
//            }
//
//            Set<Long> productPlatformIds = productPlatforms.stream()
//                    .map(MediaPlatform::getId)
//                    .collect(Collectors.toSet());
//
//            List<UserPlatformVo> userPlatformVoList = userPlatformList.stream()
//                    .filter(up -> up.getPlatformId() != null && productPlatformIds.contains(up.getPlatformId()))
//                    .map(up -> {
//                        UserPlatformVo upVo = new UserPlatformVo();
//                        BeanUtils.copyProperties(up, upVo);
//                        upVo.setMediaPlatform(platformIdMap.get(up.getPlatformId()));
//                        return upVo;
//                    })
//                    .collect(Collectors.toList());
//
//            vo.setUserPlatformList(userPlatformVoList);
//            return vo;
//        }).collect(Collectors.toList());
//
//        return result;
//    }

    @Override
    public MediaProductVo selectById(Long id) {
        Long userId = SecurityUtils.getLoginUser().getUserid();

        // 当前用户自己的产品
        MediaProduct product = mediaProductsMapper.selectOne(
                new LambdaQueryWrapper<MediaProduct>()
                        .eq(MediaProduct::getId, id)
                        .eq(MediaProduct::getIsDelete, 0)
                        .eq(MediaProduct::getCreateBy, userId)
        );

        if (product == null) {
            throw new ServiceException("产品不存在!");
        }

        MediaProductVo vo = new MediaProductVo();
        BeanUtils.copyProperties(product, vo);

        // 用户绑定的平台关系
        List<UserPlatform> userPlatformList = userPlatformMapper.selectList(
                new LambdaQueryWrapper<UserPlatform>()
                        .eq(UserPlatform::getUserId, userId)
        );

        if (userPlatformList == null || userPlatformList.isEmpty()) {
            vo.setUserPlatformList(Collections.emptyList());
            return vo;
        }

        // 用户拥有的平台ID
        List<Long> userPlatformIds = userPlatformList.stream()
                .map(UserPlatform::getPlatformId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (userPlatformIds.isEmpty()) {
            vo.setUserPlatformList(Collections.emptyList());
            return vo;
        }

        // “当前产品下”且“当前用户可见”的平台
        List<MediaPlatform> authPlatformList = mediaPlatformMapper.selectList(
                new LambdaQueryWrapper<MediaPlatform>()
                        .eq(MediaPlatform::getProductId, product.getId())
                        .in(MediaPlatform::getId, userPlatformIds)
        );

        if (authPlatformList == null || authPlatformList.isEmpty()) {
            vo.setUserPlatformList(Collections.emptyList());
            return vo;
        }

        Map<Long, MediaPlatform> platformMap = authPlatformList.stream()
                .collect(Collectors.toMap(MediaPlatform::getId, Function.identity()));

        List<UserPlatformVo> userPlatformVoList = userPlatformList.stream()
                .filter(item -> item.getPlatformId() != null && platformMap.containsKey(item.getPlatformId()))
                .map(item -> {
                    UserPlatformVo upVo = new UserPlatformVo();
                    BeanUtils.copyProperties(item, upVo);
                    upVo.setMediaPlatform(platformMap.get(item.getPlatformId()));
                    return upVo;
                })
                .collect(Collectors.toList());

        vo.setUserPlatformList(userPlatformVoList);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(List<Long> ids) {
        // todo 产品下有平台, 则无法删除, 需要先删除所有的平台, 才能删除 产品
        LambdaQueryWrapper<MediaPlatform> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MediaPlatform::getIsDelete, 0);
        queryWrapper.in(MediaPlatform::getProductId, ids);
        if (mediaPlatformMapper.selectCount(queryWrapper) == 0) {
            UpdateWrapper<MediaProduct> query = new UpdateWrapper<>();
            query.set("is_delete", 1);
            query.in("id", ids);
            return mediaProductsMapper.update(null, query) > 0;
        }else {
            throw new ServiceException("待删除的产品中存在关联平台，无法删除");
        }
    }
}
