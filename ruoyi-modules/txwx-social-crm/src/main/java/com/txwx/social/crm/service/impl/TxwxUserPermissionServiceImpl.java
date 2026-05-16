/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.service.impl;

import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.crm.domain.po.TxwxUserPermissionPO;
import com.txwx.social.crm.mapper.TxwxUserPermissionMapper;
import com.txwx.social.crm.service.ITxwxUserPermissionService;
import com.txwx.social.crm.util.EntityConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 用户协作权限分配服务实现类
 *
 * @author txwx
 * @date 2026-04-03
 */
@Service
public class TxwxUserPermissionServiceImpl implements ITxwxUserPermissionService {

    @Autowired
    private TxwxUserPermissionMapper userPermissionMapper;

    @Override
    public List<TxwxUserPermissionPO> selectPermissionList(TxwxUserPermissionPO permission) {
        return userPermissionMapper.selectPermissionList(permission);
    }

    @Override
    public TxwxUserPermissionPO selectPermissionById(Long id) {
        return userPermissionMapper.selectPermissionById(id);
    }

    @Override
    public int insertPermission(TxwxUserPermissionPO permission) {
        return userPermissionMapper.insertPermission(permission);
    }

    @Override
    public int updatePermission(TxwxUserPermissionPO permission) {
        return userPermissionMapper.updatePermission(permission);
    }

    @Override
    public int deletePermissionByIds(List<Long> ids) {
        return userPermissionMapper.deletePermissionByIds(ids);
    }

    @Override
    public List<TxwxUserPermissionPO> selectPermissionByUserId(Long userId) {
        return userPermissionMapper.selectPermissionByUserId(userId);
    }

    @Override
    public List<TxwxUserPermissionPO> selectPermissionByUserIdAndRelType(Long userId, Integer relType) {
        List<TxwxUserPermissionPO> permissionPOS = selectPermissionByUserId(userId);
        return permissionPOS.stream().filter(po -> Objects.equals(po.getRelationType(), relType)).toList();
    }

    @Override
    public int deletePermissionByUserId(Long userId) {
        return userPermissionMapper.deletePermissionByUserId(userId);
    }


    @Override
    public int deleteUserPermissionByProductId(Long productId) {
        List<TxwxUserPermissionPO>  userPermissionPOList =
                this.selectPermissionByUserId(SecurityUtils.getUserId());
        if (!CollectionUtils.isEmpty(userPermissionPOList)) {
            Optional<TxwxUserPermissionPO> userPermissionPOOpt = userPermissionPOList.stream().filter(po -> {
                return po.getRelationType() == 1;
            }).findAny();
            if (userPermissionPOOpt.isPresent()) {
                TxwxUserPermissionPO curUserPermissionPO = userPermissionPOOpt.get();
                String relIds = curUserPermissionPO.getRelationIds();
                List<Long> relList = EntityConvertor.convertToLongList(relIds);
                relList.remove(productId);
                curUserPermissionPO.setRelationIds(EntityConvertor.convertToString(relList));
                return this.updatePermission(curUserPermissionPO);
            }
        }
        return 0;
    }
}
