/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.filter;

import com.ruoyi.iam.service.TokenService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * IAM 自签 Token 校验器（第二层 Fallback）
 * <p>
 * 使用 IAM 自身的 Nacos 密钥（${iam.jwt.secret}）解析 token，
 * 适用于前端直接使用 IAM 注册/登录返回的 token 的场景。
 * <p>
 * 优先级较低，当 BusinessTokenValidator 无法解析时使用本 Validator。
 *
 * @author txwx
 */
@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class NativeIamTokenValidator implements TokenValidator {

    private final TokenService tokenService;

    @Override
    public Long validate(String token) {
        try {
            Claims claims = tokenService.parseToken(token);
            if ("refresh".equals(claims.get("type", String.class))) {
                log.debug("NativeIamTokenValidator: refresh token rejected");
                return null; // refresh token 不能当 access token 用
            }
            Long userId = claims.get("userId", Long.class);
            if (userId == null) {
                userId = claims.get("user_id", Long.class);
            }
            log.debug("NativeIamTokenValidator: success, userId={}", userId);
            return userId;
        } catch (Exception e) {
            log.debug("NativeIamTokenValidator: failed - {}", e.getMessage());
            return null;
        }
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
