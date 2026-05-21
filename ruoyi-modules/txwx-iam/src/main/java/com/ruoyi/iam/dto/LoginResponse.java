package com.ruoyi.iam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录响应
 *
 * @author txwx
 */
@Data
@Schema(description = "登录响应信息对象")
public class LoginResponse
{
    @Schema(description = "访问令牌（JWT）")
    private String accessToken;

    @Schema(description = "刷新令牌")
    private String refreshToken;

    @Schema(description = "用户 ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "产品线标识")
    private String productLine;

    @Schema(description = "令牌过期时间（秒）")
    private long expiresIn;
}
