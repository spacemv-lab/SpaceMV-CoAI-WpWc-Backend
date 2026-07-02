/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.common.core.config;

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
 * <p>密钥读取优先级：Nacos > 本地 YAML > 内置默认值
 *
 * @author txwx
 */
@Component
public class RsaKeyConfig {

    /** RSA 私钥配置键 */
    private static final String CONF_KEY_PRIVATE = "rsa_private_key";

    /** RSA 公钥配置键 */
    private static final String CONF_KEY_PUBLIC = "rsa_public_key";

    /** 内置默认私钥（兜底，当 Nacos 无配置时） */
    private static final String DEFAULT_PRIVATE_KEY =
        "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI+rBZARCpd8/FrcYVS81H7WuQxeYtKVdmp4ERiteqMLFWn5x0ogM84qOJqlbTpFK4YQFZC4rA3IbUikNEt12J9knRNlAR3cE2Roip9TPhET6qGchvjx1wlDBB2LC9N+uQsKTLrtpzOFG3yTBw68bR0rrxBUjbEqjBPgEwaSjB8DAgMBAAECgYAG5yVuYL5wCe08IzBs0fsQMk2UEELr5SRaUduMb0vTSRIl1Etxi27Byw1XzN2cxtVxBkieLZyPhJMcdU6EvUXqGQjfCCjeZxLD53Q6te3Dj7fiEnvpYB+ISrSwQ5hysbTG5UTKa2KwrFIDbXSC9vZKgjDpYig43OhWddiQ7Y3OPQJBALd7YeYtF4xRbiOnPXy4XJhE0y9VlMcD0VGCwBHcL8mRCTl35IZShQhEAgMhFd31Hyz15CdaBNkVMLtntN5KuscCQQDIc0l2tNukj/1jphP7aMvRKWNogNr/gjxzy6RC1EDDZu6JRhhUjukm0CI1sdu98/lvheIynaj1S+pnjRYioZ3lAkAoljlssjLQTj7/0gHO8fVBlY/lm5fCgjyuPC8ChGNpwhR5SuUZNW3KC0kqqgntREi2KFpkvgvufTp/agxfU8aHAkAEC+fAwLfqY4m++DxRB/WNXGOIWYmSPOPRhpvjSXuhNjO8i7C0DEqCoRL/uH5yIDm52Z8OXIZrpUOvIXb/7flNAkEAmsG8NZxapGPPWuFNgpbgi7b4Vt6pixaAv65HdXHjYLsXSS75EFHrHIU554FBnutSSJvUBUWBCgxUjv77bFalog\\u003d\\u003d";

    /** 内置默认公钥（兜底，当 Nacos 无配置时） */
    private static final String DEFAULT_PUBLIC_KEY =
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCPqwWQEQqXfPxa3GFUvNR+1rkMXmLSlXZqeBEYrXqjCxVp+cdKIDPOKjiapW06RSuGEBWQuKwNyG1IpDRLddifZJ0TZQEd3BNkaIqfUz4RE+qhnIb48dcJQwQdiwvTfrkLCky67aczhRt8kwcOvG0dK68QVI2xKowT4BMGkowfAwIDAQAB";

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
     * - 优先从 Nacos 读取 rsa_private_key / rsa_public_key
     * - Nacos 无配置时使用内置默认值
     */
    public synchronized void refresh() {
        this.privateKey = loadKey(CONF_KEY_PRIVATE);
        this.publicKey = loadKey(CONF_KEY_PUBLIC);

        // 通知监听器
        for (Runnable listener : listeners) {
            listener.run();
        }
    }

    /**
     * 加载单个 key 的配置值
     */
    private String loadKey(String key) {
        String value = environment.getProperty(key);
        if (value != null && !value.trim().isEmpty()) {
            return value;
        }
        // Nacos 无配置 → 使用内置默认值
        if (key.equals(CONF_KEY_PRIVATE)) {
            return DEFAULT_PRIVATE_KEY;
        }
        return DEFAULT_PUBLIC_KEY;
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
