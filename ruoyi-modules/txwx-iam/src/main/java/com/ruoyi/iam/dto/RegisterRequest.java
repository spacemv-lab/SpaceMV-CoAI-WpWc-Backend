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
 * 注册请求
 *
 * @author txwx
 */
@Data
@Schema(description = "注册信息请求对象")
public class RegisterRequest implements CaptchaRequest
{
    @Schema(description = "账号（手机号/邮箱）")
    private String channelAccount;

    @Schema(description = "验证码")
    private String verifyCode;

    @Schema(description = "密码（8-20 位）")
    private String password;

    @Schema(description = "通道类型：phone / email")
    private String channelType;

    @Schema(description = "用户名（0-20 字符，前端优先传入；为空则自动生成）")
    private String username;

    @Schema(description = "备用手机号")
    private String bakPhone;

    @Schema(description = "备用邮箱")
    private String bakEmail;

    @Schema(description = "产品线标识（不传默认 spacemv-coai）")
    private String productLine;

    @Schema(description = "图形验证码UUID（由 /auth/v1/code 接口返回）")
    private String uuid;

    @Schema(description = "图形验证码")
    private String code;
}
