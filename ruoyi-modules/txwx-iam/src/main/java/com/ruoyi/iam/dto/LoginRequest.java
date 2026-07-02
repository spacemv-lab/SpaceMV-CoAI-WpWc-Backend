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
 * 登录请求
 *
 * @author txwx
 */
@Data
@Schema(description = "登录信息请求对象")
public class LoginRequest implements CaptchaRequest
{
    @Schema(description = "账号（手机号/邮箱/用户名）")
    private String channelAccount;

    @Schema(description = "凭证（密码 or 短信验证码）")
    private String credential;

    @Schema(description = "图形验证码UUID（由 /code 接口返回）")
    private String uuid;

    @Schema(description = "图形验证码")
    private String code;

    @Schema(description = "产品线标识（不传默认 spacemv-coai）")
    private String productLine;

    @Schema(description = "登录类型：password / sms")
    private String loginType;
}
