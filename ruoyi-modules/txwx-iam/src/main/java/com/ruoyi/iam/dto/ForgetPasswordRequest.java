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
 * 忘记密码重置请求
 *
 * @author txwx
 */
@Data
@Schema(description = "忘记密码重置请求")
public class ForgetPasswordRequest {

    @Schema(description = "邮箱或手机号", required = true)
    private String channelAccount;

    @Schema(description = "验证码", required = true)
    private String verifyCode;

    @Schema(description = "新密码（RSA 加密后）", required = true)
    private String newPassword;
}
