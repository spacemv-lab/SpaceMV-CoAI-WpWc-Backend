/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.controller;

import com.ruoyi.common.core.config.RsaKeyConfig;
import com.ruoyi.common.core.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * RSA 密钥管理控制器
 *
 * @author txwx
 */
@RestController
@RequestMapping("/rsa")
@RequiredArgsConstructor
@Validated
@Tag(name = "RSA 密钥管理")
@Slf4j
public class RsaKeyController {

    private final RsaKeyConfig rsaKeyConfig;

    /**
     * 获取 RSA 公钥（供前端加密使用）
     * 需要登录才能访问
     */
    @GetMapping("/key-pair")
    @PreAuthorize("@ss.hasPermi('system:registerUser:list')")
    @Operation(summary = "获取 RSA 公钥/私钥")
    public R<Map<String, String>> getPublicKey() {
        Map<String, String> result = new HashMap<>();
        result.put("publicKey", rsaKeyConfig.getPublicKey());
        result.put("privateKey", rsaKeyConfig.getPrivateKey());
        return R.ok(result);
    }
}
