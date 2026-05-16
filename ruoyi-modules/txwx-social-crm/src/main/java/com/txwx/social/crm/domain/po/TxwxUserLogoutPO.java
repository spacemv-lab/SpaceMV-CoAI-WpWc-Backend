/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
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

/**
 * 账号注销申请表实体
 *
 * @author txwx
 * @date 2026-04-20
 */
@Data
@Accessors(chain = true)
@Schema(description = "账号注销申请表")
@TableName("txwx_user_logout")
public class TxwxUserLogoutPO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "注销ID")
    @TableId(type = IdType.AUTO)
    private Long logoutId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "申请时间")
    private Date applyTime;

    @Schema(description = "冷却结束时间")
    private Date coolEndTime;

    @Schema(description = "状态：0=冷却中 1=已注销 2=已撤销")
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
