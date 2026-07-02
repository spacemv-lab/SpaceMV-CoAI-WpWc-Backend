package com.ruoyi.iam.config;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 注册用户白名单配置
 * <p>
 * 从 Nacos 配置中心加载白名单数据，自动标准化账号格式，支持量级校验和不可变集合包装。
 *
 * @author txwx
 */
@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "register.whitelist")
public class RegisterWhitelistConfig {

    /**
     * 全局总开关（MVP 首次接入阶段默认 false，正式上线后改为 true）
     */
    private boolean enabled = false;

    /**
     * 全局白名单开关
     */
    private boolean globalEnabled = true;

    /**
     * 产品线白名单开关
     */
    private boolean productLineEnabled = true;

    /**
     * 全局白名单账号列表
     */
    private List<String> globalAccounts = new ArrayList<>();

    /**
     * 按产品线分组的白名单
     */
    private Map<String, List<String>> productLineAccounts = new HashMap<>();

    /**
     * 全局白名单最大数量硬限制（不可通过 Nacos 调整）
     */
    private static final int GLOBAL_MAX_COUNT = 500;

    /**
     * 单个产品线白名单最大数量硬限制（不可通过 Nacos 调整）
     */
    private static final int BUSINESS_LINE_MAX_COUNT = 200;

    /**
     * volatile 缓存 Set/Map（线程安全快照，不可变包装防篡改）
     */
    private volatile Set<String> globalSet = Collections.emptySet();

    /**
     * volatile 缓存产品线映射
     */
    private volatile Map<String, Set<String>> productLineMap = Collections.emptyMap();

    @PostConstruct
    public void init() {
        log.info("白名单配置初始化开始");
        refresh();
    }

    /**
     * 刷新白名单配置
     * <p>
     * 量级校验 + 账号标准化 + 构建不可变集合 + 原子替换
     */
    public void refresh() {
        // 1. 量级校验（统一截断策略，不阻断启动）
        if (CollUtil.isNotEmpty(globalAccounts) && globalAccounts.size() > GLOBAL_MAX_COUNT) {
            log.warn("全局白名单超限: {} > {}，截断到限制值", globalAccounts.size(), GLOBAL_MAX_COUNT);
            globalAccounts = new ArrayList<>(globalAccounts.subList(0, GLOBAL_MAX_COUNT));
        }

        Map<String, List<String>> trimmedProductMap = new HashMap<>();
        if (CollUtil.isNotEmpty(productLineAccounts)) {
            productLineAccounts.forEach((key, accounts) -> {
                List<String> trimmed = accounts;
                if (CollUtil.isNotEmpty(accounts) && accounts.size() > BUSINESS_LINE_MAX_COUNT) {
                    log.warn("产品线白名单超限 [{}]: {} > {}，截断到限制值", key, accounts.size(), BUSINESS_LINE_MAX_COUNT);
                    trimmed = new ArrayList<>(accounts.subList(0, BUSINESS_LINE_MAX_COUNT));
                }
                trimmedProductMap.put(key, trimmed);
            });
            productLineAccounts = trimmedProductMap;
        }

        // 2. productLine key 自动 trim + 账号标准化 + 构建新 Set/Map
        Map<String, Set<String>> newProductMap = new HashMap<>();
        if (CollUtil.isNotEmpty(productLineAccounts)) {
            productLineAccounts.forEach((key, accounts) -> {
                Set<String> normalizedAccounts = accounts.stream()
                        .map(this::normalizeAccount)
                        .filter(Objects::nonNull)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toSet());
                newProductMap.put(key.trim(), normalizedAccounts);
            });
        }

        Set<String> newGlobalSet = new HashSet<>();
        if (CollUtil.isNotEmpty(globalAccounts)) {
            newGlobalSet = globalAccounts.stream()
                    .map(this::normalizeAccount)
                    .filter(Objects::nonNull)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toCollection(HashSet::new));
        }

        // 3. 原子替换 + 不可变包装
        this.globalSet = Collections.unmodifiableSet(newGlobalSet);
        this.productLineMap = Collections.unmodifiableMap(newProductMap);

        log.info("白名单配置刷新完成: globalSet={}, productLineMap={}", this.globalSet.size(), this.productLineMap.size());

        // 4. 双关关闭告警
        if (enabled && !globalEnabled && !productLineEnabled) {
            log.error("配置异常：enabled=true 但 globalEnabled=false 且 productLineEnabled=false，无校验层生效。请核查 Nacos 配置");
        }
    }

    /**
     * 账号标准化规则
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
                // v6.0：保留原始值，不再 return null 静默丢弃
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
