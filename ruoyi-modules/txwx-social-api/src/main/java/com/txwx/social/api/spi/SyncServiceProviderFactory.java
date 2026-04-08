package com.txwx.social.api.spi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SPI 工厂类
 * 用于注册和获取 SPI 提供者
 *
 * @author txwx
 * @date 2026-04-03
 */
public class SyncServiceProviderFactory {

    private static final Map<String, ISyncServiceProvider> providers = new ConcurrentHashMap<>();

    /**
     * 注册 SPI 提供者
     *
     * @param channelType 渠道类型
     * @param provider SPI 提供者
     */
    public static void registerProvider(String channelType, ISyncServiceProvider provider) {
        providers.put(channelType, provider);
    }

    /**
     * 获取 SPI 提供者
     *
     * @param channelType 渠道类型
     * @return SPI 提供者
     */
    public static ISyncServiceProvider getProvider(String channelType) {
        return providers.get(channelType);
    }

    /**
     * 移除 SPI 提供者
     *
     * @param channelType 渠道类型
     */
    public static void removeProvider(String channelType) {
        providers.remove(channelType);
    }
}
