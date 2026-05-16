/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 用户注册扩展表实体
 *
 * @author txwx
 * @date 2026-04-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "用户注册扩展表")
@TableName("txwx_user_register")
public class TxwxUserRegisterPO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "注册ID")
    @TableId(type = IdType.AUTO)
    private Long registerId;

    @Schema(description = "关联sys_user用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "绑定手机号")
    private String bindPhone;

    @Schema(description = "绑定邮箱")
    private String bindEmail;

    @Schema(description = "备用手机号")
    private String bakPhone;

    @Schema(description = "备用邮箱")
    private String bakEmail;

    @Schema(description = "注册时间")
    private Date registerTime;

    @Schema(description = "状态：0=正常 1=停用 2=注销中 3=已注销")
    private String status;

    @Schema(description = "删除标志(0代表存在 2代表删除)")
    private String delFlag;

    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


}
