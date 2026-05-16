/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.crm.service.ITxwxVerifyCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

/**
 * 验证码控制器
 *
 * @author txwx
 * @date 2026-04-20
 */
@RestController
@RequestMapping("/verify")
@RequiredArgsConstructor
@Validated
@Tag(name = "验证码服务")
@Slf4j
public class VerifyCodeController {

    @Autowired
    private ITxwxVerifyCodeService verifyCodeService;

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @return 结果
     */
    @PostMapping("/sms/send")
    @Operation(summary = "发送短信验证码")
    public AjaxResult sendSmsCode(@NotBlank(message = "手机号不能为空") @RequestParam String phone) {
        String result = verifyCodeService.sendSmsCode(phone);
        if ("操作过于频繁，请1分钟后再试".equals(result)) {
            return AjaxResult.error(result);
        }
        return AjaxResult.success(result);
    }

    /**
     * 发送邮箱验证码
     *
     * @param email 邮箱
     * @return 结果
     */
    @PostMapping("/email/send")
    @Operation(summary = "发送邮箱验证码")
    public AjaxResult sendEmailCode(@NotBlank(message = "邮箱不能为空") @RequestParam String email) {
        String result = verifyCodeService.sendEmailCode(email);
        if ("操作过于频繁，请1分钟后再试".equals(result)) {
            return AjaxResult.error(result);
        }
        return AjaxResult.success(result);
    }

    /**
     * 校验验证码
     *
     * @param account 手机号/邮箱
     * @param code 验证码
     * @return 结果
     */
    @PostMapping("/code/check")
    @Operation(summary = "校验验证码")
    public R<Boolean> verifyCode(
            @NotBlank(message = "账号不能为空") @RequestParam String account,
            @NotBlank(message = "验证码不能为空") @RequestParam String code) {
        boolean result = verifyCodeService.verifyCode(account, code);
        return R.ok(result);
    }

}
