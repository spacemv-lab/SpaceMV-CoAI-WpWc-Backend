package com.txwx.social.crm.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.service.RedisService;
import com.txwx.social.crm.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmtpEmailServiceImpl implements EmailService {
    /**
     * 验证码过期时间（单位：分钟）
     */
    @Value("${txwx.verify.code-expire-minutes:5}")
    private Long codeExpireMinutes;

    /**
     * 频次限制过期时间（单位：秒）
     */
    @Value("${txwx.verify.limit-expire-seconds:60}")
    private Long limitExpireSeconds;

    /**
     * 邮箱验证码Redis key前缀
     */
    @Value("${txwx.verify.redis-key-prefix.email-code:txwx:verify:email:code:}")
    private String emailCodePrefix;

    /**
     * 频次限制Redis key前缀
     */
    @Value("${txwx.verify.redis-key-prefix.limit:txwx:verify:limit:}")
    private String limitPrefix;

    /**
     * SMTP服务器主机
     */
    @Value("${txwx.email.host:smtp.huaweicloud.com}")
    private String smtpHost;

    /**
     * SMTP服务器端口
     */
    @Value("${txwx.email.port:465}")
    private Integer smtpPort;

    /**
     * SMTP用户名（发件人邮箱）
     */
    @Value("${txwx.email.username:}")
    private String smtpUsername;

    /**
     * SMTP密码或授权码
     */
    @Value("${txwx.email.password:}")
    private String smtpPassword;

    /**
     * 发件人别名
     */
    @Value("${txwx.email.from-alias:TXWC官方}")
    private String fromAlias;

    /**
     * 邮件主题
     */
    @Value("${txwx.email.subject:验证码}")
    private String emailSubject;

    /**
     * 邮件内容模板
     */
    @Value("${txwx.email.content-template:您的验证码是：{code}}")
    private String emailContentTemplate;

    private final RedisService redisService;

    @Override
    public boolean sendVerifyCode(String email, String code) {
        // 1. 频次限制检查
        String limitKey = limitPrefix + email;
        if (redisService.hasKey(limitKey)) {
            log.warn("邮箱验证码发送过于频繁: {}", email);
            return false;
        }

        // 3. 存储验证码到Redis
        String codeKey = emailCodePrefix + email;
        redisService.setCacheObject(codeKey, code, codeExpireMinutes, TimeUnit.MINUTES);

        // 4. 设置频次限制Redis key（1分钟过期）
        redisService.setCacheObject(limitKey, "1", limitExpireSeconds, TimeUnit.SECONDS);

        // 5. 发送邮件（MVP阶段使用SMTP）
        try {
            sendEmailViaSmtp(email, code);
            log.info("发送邮箱验证码成功");
        } catch (Exception e) {
            log.error("发送邮箱验证码失败: 错误={}", e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * 通过SMTP发送邮箱验证码（MVP阶段实现）
     *
     * @param email 收件人邮箱
     * @param code 验证码
     */
    private void sendEmailViaSmtp(String email, String code) {
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

    @Override
    public boolean verifyCode(String email, String code) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(code)) {
            return false;
        }

        String redisKey = emailCodePrefix + email;
        String storedCode = redisService.getCacheObject(redisKey);

        if (code.equals(storedCode)) {
            // 验证成功后删除验证码，防止重复使用
            redisService.deleteObject(redisKey);
            return true;
        }

        return false;
    }
}
