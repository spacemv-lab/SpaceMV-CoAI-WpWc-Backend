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
 * 认证审计日志实体
 *
 * @author txwx
 */
@Data
@TableName("iam_auth_log")
public class IamAuthLog implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID（未登录为null） */
    private Long userId;

    /** 认证通道 */
    private String channelType;

    /** login/register/refresh/logout/user_delete */
    private String authType;

    /** success/fail */
    private String authResult;

    /** 失败原因 */
    private String failReason;

    /** IP地址 */
    private String ipAddr;

    /** User-Agent */
    private String userAgent;

    /** 认证时间 */
    @TableField(fill = FieldFill.INSERT)
    private Date authTime;
}
