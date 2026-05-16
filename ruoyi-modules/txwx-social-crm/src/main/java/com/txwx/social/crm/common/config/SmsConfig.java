/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.common.config;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class SmsConfig {
    /**
     * 资源映射路径 前缀
     */
    @Value("${txwx.sms.aliyun.ALIBABA_CLOUD_ACCESS_KEY_ID}")
    public String accessKeyID;

    @Value("${txwx.sms.aliyun.ALIBABA_CLOUD_ACCESS_KEY_SECRET}")
    private String secret;

    /**
     * 阿里云短信服务的端点
     */
    @Value("${txwx.sms.aliyun.endpoint:dysmsapi.aliyuncs.com}")
    private String endpoint;

    /**
     * 阿里云区域ID
     */
    @Value("${txwx.sms.aliyun.region-id:cn-hangzhou}")
    private String regionId;

    /**
     * 创建阿里云短信客户端
     * 这个Bean会在Spring容器初始化时自动加载，且默认是Singleton（单例）
     *
     * @return 阿里云短信客户端实例
     * @throws Exception 创建客户端异常
     */
    @Bean
    public Client aliyunSmsClient() throws Exception {
        Config config = new Config()
                .setAccessKeyId(accessKeyID)
                .setAccessKeySecret(secret)
                .setEndpoint(endpoint)
                .setRegionId(regionId);

        return new Client(config);
    }

}
