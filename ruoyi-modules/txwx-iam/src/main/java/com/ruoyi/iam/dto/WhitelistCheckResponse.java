package com.ruoyi.iam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 白名单校验响应 DTO
 *
 * @author txwx
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "白名单校验响应对象")
public class WhitelistCheckResponse {

    @Schema(description = "是否允许注册")
    private boolean allowed;

    @Schema(description = "原因：GLOBAL_WHITELIST / PRODUCT_LINE_WHITELIST / DENIED / RATE_LIMITED / CONFIG_DISABLED / CONFIG_ABNORMAL / MISSING_PRODUCT_LINE")
    private String reason;

    @Schema(description = "请求的产品线")
    private String productLine;

    @Schema(description = "前端提示信息")
    private String message;
}
