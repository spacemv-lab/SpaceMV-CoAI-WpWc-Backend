package com.txwx.social.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.web.domain.BaseEntity;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.crm.domain.po.TxwxUserLogoutPO;
import com.txwx.social.crm.domain.po.TxwxUserRegisterPO;
import com.txwx.social.crm.mapper.TxwxUserLogoutMapper;
import com.txwx.social.crm.mapper.TxwxUserRegisterMapper;
import com.txwx.social.crm.service.ITxwxUserLogoutService;
import com.txwx.social.crm.service.ITxwxUserRegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 账号注销申请表服务实现类
 *
 * @author txwx
 * @date 2026-04-20
 */
@Service
@Slf4j
public class TxwxUserLogoutServiceImpl extends ServiceImpl<TxwxUserLogoutMapper, TxwxUserLogoutPO> implements ITxwxUserLogoutService {

    @Autowired
    private TxwxUserLogoutMapper userLogoutMapper;

    @Override
    public TxwxUserLogoutPO selectByUserId(Long userId) {
        LambdaQueryWrapper<TxwxUserLogoutPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TxwxUserLogoutPO::getUserId, userId)
                .eq(TxwxUserLogoutPO::getDelFlag, "0")
                .orderByDesc(TxwxUserLogoutPO::getApplyTime);
        return userLogoutMapper.selectOne(wrapper);
    }

    @Override
    public boolean updateStatus(Long logoutId, String status, String updateBy) {
        TxwxUserLogoutPO entity = new TxwxUserLogoutPO();
        entity.setLogoutId(logoutId);
        entity.setStatus(status);
        entity.setUpdateBy(updateBy);
        entity.setUpdateTime(new Date());
        return updateById(entity);
    }

    @Override
    public List<TxwxUserLogoutPO> selectCoolingCompleted() {
        LambdaQueryWrapper<TxwxUserLogoutPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TxwxUserLogoutPO::getStatus, "0")
                .le(TxwxUserLogoutPO::getCoolEndTime, new Date())
                .eq(TxwxUserLogoutPO::getDelFlag, "0");
        return userLogoutMapper.selectList(wrapper);
    }

    @Override
    public List<TxwxUserLogoutPO> selectList(TxwxUserLogoutPO query) {
        LambdaQueryWrapper<TxwxUserLogoutPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getDelFlag() != null, TxwxUserLogoutPO::getDelFlag, query.getDelFlag());
        wrapper.eq(query.getUserId() != null, TxwxUserLogoutPO::getUserId, query.getUserId());
        wrapper.eq(query.getStatus() != null && !query.getStatus().isEmpty(), TxwxUserLogoutPO::getStatus, query.getStatus());
        wrapper.orderByDesc(TxwxUserLogoutPO::getApplyTime);
        return userLogoutMapper.selectList(wrapper);
    }
}
