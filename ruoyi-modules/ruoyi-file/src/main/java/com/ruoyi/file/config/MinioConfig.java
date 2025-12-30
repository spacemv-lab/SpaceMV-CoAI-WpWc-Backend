package com.ruoyi.file.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.minio.MinioClient;

/**
 * Minio 配置信息
 *
 * @author ruoyi
 */
@Configuration
public class MinioConfig
{
    /**
     * 服务地址
     */
    @Value("${minio.url}")
    private String url;

    /**
     * 用户名
     */
    @Value("${minio.accessKey}")
    private String accessKey;

    /**
     * 密码
     */
    @Value("${minio.secretKey}")
    private String secretKey;

    /**
     * 存储桶名称
     */
    @Value("${minio.bucketName}")
    private String bucketName;

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getAccessKey()
    {
        return accessKey;
    }

    public void setAccessKey(String accessKey)
    {
        this.accessKey = accessKey;
    }

    public String getSecretKey()
    {
        return secretKey;
    }

    public void setSecretKey(String secretKey)
    {
        this.secretKey = secretKey;
    }

    public String getBucketName()
    {
        return bucketName;
    }

    public void setBucketName(String bucketName)
    {
        this.bucketName = bucketName;
    }

    @Bean
    public MinioClient getMinioClient()
    {
        return MinioClient.builder().endpoint(url).credentials(accessKey, secretKey).build();
    }
}
