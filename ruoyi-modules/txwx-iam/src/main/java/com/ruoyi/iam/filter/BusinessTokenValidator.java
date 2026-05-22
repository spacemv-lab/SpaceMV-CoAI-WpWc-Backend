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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 业务系统 Token 校验器（第一层优先）
 * <p>
 * 1. 用业务系统配置的 JWT 密钥（${iam.token-validators.business.secret}）解析 token
 * 2. 提取 productUserId（RuoYi token 中的 user_id claim）
 * 3. 通过 iam_user_product 映射表反查 IAM userId
 * <p>
 * 适用于前端使用 RuoYi 业务 token 访问 IAM 的场景。
 * IAM 自身不依赖 RuoYi 的 JwtUtils，密钥通过 Nacos 配置注入。
 *
 * @author txwx
 */
@Slf4j
@Component
@Order(1)
public class BusinessTokenValidator implements TokenValidator {

    /** 业务系统 JWT 签名密钥（如 RuoYi 的 JwtUtils.SECRET），通过 Nacos 配置注入 */
    private final String businessSecret;

    /** 当前产品线标识（用于 iam_user_product 映射查询） */
    private final String productLine;

    /** IAM ↔ System 用户映射 Mapper */
    private final IamUserProductMapper iamUserProductMapper;

    public BusinessTokenValidator(
            @Value("${iam.token-validators.business.secret:abcdefghijklmnopqrstuvwxyz}") String businessSecret,
            @Value("${iam.product-line:spacemv-coai}") String productLine,
            IamUserProductMapper iamUserProductMapper) {
        this.businessSecret = businessSecret;
        this.productLine = productLine;
        this.iamUserProductMapper = iamUserProductMapper;
    }

    @Override
    public Long validate(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(businessSecret)
                    .parseClaimsJws(token)
                    .getBody();

            // 步骤2：提取业务系统的用户 ID（RuoYi token 中字段名 "user_id"）
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

            log.debug("BusinessTokenValidator: parsed productUserId={}", productUserId);

            // 步骤3：通过映射表反查 IAM userId
            LambdaQueryWrapper<IamUserProduct> query = new LambdaQueryWrapper<>();
            query.eq(IamUserProduct::getProductUserId, productUserId)
                    .eq(IamUserProduct::getProductLine, productLine);
            IamUserProduct mapping = iamUserProductMapper.selectOne(query);

            if (mapping == null) {
                log.warn("BusinessTokenValidator: no mapping found for productUserId={}, productLine={}",
                        productUserId, productLine);
                return null;
            }

            log.debug("BusinessTokenValidator: success, iamUserId={}", mapping.getIamUserId());
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
