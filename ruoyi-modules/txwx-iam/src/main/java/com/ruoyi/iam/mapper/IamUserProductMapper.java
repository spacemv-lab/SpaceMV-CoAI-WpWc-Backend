/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.iam.entity.IamUserProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * IAM ↔ System 用户映射 Mapper 接口
 *
 * @author txwx
 */
@Mapper
public interface IamUserProductMapper extends BaseMapper<IamUserProduct>
{
    /**
     * 按 IAM 用户 ID 软删（硬编码SQL绕过 @TableLogic 跳过 del_flag 的问题）
     */
    int softDeleteByIamUserId(@Param("iamUserId") Long iamUserId);
}
