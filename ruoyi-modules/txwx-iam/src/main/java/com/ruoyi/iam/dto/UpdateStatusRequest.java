/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 更新状态请求
 */
@Data
public class UpdateStatusRequest {
    @NotNull(message = "注册ID不能为空")
    private Long registerId;

    @NotBlank(message = "状态不能为空")
    private String status;

    @NotBlank(message = "更新人不能为空")
    private String updateBy;
}
