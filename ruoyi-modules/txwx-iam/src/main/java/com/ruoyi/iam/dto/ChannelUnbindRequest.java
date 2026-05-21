package com.ruoyi.iam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 通道解绑请求
 *
 * @author txwx
 */
@Data
@Schema(description = "通道解绑请求对象")
public class ChannelUnbindRequest implements CaptchaRequest
{
    @Schema(description = "通道类型：phone / email")
    private String channelType;

    @Schema(description = "验证码")
    private String verifyCode;

    @Schema(description = "解绑邮箱时需要传入登录密码（双验证）")
    private String password;

    @Schema(description = "图形验证码UUID（由 /auth/v1/code 接口返回）")
    private String uuid;

    @Schema(description = "图形验证码")
    private String code;
}
