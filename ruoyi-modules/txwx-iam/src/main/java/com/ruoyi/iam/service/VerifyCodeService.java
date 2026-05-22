/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.json.JSONUtil;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务
 * 存储：Redis（6位数字，TTL 300s）
 * 限流：1次/分钟/账号（基于 Redis SET NX）
 * 发送：短信通过阿里云 SDK，邮件通过 SMTP（Hutool MailUtil）
 *
 * @author txwx
 */
@Slf4j
@Service
public class VerifyCodeService
{
    private static final String CODE_KEY_PREFIX = "iam:verify:code:";
    private static final String LIMIT_KEY_PREFIX = "iam:verify:limit:";
    private static final String LOCK_KEY_PREFIX = "iam:verify:lock:";
    private static final int CODE_LENGTH = 6;
    private static final long CODE_TTL = 300; // 5 分钟
    private static final long LIMIT_TTL = 60; // 1 分钟限流
    private static final long LOCK_TTL = 5;   // 分布式锁 5 秒

    private final StringRedisTemplate redisTemplate;
    private final Client aliyunSmsClient;

    // ==================== 阿里云短信配置 ====================

    @Value("${txwx.sms.aliyun.sign-name}")
    private String signName;

    @Value("${txwx.sms.aliyun.template.verification-code}")
    private String verificationCodeTemplate;

    // ==================== SMTP 邮件配置 ====================

    @Value("${txwx.email.host:smtp.huaweicloud.com}")
    private String smtpHost;

    @Value("${txwx.email.port:465}")
    private Integer smtpPort;

    @Value("${txwx.email.username:}")
    private String smtpUsername;

    @Value("${txwx.email.password:}")
    private String smtpPassword;

    @Value("${txwx.email.from-alias:TXWC官方}")
    private String fromAlias;

    @Value("${txwx.email.subject:验证码}")
    private String emailSubject;

    @Value("${txwx.email.content-template:您的验证码是：{code}}")
    private String emailContentTemplate;

    public VerifyCodeService(StringRedisTemplate redisTemplate, Client aliyunSmsClient)
    {
        this.redisTemplate = redisTemplate;
        this.aliyunSmsClient = aliyunSmsClient;
    }

    /**
     * 生成 6 位数字验证码
     */
    public String generateCode()
    {
        return RandomUtil.randomNumbers(CODE_LENGTH);
    }

    /**
     * 根据通道类型发送验证码
     */
    public boolean sendCode(String type, String account)
    {
        if ("sms".equals(type) || "phone".equals(type))
        {
            return sendSmsCode(account);
        }
        if ("email".equals(type))
        {
            return sendEmailCode(account);
        }
        log.warn("Unsupported type: {}", type);
        return false;
    }

