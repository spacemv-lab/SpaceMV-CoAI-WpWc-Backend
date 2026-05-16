/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.service.RedisService;
import com.txwx.social.crm.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 阿里云短信服务实现
 *
 * @author txwx
 * @date 2026-04-21
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AliyunSmsServiceImpl implements SmsService {

    private final Client aliyunSmsClient;
    private final RedisService redisService;

    @Value("${txwx.sms.aliyun.sign-name}")
    private String signName;

    @Value("${txwx.sms.aliyun.template.verification-code}")
    private String verificationCodeTemplate;

    @Value("${txwx.sms.aliyun.template.notification}")
    private String notificationTemplate;

    // 短信发送频率限制（单位：秒）
    @Value("${verify.limit-expire-seconds:60}")
    private Long frequencyLimit;

    // 验证码有效期（单位：分钟）
    @Value("${code-expire-minutes:5}")
    private long codeExpireTime;
    /**
     * 短信验证码Redis key前缀
     */
    @Value("${txwx.verify.redis-key-prefix.sms-code:txwx:verify:sms:code:}")
    private String smsCodePrefix;

    /**
     * 频次限制Redis key前缀
     */
    @Value("${txwx.verify.redis-key-prefix.limit:txwx:verify:limit:}")
    private String limitPrefix;

    /**
     * 发送短信通用方法
     *
     * @param phoneNumber 手机号码
     * @param templateCode 模板代码
     * @param templateParam 模板参数
     * @return 发送结果
     */
    public R<String> sendSms(String phoneNumber, String templateCode, Map<String, String> templateParam) {
        try {
            // 1. 参数校验
            if (!isValidPhoneNumber(phoneNumber)) {
                return R.fail("手机号码格式不正确");
            }

            if (!StringUtils.hasText(templateCode)) {
                return R.fail("短信模板不能为空");
            }

            // 2. 频率限制检查
            R<String> frequencyCheckResult = checkSendFrequency(phoneNumber);
            if (!Constants.SUCCESS.equals(frequencyCheckResult.getCode())) {
                return frequencyCheckResult;
            }

            // 3. 构建请求
            SendSmsRequest request = new SendSmsRequest()
                    .setPhoneNumbers(phoneNumber)
                    .setSignName(signName)
                    .setTemplateCode(templateCode)
                    .setTemplateParam(JSON.toJSONString(templateParam));

            // 4. 发送短信
            long startTime = System.currentTimeMillis();
            SendSmsResponse response = aliyunSmsClient.sendSms(request);
            long endTime = System.currentTimeMillis();

            // 5. 处理响应
            if (isSuccessResponse(response)) {
                String requestId = response.getBody().getRequestId();
                String bizId = response.getBody().getBizId();

                log.info("短信发送成功");
                // 记录发送时间
                recordSendTime(phoneNumber);

                return R.ok("短信发送成功", requestId);
            } else {
                String errorCode = response.getBody().getCode();
                String errorMessage = response.getBody().getMessage();

                log.error("短信发送失败 - 模板: {}, 错误码: {}, 错误信息: {}",
                        templateCode, errorCode, errorMessage);

                return R.fail("短信发送失败: " + getErrorMessage(errorCode, errorMessage));
            }

        } catch (Exception e) {
            log.error("短信发送异常 -  模板: {}" , templateCode, e);
            return R.fail("短信发送异常: " + e.getMessage());
        }
    }

    /**
     * 发送验证码短信
     */
    public R<String> sendVerificationCode(String phoneNumber, String code) {

        Map<String, String> templateParam = new HashMap<>();
        templateParam.put("code", code);

        return sendSms(phoneNumber, verificationCodeTemplate, templateParam);
    }

    /**
     * 验证短信验证码
     */
    public boolean verifyCode(String phoneNumber, String code) {
        if (!StringUtils.hasText(phoneNumber) || !StringUtils.hasText(code)) {
            return false;
        }

        String redisKey = getSmsCodeKey(phoneNumber);
        String storedCode = redisService.getCacheObject(redisKey);

        if (code.equals(storedCode)) {
            // 验证成功后删除验证码，防止重复使用
            redisService.deleteObject(redisKey);
            return true;
        }

        return false;
    }

    /**
     * 发送通知短信
     */
    public R<String> sendNotification(String phoneNumber, Map<String, String> params) {
        return sendSms(phoneNumber, notificationTemplate, params);
    }

    /**
     * 批量发送短信（最多1000个手机号）
     */
    public Map<String, R<String>> batchSendSms(String[] phoneNumbers, String templateCode,
                                               Map<String, String> templateParam) {
        Map<String, R<String>> results = new HashMap<>();

        for (String phoneNumber : phoneNumbers) {
            R<String> result = sendSms(phoneNumber, templateCode, templateParam);
            results.put(phoneNumber, result);
        }

        return results;
    }

    // ====================== 私有方法 ======================

    /**
     * 验证手机号格式
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        if (!StringUtils.hasText(phoneNumber)) {
            return false;
        }
        // 简单的手机号格式验证（11位数字，1开头）
        return phoneNumber.matches("^1[3-9]\\d{9}$");
    }

    /**
     * 检查发送频率
     */
    private R<String> checkSendFrequency(String phoneNumber) {
        String frequencyKey = getFrequencyKey(phoneNumber);
        Long lastSendTime = redisService.getCacheObject(frequencyKey);

        if (lastSendTime != null) {
            long currentTime = System.currentTimeMillis();
            long elapsedSeconds = (currentTime - lastSendTime) / 1000;

            if (elapsedSeconds < frequencyLimit) {
                long waitTime = frequencyLimit - elapsedSeconds;
                return R.fail(String.format("请求过于频繁，请%d秒后重试", waitTime));
            }
        }

        return R.ok();
    }

    /**
     * 记录发送时间
     */
    private void recordSendTime(String phoneNumber) {
        String frequencyKey = getFrequencyKey(phoneNumber);
        redisService.setCacheObject(frequencyKey, System.currentTimeMillis(),
                frequencyLimit, TimeUnit.SECONDS);
    }

    /**
     * 判断响应是否成功
     */
    private boolean isSuccessResponse(SendSmsResponse response) {
        return response != null &&
                response.getBody() != null &&
                "OK".equals(response.getBody().getCode());
    }

    /**
     * 获取错误信息
     */
    private String getErrorMessage(String errorCode, String errorMessage) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("isv.BUSINESS_LIMIT_CONTROL", "业务限流，请稍后再试");
        errorMap.put("isv.AMOUNT_NOT_ENOUGH", "账户余额不足");
        errorMap.put("isv.MOBILE_NUMBER_ILLEGAL", "手机号码不合法");
        errorMap.put("isv.TEMPLATE_MISSING_PARAMETERS", "短信模板变量缺失");

        return errorMap.getOrDefault(errorCode, errorMessage);
    }

    /**
     * 获取短信验证码的Redis键
     */
    private String getSmsCodeKey(String phoneNumber) {
        return smsCodePrefix + phoneNumber;
    }

    /**
     * 获取频率限制的Redis键
     */
    private String getFrequencyKey(String phoneNumber) {
        return limitPrefix + phoneNumber;
    }



    @Override
    public boolean sendVerifySms(String phone, String code) {
        R<String> res = sendVerificationCode(phone, code);
        return Constants.SUCCESS.equals(res.getCode());
    }
}
