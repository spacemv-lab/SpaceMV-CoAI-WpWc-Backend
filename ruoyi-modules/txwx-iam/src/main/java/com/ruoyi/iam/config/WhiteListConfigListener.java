package com.ruoyi.iam.config;

import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Nacos 白名单配置监听器
 * <p>
 * 通过 @NacosConfigListener 实现 Nacos 配置变更监听（方案 A，推荐）。
 * 当 Nacos 中 register-whitelist.yml 配置发生变化时，
 * 自动触发 refresh 回调。
 *
 * @author txwx
 */
@Component
@Slf4j
public class WhiteListConfigListener {

    private final RegisterWhitelistConfig config;

    public WhiteListConfigListener(RegisterWhitelistConfig config) {
        this.config = config;
    }

    /**
     * Nacos 配置变更监听器
     * <p>
     * dataId: register-whitelist.yml（统一 data-id，Namespace 物理隔离）
     * groupId: WHITELIST_GROUP（独立 Group，不影响已有业务配置）
     *
     * @param newConfig 配置变更后的新配置内容
     * @return 配置内容（可选，用于日志记录）
     */
    @NacosConfigListener(
        dataId = "register-whitelist.yml",
        groupId = "WHITELIST_GROUP"
    )
    public String onConfigChange(String newConfig) {
        log.info("Nacos 配置变更，触发白名单刷新");
        config.refresh();

        // 双关关闭告警
        if (config.isEnabled() && !config.isGlobalEnabled() && !config.isProductLineEnabled()) {
            log.error("配置异常告警：enabled=true 但 globalEnabled=false 且 productLineEnabled=false");
            // TODO: 调用告警接口（钉钉/飞书 webhook）
        }

        return newConfig;
    }
}
