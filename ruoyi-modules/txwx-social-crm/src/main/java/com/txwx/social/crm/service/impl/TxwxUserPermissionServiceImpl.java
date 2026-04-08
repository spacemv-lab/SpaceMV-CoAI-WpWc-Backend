package com.txwx.social.crm.service.Impl;

import com.txwx.social.crm.domain.po.TxwxUserPermissionPO;
import com.txwx.social.crm.mapper.TxwxUserPermissionMapper;
import com.txwx.social.crm.service.ITxwxUserPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public int deletePermissionByUserId(Long userId) {
        return userPermissionMapper.deletePermissionByUserId(userId);
    }
}
