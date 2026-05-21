package com.ruoyi.iam.service;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * VerifyCodeService 测试类
 *
 * @author txwx
 */
@DisplayName("VerifyCodeService Tests")
@ExtendWith(MockitoExtension.class)
class VerifyCodeServiceTest
{
    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOps;

    @Mock
    private Client aliyunSmsClient;

    private VerifyCodeService verifyCodeService;

    @BeforeEach
    void setUp()
    {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOps);
        verifyCodeService = new VerifyCodeService(redisTemplate, aliyunSmsClient);

        // 设置 @Value 配置属性（避免 NPE）
        ReflectionTestUtils.setField(verifyCodeService, "signName", "TXWC");
        ReflectionTestUtils.setField(verifyCodeService, "verificationCodeTemplate", "SMS_123456");
        ReflectionTestUtils.setField(verifyCodeService, "smtpHost", "smtp.test.com");
        ReflectionTestUtils.setField(verifyCodeService, "smtpPort", 465);
        ReflectionTestUtils.setField(verifyCodeService, "smtpUsername", "test@test.com");
        ReflectionTestUtils.setField(verifyCodeService, "smtpPassword", "password");
        ReflectionTestUtils.setField(verifyCodeService, "fromAlias", "TXWC官方");
        ReflectionTestUtils.setField(verifyCodeService, "emailSubject", "验证码");
        ReflectionTestUtils.setField(verifyCodeService, "emailContentTemplate", "您的验证码是：{code}");
    }

    // ==================== generateCode ====================

    @Nested
    @DisplayName("generateCode")
    class GenerateCodeTests
    {
        @Test
        @DisplayName("应生成 6 位数字")
        void shouldGenerate6DigitCode()
        {
            String code = verifyCodeService.generateCode();
            assertNotNull(code);
            assertEquals(6, code.length());
            assertTrue(code.matches("\\d{6}"));
        }

        @Test
        @DisplayName("连续生成应返回不同值")
        void shouldGenerateDifferentCodes()
        {
            String c1 = verifyCodeService.generateCode();
            String c2 = verifyCodeService.generateCode();
            assertNotEquals(c1, c2);
        }
    }

    // ==================== sendSmsCode ====================

    @Nested
    @DisplayName("sendSmsCode")
    class SendSmsCodeTests
    {
        @Test
        @DisplayName("发送验证码应成功并调用阿里云 SDK")
        void shouldSendSmsCode() throws Exception
        {
            // 模拟 Redis 锁获取成功
            when(redisTemplate.opsForValue().setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);
            // 模拟不限流
            when(redisTemplate.hasKey(anyString())).thenReturn(false);

            // 模拟阿里云 SDK 返回成功
            SendSmsResponse response = mock(SendSmsResponse.class);
            SendSmsResponseBody body = mock(SendSmsResponseBody.class);
            when(body.getCode()).thenReturn("OK");
            when(response.getBody()).thenReturn(body);
            when(aliyunSmsClient.sendSms(any(SendSmsRequest.class))).thenReturn(response);

            boolean result = verifyCodeService.sendSmsCode("13800138000");

            assertTrue(result);
            // 验证锁 key
            verify(valueOps).setIfAbsent(
                eq("iam:verify:lock:sms:13800138000"), anyString(), anyLong(), any(TimeUnit.class));
            // 验证验证码存入 Redis
            verify(valueOps).set(
                eq("iam:verify:code:sms:13800138000"), anyString(), eq(300L), eq(TimeUnit.SECONDS));
            // 验证限流 key
            verify(valueOps).set(
                eq("iam:verify:limit:sms:13800138000"), eq("1"), eq(60L), eq(TimeUnit.SECONDS));
            // 验证调用了阿里云 SDK
            verify(aliyunSmsClient).sendSms(any(SendSmsRequest.class));
        }

        @Test
        @DisplayName("阿里云 SDK 返回非 OK 时应清理 Redis 并返回 false")
        void shouldCleanupWhenAliyunReturnsError() throws Exception
        {
            when(redisTemplate.opsForValue().setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);
            when(redisTemplate.hasKey(anyString())).thenReturn(false);

            // 模拟阿里云 SDK 返回失败
            SendSmsResponse response = mock(SendSmsResponse.class);
            SendSmsResponseBody body = mock(SendSmsResponseBody.class);
            when(body.getCode()).thenReturn("isv.MOBILE_NUMBER_ILLEGAL");
            when(body.getMessage()).thenReturn("手机号码不合法");
            when(response.getBody()).thenReturn(body);
            when(aliyunSmsClient.sendSms(any(SendSmsRequest.class))).thenReturn(response);

            boolean result = verifyCodeService.sendSmsCode("13800138000");

            assertFalse(result);
            // 验证清理了验证码和限流 key
            verify(redisTemplate).delete(eq("iam:verify:code:sms:13800138000"));
            verify(redisTemplate).delete(eq("iam:verify:limit:sms:13800138000"));
        }

        @Test
        @DisplayName("阿里云 SDK 抛出异常时应清理 Redis 并返回 false")
        void shouldCleanupWhenAliyunThrowsException() throws Exception
        {
            when(redisTemplate.opsForValue().setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);
            when(redisTemplate.hasKey(anyString())).thenReturn(false);
            when(aliyunSmsClient.sendSms(any(SendSmsRequest.class)))
                .thenThrow(new RuntimeException("Connection timeout"));

            boolean result = verifyCodeService.sendSmsCode("13800138000");

            assertFalse(result);
            verify(redisTemplate).delete(eq("iam:verify:code:sms:13800138000"));
            verify(redisTemplate).delete(eq("iam:verify:limit:sms:13800138000"));
        }

        @Test
        @DisplayName("限流应拒绝发送且不调用阿里云 SDK")
        void shouldRejectWhenLimited() throws Exception
        {
            when(redisTemplate.opsForValue().setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);
            when(redisTemplate.hasKey(anyString())).thenReturn(true);

            boolean result = verifyCodeService.sendSmsCode("13800138000");

            assertFalse(result);
            verify(aliyunSmsClient, never()).sendSms(any(SendSmsRequest.class));
        }

        @Test
        @DisplayName("锁获取失败应返回 false 且不调用阿里云 SDK")
        void shouldReturnFalseWhenLockFailed() throws Exception
        {
            when(redisTemplate.opsForValue().setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                .thenReturn(false);

            boolean result = verifyCodeService.sendSmsCode("13800138000");

            assertFalse(result);
            verify(aliyunSmsClient, never()).sendSms(any(SendSmsRequest.class));
        }
    }

    // ==================== sendEmailCode ====================

    @Nested
    @DisplayName("sendEmailCode")
    class SendEmailCodeTests
    {
        @Test
        @DisplayName("发送邮件验证码应成功并调用 SMTP")
        void shouldSendEmailCode()
        {
            when(redisTemplate.opsForValue().setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);
            when(redisTemplate.hasKey(anyString())).thenReturn(false);

            try (MockedStatic<MailUtil> mailUtilMock = mockStatic(MailUtil.class))
            {
                mailUtilMock.when(() -> MailUtil.send(
                        any(MailAccount.class), anyString(), anyString(), anyString(), anyBoolean()))
                    .thenReturn(true);

                boolean result = verifyCodeService.sendEmailCode("test@txwx.com");

                assertTrue(result);
                // 验证锁 key
                verify(valueOps).setIfAbsent(
                    eq("iam:verify:lock:email:test@txwx.com"), anyString(), anyLong(), any(TimeUnit.class));
                // 验证调用了 MailUtil.send
                mailUtilMock.verify(() -> MailUtil.send(
                    any(MailAccount.class), eq("test@txwx.com"), anyString(), anyString(), eq(true)));
            }
        }

        @Test
        @DisplayName("SMTP 发送异常时应清理 Redis 并返回 false")
        void shouldCleanupWhenSmtpFails()
        {
            when(redisTemplate.opsForValue().setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);
            when(redisTemplate.hasKey(anyString())).thenReturn(false);

            try (MockedStatic<MailUtil> mailUtilMock = mockStatic(MailUtil.class))
            {
                mailUtilMock.when(() -> MailUtil.send(
                        any(MailAccount.class), anyString(), anyString(), anyString(), anyBoolean()))
                    .thenThrow(new RuntimeException("SMTP connection failed"));

                boolean result = verifyCodeService.sendEmailCode("test@txwx.com");

                assertFalse(result);
                // 验证清理了验证码和限流 key
                verify(redisTemplate).delete(eq("iam:verify:code:email:test@txwx.com"));
                verify(redisTemplate).delete(eq("iam:verify:limit:email:test@txwx.com"));
            }
        }

        @Test
        @DisplayName("邮箱限流应拒绝发送且不调用 MailUtil")
        void shouldRejectEmailWhenLimited()
        {
            when(redisTemplate.opsForValue().setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);
            when(redisTemplate.hasKey(anyString())).thenReturn(true);

            boolean result = verifyCodeService.sendEmailCode("test@txwx.com");

            assertFalse(result);
        }
    }

    // ==================== verifyCode ====================

    @Nested
    @DisplayName("verifyCode")
    class VerifyCodeTests
    {
        @Test
        @DisplayName("正确验证码应返回 true 并删除")
        void shouldVerifyAndDeleteCode()
        {
            when(redisTemplate.opsForValue().get(anyString())).thenReturn("123456");

            boolean result = verifyCodeService.verifyCode("sms", "13800138000", "123456");

            assertTrue(result);
            verify(redisTemplate).delete("iam:verify:code:sms:13800138000");
        }

        @Test
        @DisplayName("错误验证码应返回 false")
        void shouldRejectWrongCode()
        {
            when(redisTemplate.opsForValue().get(anyString())).thenReturn("123456");

            boolean result = verifyCodeService.verifyCode("sms", "13800138000", "654321");

            assertFalse(result);
            verify(redisTemplate, never()).delete(anyString());
        }

        @Test
        @DisplayName("过期验证码应返回 false")
        void shouldRejectExpiredCode()
        {
            when(redisTemplate.opsForValue().get(anyString())).thenReturn(null);

            boolean result = verifyCodeService.verifyCode("sms", "13800138000", "123456");

            assertFalse(result);
        }

        @Test
        @DisplayName("不同类型的验证码应独立")
        void shouldHaveIndependentCodeTypes()
        {
            when(redisTemplate.opsForValue().get(eq("iam:verify:code:sms:13800138000"))).thenReturn("123456");
            when(redisTemplate.opsForValue().get(eq("iam:verify:code:email:test@txwx.com"))).thenReturn("654321");

            assertTrue(verifyCodeService.verifyCode("sms", "13800138000", "123456"));
            assertTrue(verifyCodeService.verifyCode("email", "test@txwx.com", "654321"));
        }

        @Test
        @DisplayName("校验后重放应返回 false")
        void shouldRejectReplay()
        {
            when(redisTemplate.opsForValue().get(anyString())).thenReturn("123456");

            assertTrue(verifyCodeService.verifyCode("sms", "13800138000", "123456"));

            // 第 2 次：key 已被删除
            when(redisTemplate.opsForValue().get(anyString())).thenReturn(null);
            assertFalse(verifyCodeService.verifyCode("sms", "13800138000", "123456"));
        }

        @Test
        @DisplayName("空验证码应返回 false")
        void shouldRejectEmptyCode()
        {
            assertFalse(verifyCodeService.verifyCode("sms", "13800138000", ""));
        }

        @Test
        @DisplayName("空 account 应返回 false")
        void shouldRejectEmptyAccount()
        {
            assertFalse(verifyCodeService.verifyCode("sms", "", "123456"));
        }
    }
}
