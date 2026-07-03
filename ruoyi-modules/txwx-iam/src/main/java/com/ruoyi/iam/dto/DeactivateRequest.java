/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 注销申请请求
 *
 * @author txwx
 */
@Data
@Schema(description = "注销申请请求对象")
public class DeactivateRequest
{
    @Schema(description = "密码确认")
    private String password;
}
