/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.filter;

import com.ruoyi.common.core.utils.StringUtils;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * IAM Token 校验过滤器（两层插件化架构）
 * <p>
 * 按 {@link TokenValidator#getOrder()} 优先级遍历所有已注册的 Validator：
 * <ol>
 *   <li><b>BusinessTokenValidator（第一层）</b>— 用业务系统密钥解析 token，
 *       提取 productUserId 后通过 iam_user_product 映射表反查 IAM userId</li>
 *   <li><b>NativeIamTokenValidator（第二层）</b>— 用 IAM 自身密钥解析 token，
 *       直接提取 IAM userId（适用于 IAM 注册/登录场景）</li>
 * </ol>
 * <p>
 * 任一 Validator 成功即设置请求属性 {@code iam_user_id} 并放行。
 * <p>
 * 白名单路径（register、login 等）不会触发校验。
 *
 * @author txwx
 */
@Slf4j
@Component
@Order(-1)
public class IamTokenValidationFilter implements Filter {

    private static final String BEARER_PREFIX = "Bearer ";

    /** 请求属性键：IAM 用户 ID */
    public static final String IAM_USER_ID_ATTR = "iam_user_id";

    /** 产品线 */
    private final String productLine;

    /** 白名单路径列表（ant 风格） */
    private final List<String> whitelist;

    /** 所有已注册的 TokenValidator（Spring 自动注入） */
    @Autowired
    private List<TokenValidator> validators;

    public IamTokenValidationFilter(
            @Value("${iam.product-line:spacemv-coai}") String productLine,
            @Value("${iam.token-validation.whitelist:/auth/v1/code,/auth/v1/checkHuman,/auth/v1/checkunique," +
                    "/auth/v1/register,/auth/v1/login,/auth/v1/verify-code/send,/auth/v1/verify-code/check," +
                    "/auth/v1/password/forget/reset,/auth/v1/token/refresh," +
                    "/auth/v1/inner/**,/swagger-ui/**,/v3/api-docs/**,/favicon.ico}") List<String> whitelist) {
        this.productLine = productLine;
        this.whitelist = whitelist;
        log.info("IamTokenValidationFilter initialized, productLine={}, whitelist={}", productLine, whitelist);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String uri = request.getRequestURI();

        // 白名单放行
        if (isWhitelisted(uri)) {
            log.debug("IamTokenValidationFilter: whitelisted path={}", uri);
            chain.doFilter(request, response);
            return;
        }

        // 提取 token
        String token = extractToken(request);
        if (token == null) {
            log.warn("IamTokenValidationFilter: no token found, uri={}", uri);
            writeUnauthorized(response, "缺少认证令牌");
            return;
        }

        // 两层校验：遍历所有 Validator
        Long iamUserId = null;
        String lastError = null;
        for (TokenValidator validator : validators) {
            iamUserId = validator.validate(token);
            if (iamUserId != null) {
                log.debug("IamTokenValidationFilter: validated by {} (order={}), iamUserId={}",
                        validator.getClass().getSimpleName(), validator.getOrder(), iamUserId);
                break;
            }
            lastError = validator.getClass().getSimpleName() + " rejected";
        }

        if (iamUserId == null) {
            log.warn("IamTokenValidationFilter: all validators rejected token, uri={}, lastError={}", uri, lastError);
            writeUnauthorized(response, "认证令牌无效或已过期");
            return;
        }

        // 设置请求属性，供 Controller 使用
        request.setAttribute(IAM_USER_ID_ATTR, iamUserId);

        // 携带 IAM userId 传递给下游
        chain.doFilter(request, response);
    }

    /**
     * 从请求头中提取 Bearer token
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authHeader.substring(BEARER_PREFIX.length()).trim();
    }

    /**
     * 判断当前路径是否在白名单中（ant 风格匹配）
     */
    private boolean isWhitelisted(String uri) {
        if (whitelist == null || whitelist.isEmpty()) {
            return false;
        }
        for (String pattern : whitelist) {
            if (StringUtils.isMatch(pattern, uri)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回 401 错误
     */
    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"code\":401,\"msg\":\"" + message + "\"}");
    }

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("IamTokenValidationFilter initialized");
    }

    @Override
    public void destroy() {
        log.info("IamTokenValidationFilter destroyed");
    }
}
