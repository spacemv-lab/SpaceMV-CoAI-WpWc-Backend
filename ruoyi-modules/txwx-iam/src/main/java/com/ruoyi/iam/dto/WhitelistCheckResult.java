package com.ruoyi.iam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 白名单校验结果（Validator 返回的中间结果 DTO）
 *
 * @author txwx
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhitelistCheckResult {

    /**
     * 是否通过白名单校验
     */
    private boolean allowed;

    /**
     * 命中原因
     * GLOBAL_WHITELIST / PRODUCT_LINE_WHITELIST / DENIED / CONFIG_DISABLED / CONFIG_ABNORMAL / MISSING_PRODUCT_LINE
     */
    private String reason;

    /**
     * 创建通过的校验结果
     */
    public static WhitelistCheckResult allowed(String reason) {
        return new WhitelistCheckResult(true, reason);
    }

    /**
     * 创建拒绝的校验结果
     */
    public static WhitelistCheckResult denied(String reason) {
        return new WhitelistCheckResult(false, reason);
    }
}
