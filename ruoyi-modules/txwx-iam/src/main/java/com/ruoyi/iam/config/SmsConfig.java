/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.config;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云短信客户端配置
 *
 * @author txwx
 */
@Configuration
@Data
public class SmsConfig
{
    @Value("${txwx.sms.aliyun.ALIBABA_CLOUD_ACCESS_KEY_ID}")
    private String accessKeyID;

    @Value("${txwx.sms.aliyun.ALIBABA_CLOUD_ACCESS_KEY_SECRET}")
    private String secret;

    @Value("${txwx.sms.aliyun.endpoint:dysmsapi.aliyuncs.com}")
    private String endpoint;

    @Value("${txwx.sms.aliyun.region-id:cn-hangzhou}")
    private String regionId;

    /**
     * 创建阿里云短信客户端（单例）
     */
    @Bean
    public Client aliyunSmsClient() throws Exception
    {
        Config config = new Config()
                .setAccessKeyId(accessKeyID)
                .setAccessKeySecret(secret)
                .setEndpoint(endpoint)
                .setRegionId(regionId);
        return new Client(config);
    }
}
