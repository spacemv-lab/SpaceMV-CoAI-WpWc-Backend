/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

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
import com.txwx.social.crm.util.AccountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 用户注册扩展表服务实现类
 *
 * @author txwx
 * @date 2026-04-20
 */
@Service
@Slf4j
public class TxwxUserRegisterServiceImpl extends ServiceImpl<TxwxUserRegisterMapper, TxwxUserRegisterPO> implements ITxwxUserRegisterService {

    @Autowired
    private TxwxUserRegisterMapper userRegisterMapper;

    @Override
    public TxwxUserRegisterPO selectByUserId(Long userId) {
        LambdaQueryWrapper<TxwxUserRegisterPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TxwxUserRegisterPO::getUserId, userId)
                .eq(TxwxUserRegisterPO::getDelFlag, "0");
        return userRegisterMapper.selectOne(wrapper);
    }

    @Override
    public TxwxUserRegisterPO selectByRegisterAccount(String registerAccount) {
        int type = AccountUtil.getAccountType(registerAccount);
        LambdaQueryWrapper<TxwxUserRegisterPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
                TxwxUserRegisterPO::getUserName,
                TxwxUserRegisterPO::getBindPhone,
                TxwxUserRegisterPO::getBindEmail
        );
        switch (type) {
            case 1 -> wrapper.eq(TxwxUserRegisterPO::getBindPhone, registerAccount);
            case 2-> wrapper.eq(TxwxUserRegisterPO::getBindEmail, registerAccount);
            default -> wrapper.eq(TxwxUserRegisterPO::getUserName, registerAccount);
        }

        wrapper.eq(TxwxUserRegisterPO::getDelFlag, "0");
        return userRegisterMapper.selectOne(wrapper);
    }

    @Override
    public TxwxUserRegisterPO selectByUserName(String userName) {
        LambdaQueryWrapper<TxwxUserRegisterPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
                TxwxUserRegisterPO::getUserName
        );
        wrapper.eq(TxwxUserRegisterPO::getUserName, userName)
                .eq(TxwxUserRegisterPO::getDelFlag, "0");
        return userRegisterMapper.selectOne(wrapper);
    }

    @Override
    public boolean updateStatus(Long registerId, String status, String updateBy) {
        TxwxUserRegisterPO entity = new TxwxUserRegisterPO();
        entity.setRegisterId(registerId);
        entity.setStatus(status);
        entity.setUpdateBy(updateBy);
        entity.setUpdateTime(new Date());
        return updateById(entity);
    }

    @Override
    public List<TxwxUserRegisterPO> selectList(TxwxUserRegisterPO query) {
        LambdaQueryWrapper<TxwxUserRegisterPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getDelFlag() != null, TxwxUserRegisterPO::getDelFlag, query.getDelFlag());
        wrapper.eq(query.getUserId() != null, TxwxUserRegisterPO::getUserId, query.getUserId());
        wrapper.eq(query.getUserName() != null && !query.getUserName().isEmpty(), TxwxUserRegisterPO::getUserName, query.getUserName());
        wrapper.eq(query.getBindPhone() != null && !query.getBindPhone().isEmpty(), TxwxUserRegisterPO::getBindPhone, query.getBindPhone());
        wrapper.eq(query.getBindEmail() != null && !query.getBindEmail().isEmpty(), TxwxUserRegisterPO::getBindEmail, query.getBindEmail());
        wrapper.eq(query.getStatus() != null && !query.getStatus().isEmpty(), TxwxUserRegisterPO::getStatus, query.getStatus());
        wrapper.orderByDesc(TxwxUserRegisterPO::getRegisterTime);
        return userRegisterMapper.selectList(wrapper);
    }
}
