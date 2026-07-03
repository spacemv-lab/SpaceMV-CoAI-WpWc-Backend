/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * IAM ↔ System 用户映射实体
 * <p>
 * 维护 iam_user.id 与 sys_user.user_id 的多产品线映射关系。
 * IAM 负责认证，system 管理后台兼容记录，通过本表解耦两个模块的用户 ID。
 *
 * @author txwx
 */
@Data
@TableName("iam_user_product")
public class IamUserProduct implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 主键 ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** IAM 用户 ID（iam_user.id） */
    private Long iamUserId;

    /** 产品线标识 */
    private String productLine;

    /** 系统模块用户 ID（sys_user.user_id） */
    private Long productUserId;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /** 逻辑删除 */
    @TableLogic
    private String delFlag;
}
