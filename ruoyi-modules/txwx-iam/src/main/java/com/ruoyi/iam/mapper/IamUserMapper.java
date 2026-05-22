/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.iam.entity.IamUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户 Mapper 接口
 *
 * @author txwx
 */
@Mapper
public interface IamUserMapper extends BaseMapper<IamUser>
{
    IamUser selectById(@Param("id") Long id);

    IamUser selectByUsername(@Param("username") String username);

    int insert(IamUser user);

    int updateById(IamUser user);

    int updateLastLogin(@Param("id") Long id, @Param("ip") String ip);

    /**
     * 注销到期处理：同时更新 delete_status 和 del_flag
     * 专用于 DeactivateService.processExpiredDeactivations()
     * 不通过 updateById，避免 MyBatis-Plus @TableLogic 跳过 del_flag
     */
    int updateDeactivateExpired(@Param("id") Long id);
}
