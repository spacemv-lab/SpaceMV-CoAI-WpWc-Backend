package com.ruoyi.iam.filter;

import com.ruoyi.iam.entity.IamUser;
import com.ruoyi.iam.mapper.IamUserMapper;
import com.ruoyi.iam.service.TokenService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * IAM Only Token 校验器（第三层 Fallback）
 * <p>
 * 针对无独立库、无双写的产品线（productLine ≠ spacemv-coai），
 * 接受 {@code type=refresh} 的 token，验证签名后通过 userId 检查 iam_user 表确认用户存在。
 * <p>
 * 校验规则：
 * <ol>
 *   <li>必须使用 IAM 自身密钥（{@code iam.jwt.secret}）签发 — 由 {@link TokenService#parseToken} 保证</li>
 *   <li>可以接受 {@code type=refresh} 的 token（区别于 NativeIamTokenValidator）</li>
 *   <li>仅对非 spacemv-coai 产品线的 token 生效 — 从 refreshToken 的 {@code product_line} claim 判断</li>
 *   <li>除签名和有效期外，额外校验 {@code userId} 在 iam_user 表中真实存在且未删除</li>
 * </ol>
 * <p>
 * 优先级最低，作为最后一道防线。
 *
 * @author txwx
 */
@Slf4j
@Component
@Order(3)
public class IamOnlyTokenValidator implements TokenValidator {

    private final TokenService tokenService;
    private final IamUserMapper userMapper;

    public IamOnlyTokenValidator(TokenService tokenService, IamUserMapper userMapper) {
        this.tokenService = tokenService;
        this.userMapper = userMapper;
    }

    @Override
    public Long validate(String token) {
        try {
            Claims claims = tokenService.parseToken(token);

            // 1. 仅处理 type=refresh 的 token
            String tokenType = claims.get("type", String.class);
            if (!"refresh".equals(tokenType)) {
                log.debug("IamOnlyTokenValidator: not a refresh token, type={}", tokenType);
                return null;
            }

            // 2. 仅对非 spacemv-coai 产品线生效
            String tokenProductLine = claims.get("product_line", String.class);
            if (tokenProductLine == null || "spacemv-coai".equals(tokenProductLine)) {
                log.debug("IamOnlyTokenValidator: productLine={}, skip (spacemv-coai goes to NativeIamTokenValidator)", tokenProductLine);
                return null;
            }

            // 3. 提取 userId
            Long userId = claims.get("userId", Long.class);
            if (userId == null) {
                log.debug("IamOnlyTokenValidator: userId claim missing");
                return null;
            }

            // 4. 查 iam_user 表验证用户存在且未删除
            IamUser user = userMapper.selectById(userId);
            if (user == null) {
                log.warn("IamOnlyTokenValidator: user not found, userId={}", userId);
                return null;
            }
            if ("2".equals(user.getDeleteStatus())) {
                log.warn("IamOnlyTokenValidator: user deleted, userId={}", userId);
                return null;
            }
            if (!"0".equals(user.getStatus())) {
                log.warn("IamOnlyTokenValidator: user disabled, userId={}", userId);
                return null;
            }

            log.debug("IamOnlyTokenValidator: success, userId={}, productLine={}", userId, tokenProductLine);
            return userId;

        } catch (Exception e) {
            log.debug("IamOnlyTokenValidator: failed - {}", e.getMessage());
            return null;
        }
    }

    @Override
    public int getOrder() {
        return 3;
    }
}
