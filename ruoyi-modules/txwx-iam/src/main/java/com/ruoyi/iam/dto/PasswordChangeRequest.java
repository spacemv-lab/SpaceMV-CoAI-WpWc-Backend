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
 * 密码修改请求
 *
 * @author txwx
 */
@Data
@Schema(description = "密码修改请求对象")
public class PasswordChangeRequest implements CaptchaRequest
{
    @Schema(description = "旧密码")
    private String oldPassword;

    @Schema(description = "新密码")
    private String newPassword;

    @Schema(description = "图形验证码UUID（由 /auth/v1/code 接口返回）")
    private String uuid;

    @Schema(description = "图形验证码")
    private String code;
}
