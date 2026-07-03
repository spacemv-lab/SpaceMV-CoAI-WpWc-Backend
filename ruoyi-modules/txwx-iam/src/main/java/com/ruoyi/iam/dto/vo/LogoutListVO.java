/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 注销申请列表响应 VO
 * <p>
 * 保持前端协议字段名不变，底层数据从 IAM 表派生
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "注销申请列表VO")
public class LogoutListVO {

    @Schema(description = "注销ID（即用户ID）")
    private Long logoutId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名（显示名）")
    private String userName;

    @Schema(description = "绑定手机号")
    private String bindPhone;

    @Schema(description = "绑定邮箱")
    private String bindEmail;

    @Schema(description = "备用手机号")
    private String bakPhone;

    @Schema(description = "备用邮箱")
    private String bakEmail;

    @Schema(description = "申请注销时间")
    private Date applyTime;

    @Schema(description = "冷却结束时间（预计注销时间）")
    private Date coolEndTime;

    /** 0=冷却中 1=已注销（已忽略撤销状态） */
    @Schema(description = "状态：0=冷却中 1=已注销")
    private String status;
}
