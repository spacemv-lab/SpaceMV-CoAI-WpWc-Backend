package com.txwx.social.crm.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.txwx.social.crm.service.EmailService;
import com.txwx.social.crm.service.ITxwxVerifyCodeService;
import com.txwx.social.crm.service.SmsService;
import com.txwx.social.crm.util.AccountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 验证码服务实现类
 *
 * @author txwx
 * @date 2026-04-20
 */
@Service
@Slf4j
public class TxwxVerifyCodeServiceImpl implements ITxwxVerifyCodeService {

    /**
     * 短信服务商
     */
    @Autowired
    private SmsService smsService;

    @Autowired
    private EmailService emailService;

    /**
     * 短信模板Code
     */
    @Value("${txwx.sms.template-code:}")
    private String smsTemplateCode;

    @Override
    public String sendSmsCode(String phone) {
        // 1. 生成6位数字验证码
        String code = RandomUtil.randomNumbers(6);

        // 2. 发送短信（使用短信服务商）
        boolean sent = smsService.sendVerifySms(phone, code);
        if (sent) {
            log.info("发送短信验证码成功");
            return "验证码发送成功";
        } else {
            log.error("发送短信验证码失败");
            return "验证码发送失败，请稍后重试";
        }
    }

    @Override
    public String sendEmailCode(String email) {
        String code = RandomUtil.randomNumbers(6);
        boolean res = emailService.sendVerifyCode(email, code);
        if (res) {
            return "发送成功";
        }
        return "发送失败";
    }


    @Override
    public boolean verifyCode(String account, String code) {
        return switch (AccountUtil.getAccountType(account)) {
            case 1 -> smsService.verifyCode(account, code);
            case 2 -> emailService.verifyCode(account, code);
            default -> false;
        };
    }

}
