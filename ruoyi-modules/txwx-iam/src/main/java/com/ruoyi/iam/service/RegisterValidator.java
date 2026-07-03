package com.ruoyi.iam.service;

import com.ruoyi.iam.config.RegisterWhitelistConfig;
import com.ruoyi.iam.dto.WhitelistCheckResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

/**
 * 白名单校验引擎
 * <p>
 * 核心校验逻辑，返回 WhitelistCheckResult 包含命中层级信息。
 * 三层优先级：enabled > globalEnabled > productLineEnabled
 *
 * @author txwx
 */
@Service
@Slf4j
public class RegisterValidator {

    private final RegisterWhitelistConfig config;

    public RegisterValidator(RegisterWhitelistConfig config) {
        this.config = config;
    }

    /**
     * 校验注册（简化版本）
     *
     * @param account 账号
     * @return WhitelistCheckResult（含 allowed + reason）
     */
    public WhitelistCheckResult canRegister(String account) {
        return canRegister(account, null);
    }

    /**
     * 校验注册（完整版）
     * <p>
     * 校验优先级：
     * 1. enabled（P1 总开关）
     * 2. 双关关闭兜底（ERROR 日志 + 放行）
     * 3. productLine 空值处理（v6.0：必须指定产品线）
     * 4. 全局白名单（P2）
     * 5. 产品线白名单（P3）
     * 6. 默认拒绝
     *
     * @param account      账号
     * @param productLine 产品线
     * @return 校验结果
     */
    public WhitelistCheckResult canRegister(String account, String productLine) {
        // 1. enabled（P1 总开关）
        if (!config.isEnabled()) {
            return WhitelistCheckResult.allowed("CONFIG_DISABLED");
        }

        // 2. 双关关闭兜底（ERROR 日志 + 放行）
        if (!config.isGlobalEnabled() && !config.isProductLineEnabled()) {
            log.error("配置异常：enabled=true 但 globalEnabled=false 且 productLineEnabled=false，视为全放行");
            return WhitelistCheckResult.allowed("CONFIG_ABNORMAL");
        }

        // 3. productLine 空值处理（v6.0 变更：必须指定产品线）
        String normalizedProductLine = productLine != null ? productLine.trim() : null;
        if (normalizedProductLine == null || normalizedProductLine.isEmpty()) {
            log.warn("注册请求缺少产品线参数: account={}", account);
            return WhitelistCheckResult.denied("MISSING_PRODUCT_LINE");
        }

        // 4. 全局白名单（P2）
        if (config.isGlobalEnabled()) {
            String normalizedAccount = normalizeAccount(account);
            Set<String> globalSet = config.getGlobalSet();
            if (globalSet.contains(normalizedAccount)) {
                return WhitelistCheckResult.allowed("GLOBAL_WHITELIST");
            }
        }

        // 5. 产品线白名单（P3）
        if (config.isProductLineEnabled()) {
            Set<String> lineSet = config.getProductLineMap().get(normalizedProductLine);
            if (lineSet == null) {
                return WhitelistCheckResult.allowed("PRODUCT_LINE_WHITELIST");
            }
            if (lineSet.contains(normalizeAccount(account))) {
                return WhitelistCheckResult.allowed("PRODUCT_LINE_WHITELIST");
            }
        }

        // 6. 未命中 → 拒绝
        return WhitelistCheckResult.denied("DENIED");
    }

    /**
     * 账号标准化
     *
     * @param account 原始账号
     * @return 标准化后的账号
     */
    private String normalizeAccount(String account) {
        if (account == null) {
            return null;
        }
        String trimmed = account.trim().toLowerCase();

        // 邮箱格式校验
        if (trimmed.contains("@")) {
            if (!trimmed.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")) {
                log.error("邮箱格式非法已丢弃: {}，该账号不会进入白名单", account);
                // v6.0：保留原始值
                return trimmed;
            }
            return trimmed;
        }

        // 手机号去格式
        trimmed = trimmed.replaceAll("\\D", "");
        if (trimmed.length() >= 13 && trimmed.startsWith("86")) {
            trimmed = trimmed.substring(2);
        }
        return trimmed;
    }
}