    /**
     * 发送验证码（短信）
     */
    public boolean sendSmsCode(String mobile)
    {
        String lockKey = LOCK_KEY_PREFIX + "sms:" + mobile;
        // 尝试获取分布式锁（SET NX + TTL）
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", LOCK_TTL, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(locked))
        {
            log.warn("Send SMS code failed, lock acquired by another instance: {}", mobile);
            return false;
        }

        try
        {
            // 检查限流
            String limitKey = LIMIT_KEY_PREFIX + "sms:" + mobile;
            if (redisTemplate.hasKey(limitKey))
            {
                log.warn("SMS code limit exceeded: {}", mobile);
                return false;
            }

            // 生成验证码
            String code = generateCode();

            // 存入 Redis（注：channelType 统一用 "phone"，与注册校验侧一致）
            String codeKey = CODE_KEY_PREFIX + "phone:" + mobile;
            redisTemplate.opsForValue().set(codeKey, code, CODE_TTL, TimeUnit.SECONDS);

            // 设置限流 key
            redisTemplate.opsForValue().set(limitKey, "1", LIMIT_TTL, TimeUnit.SECONDS);

            // 通过阿里云 SDK 发送真实短信
            try
            {
                sendSmsViaAliyun(mobile, code);
                log.info("短信发送成功: {}", mobile);
            }
            catch (Exception e)
            {
                log.error("短信发送失败: {} {}", mobile, e.getMessage());
                // 发送失败时回滚已存储的验证码和限流 key，避免无效的缓存
                redisTemplate.delete(codeKey);
                redisTemplate.delete(limitKey);
                return false;
            }

            return true;
        } finally
        {
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * 发送验证码（邮件）
     */
    public boolean sendEmailCode(String email)
    {
        String lockKey = LOCK_KEY_PREFIX + "email:" + email;
        // 尝试获取分布式锁（SET NX + TTL）
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", LOCK_TTL, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(locked))
        {
            log.warn("Send email code failed, lock acquired by another instance: {}", email);
            return false;
        }

        try
        {
            // 检查限流
            String limitKey = LIMIT_KEY_PREFIX + "email:" + email;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(limitKey)))
            {
                log.warn("Email code limit exceeded: {}", email);
                return false;
            }

            // 生成验证码
            String code = generateCode();

            // 存入 Redis
            String codeKey = CODE_KEY_PREFIX + "email:" + email;
            redisTemplate.opsForValue().set(codeKey, code, CODE_TTL, TimeUnit.SECONDS);

            // 设置限流 key
            redisTemplate.opsForValue().set(limitKey, "1", LIMIT_TTL, TimeUnit.SECONDS);

            // 通过 SMTP 发送真实邮件
            try
            {
                sendEmailViaSmtp(email, code);
                log.info("邮件发送成功: {}", email);
            }
            catch (Exception e)
            {
                log.error("邮件发送失败: {} {}", email, e.getMessage());
                // 发送失败时回滚已存储的验证码和限流 key
                redisTemplate.delete(codeKey);
                redisTemplate.delete(limitKey);
                return false;
            }

            return true;
        } finally
        {
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * 通过阿里云 SDK 发送短信验证码
     */
    private void sendSmsViaAliyun(String mobile, String code) throws Exception
    {
        Map<String, String> templateParam = new HashMap<>();
        templateParam.put("code", code);

        SendSmsRequest request = new SendSmsRequest()
                .setPhoneNumbers(mobile)
                .setSignName(signName)
                .setTemplateCode(verificationCodeTemplate)
                .setTemplateParam(JSONUtil.toJsonStr(templateParam));

        SendSmsResponse response = aliyunSmsClient.sendSms(request);

        if (response == null || response.getBody() == null || !"OK".equals(response.getBody().getCode()))
        {
            String errMsg = (response != null && response.getBody() != null)
                    ? response.getBody().getMessage() : "未知错误";
            throw new RuntimeException("短信发送失败: " + errMsg);
        }
    }

    /**
     * 通过 SMTP 发送邮件验证码（Hutool MailUtil）
     */
    private void sendEmailViaSmtp(String email, String code)
    {
        MailAccount account = new MailAccount();
        account.setHost(smtpHost);
        account.setPort(smtpPort);
        account.setAuth(true);
        account.setUser(smtpUsername);
        account.setPass(smtpPassword);
        account.setSslEnable(true);
        account.setFrom(fromAlias + " <" + smtpUsername + ">");

        String subject = emailSubject;
        String content = emailContentTemplate.replace("{code}", code);

        MailUtil.send(account, email, subject, content, true);
    }

    /**
     * 校验验证码
     * 校验成功删除验证码，防止重放
     */
    public boolean verifyCode(String type, String account, String code)
    {
        if (type == null || type.isEmpty() || account == null || account.isEmpty() || code == null || code.isEmpty())
        {
            log.warn("Invalid verify code parameters: type={}, account={}", type, account);
            return false;
        }
        String codeKey = CODE_KEY_PREFIX + type + ":" + account;
        String storedCode = redisTemplate.opsForValue().get(codeKey);

        if (storedCode == null)
        {
            log.warn("Code expired or not found: type={}, account={}", type, account);
            return false;
        }

        boolean matched = code.equals(storedCode);
        if (matched)
        {
            // 删除验证码（防重放）
            redisTemplate.delete(codeKey);
            // 同时删除限流 key（如果存在）
            String limitKey = LIMIT_KEY_PREFIX + type + ":" + account;
            redisTemplate.delete(limitKey);
        }
        return matched;
    }
}
