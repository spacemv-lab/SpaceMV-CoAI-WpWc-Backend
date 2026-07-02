package com.ruoyi.iam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token 验证响应
 * <p>
 * 供 Feign / 内部调用查询 token 有效性及用户信息。
 *
 * @author txwx
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token 验证响应")
public class TokenValidateResponse
{
    @Schema(description = "令牌是否有效")
    private Boolean valid;

    @Schema(description = "IAM 用户 ID（有效时返回）")
    private Long userId;

    @Schema(description = "用户名（有效时返回）")
    private String username;

    @Schema(description = "产品线标识（有效时返回）")
    private String productLine;
}
