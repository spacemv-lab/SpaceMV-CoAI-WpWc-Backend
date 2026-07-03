/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.iam.entity.IamAuthLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 认证审计日志 Mapper 接口
 *
 * @author txwx
 */
@Mapper
public interface IamAuthLogMapper extends BaseMapper<IamAuthLog>
{
    IamAuthLog selectById(@Param("id") Long id);

    int insert(IamAuthLog log);
}
