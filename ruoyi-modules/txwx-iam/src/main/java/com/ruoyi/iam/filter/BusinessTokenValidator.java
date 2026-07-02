/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.filter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.iam.entity.IamUserProduct;
import com.ruoyi.iam.mapper.IamUserProductMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Slf4j
@Component
@Order(1)
public class BusinessTokenValidator implements TokenValidator {

    private final String businessSecret;
    private final String productLine;
    private final IamUserProductMapper iamUserProductMapper;

    public BusinessTokenValidator(
        @Value("${iam.token-validators.business.secret}") String businessSecret,
        @Value("${iam.product-line:spacemv-coai}") String productLine,
        IamUserProductMapper iamUserProductMapper) {
        this.businessSecret = businessSecret;
        this.productLine = productLine;
        this.iamUserProductMapper = iamUserProductMapper;
    }

    private Key signingKey() {
        if (businessSecret == null || businessSecret.isBlank()) {
            throw new IllegalStateException("Business token secret is not configured");
        }
        return Keys.hmacShaKeyFor(businessSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Long validate(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

            Long productUserId;
            Object userIdObj = claims.get("user_id");
            if (userIdObj instanceof Number) {
                productUserId = ((Number) userIdObj).longValue();
            } else if (userIdObj instanceof String) {
                productUserId = Long.parseLong((String) userIdObj);
            } else {
                log.warn("BusinessTokenValidator: user_id field missing or invalid type in token claims");
                return null;
            }

            LambdaQueryWrapper<IamUserProduct> query = new LambdaQueryWrapper<>();
            query.eq(IamUserProduct::getProductUserId, productUserId)
                .eq(IamUserProduct::getProductLine, productLine);
            IamUserProduct mapping = iamUserProductMapper.selectOne(query);

            if (mapping == null) {
                log.warn("BusinessTokenValidator: no mapping found for productUserId={}, productLine={}",
                    productUserId, productLine);
                return null;
            }

            return mapping.getIamUserId();
        } catch (Exception e) {
            log.debug("BusinessTokenValidator: failed - {}", e.getMessage());
            return null;
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
