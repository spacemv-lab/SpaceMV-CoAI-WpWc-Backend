package com.ruoyi.iam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 图形验证码校验请求
 * <p>
 * 前端调用 POST /auth/v1/checkHuman 时使用此对象。
 *
 * @author txwx
 */
@Data
@Schema(description = "图形验证码校验请求")
public class CaptchaCheckRequest
{
    @Schema(description = "用户输入的验证码")
    private String code;

    @Schema(description = "验证码 UUID（由 /auth/v1/code 接口返回）")
    private String uuid;
}
