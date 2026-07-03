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
 * IAM 统一用户实体
 *
 * @author txwx
 */
@Data
@TableName("iam_user")
public class IamUser implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 系统自动生成（spmv_xxx） */
    private String username;

    /** BCrypt 密码哈希 */
    private String passwordHash;

    /** 显示名 */
    private String displayName;

    /** 产品线（用于productLine级别隔离，如spacemv-coai） */
    private String productLine;

    /** 来源：register=注册导入 import=存量迁移 */
    private String userFrom;

    /** 头像URL */
    private String avatarUrl;

    /** 0=正常 1=停用 2=锁定 */
    private String status;

    /** 0=正常 1=注销申请中 2=已注销 */
    private String deleteStatus;

    /** 预计注销时间（提交后+7天） */
    private Date deleteScheduledAt;

    /** 注销申请提交时间 */
    private Date deleteApplyTime;

    /** 最后登录IP */
    private String lastLoginIp;

    /** 最后登录时间 */
    private Date lastLoginTime;

    /** 注册时间 */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /** 逻辑删除 */
    @TableLogic
    private String delFlag;
}
