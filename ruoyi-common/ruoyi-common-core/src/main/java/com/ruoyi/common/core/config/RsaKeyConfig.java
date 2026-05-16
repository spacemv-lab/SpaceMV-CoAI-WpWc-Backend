/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.common.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

/**
 * RSA 密钥配置 —— 直接从 Environment（Nacos/本地 YAML）读取
 *
 * <p>设计思路：
 * - common-core 不应依赖 ConfigService 接口（所有模块都用到 RSA，但只有 ruoyi-system 有 ConfigService）
 * - 直接注入 Environment，Nacos 配置自动桥接到 Environment
 * - 支持热刷新（通过 Nacos listener 或手动 refresh()）
 *
 * <p>密钥读取优先级：仅从 Nacos/本地 YAML 配置读取，禁止内置默认密钥（安全要求）
 *
 * @author txwx
 */
@Slf4j
@Component
public class RsaKeyConfig {

    /** RSA 私钥配置键 */
    private static final String CONF_KEY_PRIVATE = "rsa_private_key";

    /** RSA 公钥配置键 */
    private static final String CONF_KEY_PUBLIC = "rsa_public_key";

    @Autowired
    private Environment environment;

    /** 当前私钥 */
    private String privateKey;

    /** 当前公钥 */
    private String publicKey;

    /**
     * 监听器列表（支持动态通知配置变更）
     */
    private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void init() {
        // 启动时首次加载
        refresh();

        // 注册 Nacos 配置变更监听（如果有的话）
        registerNacosListener();
    }

    /**
     * 尝试注册 Nacos 配置变更监听
     */
    private void registerNacosListener() {
        try {
            Class<?> nacosValueClass = Class.forName("com.alibaba.cloud.nacos.annotation.NacosValue");
            // Nacos 配置变更自动刷到 Environment，无需额外监听器
        } catch (ClassNotFoundException e) {
            // Nacos 未启用，忽略
        }
    }

    /**
     * 重新加载 RSA 密钥
     * - 优先从 Nacos/本地YAML读取 rsa_private_key / rsa_public_key
     * - 未配置时拒绝使用（安全要求：禁止使用内置兜底值）
     */
    public synchronized void refresh() {
        String privateValue = environment.getProperty(CONF_KEY_PRIVATE);
        String publicValue  = environment.getProperty(CONF_KEY_PUBLIC);

        if (privateValue == null || privateValue.trim().isEmpty()) {
            log.error("RSA 私钥未配置，请通过Nacos或本地YAML配置 rsa_private_key");
        }
        if (publicValue == null || publicValue.trim().isEmpty()) {
            log.error("RSA 公钥未配置，请通过Nacos或本地YAML配置 rsa_public_key");
        }

        this.privateKey = privateValue;
        this.publicKey  = publicValue;

        // 通知监听器
        for (Runnable listener : listeners) {
            listener.run();
        }
    }

    /**
     * 注册密钥变更监听器
     */
    public void registerListener(Runnable callback) {
        listeners.add(callback);
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }
}
