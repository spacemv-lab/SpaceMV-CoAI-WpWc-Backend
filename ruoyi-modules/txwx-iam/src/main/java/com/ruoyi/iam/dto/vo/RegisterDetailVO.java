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
 * 注册用户详情响应 VO
 * <p>
 * 与 RegisterListVO 结构一致，前端协议保持相同字段
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "注册用户详情VO")
public class RegisterDetailVO {

    @Schema(description = "注册ID（即用户ID）")
    private Long registerId;

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

    @Schema(description = "状态：0=正常 1=停用 2=注销中 3=已注销")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "注册时间")
    private Date registerTime;
}
