/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.mapper;

import com.txwx.social.crm.domain.po.TxwxUserPermissionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户协作权限分配Mapper
 *
 * @author txwx
 * @date 2026-04-03
 */
@Mapper
public interface TxwxUserPermissionMapper {

    /**
     * 查询权限列表
     *
     * @param permission 权限参数
     * @return 权限列表
     */
    List<TxwxUserPermissionPO> selectPermissionList(TxwxUserPermissionPO permission);

    /**
     * 查询权限详情
     *
     * @param id 主键ID
     * @return 权限详情
     */
    TxwxUserPermissionPO selectPermissionById(@Param("id") Long id);

    /**
     * 新增权限
     *
     * @param permission 权限信息
     * @return 结果
     */
    int insertPermission(TxwxUserPermissionPO permission);

    /**
     * 修改权限
     *
     * @param permission 权限信息
     * @return 结果
     */
    int updatePermission(TxwxUserPermissionPO permission);

    /**
     * 删除权限
     *
     * @param ids 主键ID列表
     * @return 结果
     */
    int deletePermissionByIds(@Param("ids") List<Long> ids);

    /**
     * 根据用户ID查询权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<TxwxUserPermissionPO> selectPermissionByUserId(@Param("userId") Long userId);

    /**
     * 删除用户的所有权限
     *
     * @param userId 用户ID
     * @return 结果
     */
    int deletePermissionByUserId(@Param("userId") Long userId);
}
