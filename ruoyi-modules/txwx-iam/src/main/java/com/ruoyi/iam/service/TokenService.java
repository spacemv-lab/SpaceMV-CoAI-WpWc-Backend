package com.ruoyi.iam.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Token 服务 — JWT 签发 / 校验 / 解析 / 刷新
 * 基于 jjwt 0.9.1 API
 *
 * @author txwx
 */
@Slf4j
@Service
public class TokenService
{

    private final byte[] signingKeyBytes;

    private final long accessTokenValidity;

    private final long refreshTokenValidity;

    public TokenService(
        @Value("${iam.jwt.secret}") String secret,
        @Value("${iam.jwt.access-token-validity:7200}") long accessTokenValidity,
        @Value("${iam.jwt.refresh-token-validity:2592000}") long refreshTokenValidity)
    {
        this.signingKeyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    /**
     * 签发访问令牌
     */
    public String createAccessToken(Long userId, String username, String productLine, String[] channels)
    {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidity * 1000L);

        // 兼容 Ruoyi-Cloud：签发符合 Gateway AuthFilter 期望的 token
        // Claims: user_key (UUID), user_id, username 是 Gateway AuthFilter 校验的必填字段
        String userKey = java.util.UUID.randomUUID().toString().replace("-", "");
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .setIssuer("spacemv-iam")
            .setIssuedAt(now)
            .setExpiration(expiry)
            .claim("user_key", userKey)
            .claim("user_id", userId)
            .claim("username", username)
            .claim("userId", userId)
            .claim("product_line", productLine)
            .claim("channels", channels)
            .claim("type", "access")
            .signWith(SignatureAlgorithm.HS512, signingKeyBytes)
            .compact();
    }

    /**
     * 签发刷新令牌
     */
    public String createRefreshToken(Long userId)
    {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenValidity * 1000L);

        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .setIssuer("spacemv-iam")
            .setIssuedAt(now)
            .setExpiration(expiry)
            .claim("userId", userId)
            .claim("type", "refresh")
            .signWith(SignatureAlgorithm.HS256, signingKeyBytes)
            .compact();
    }

    /**
     * 解析令牌（校验签名和有效期）
     */
    public Claims parseToken(String token)
    {
        Claims claims = Jwts.parser()
            .setSigningKey(signingKeyBytes)
            .parseClaimsJws(token)
            .getBody();

        if (isTokenExpired(claims))
        {
            throw new IllegalArgumentException("Token expired");
        }
        return claims;
    }

    /**
     * 校验令牌是否有效（签名+有效期）
     */
    public boolean validateToken(String token)
    {
        try
        {
            Claims claims = parseToken(token);
            if ("refresh".equals(claims.get("type", String.class)))
            {
                return false; // refresh token 不能当 access token 用
            }
            return !isTokenExpired(claims);
        }
        catch (Exception e)
        {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从令牌中获取 userId
     */
    public Long getUserId(String token)
    {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从令牌中获取 username
     */
    public String getUsername(String token)
    {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    /**
     * 从令牌中获取 product_line
     */
    public String getProductLine(String token)
    {
        Claims claims = parseToken(token);
        return claims.get("product_line", String.class);
    }

    /**
     * 刷新 access token
     */
    public String refreshToken(String refreshToken)
    {
        try
        {
            Claims claims = parseToken(refreshToken);
            if (!"refresh".equals(claims.get("type", String.class)))
            {
                throw new IllegalArgumentException("Invalid refresh token type");
            }

            Long userId = claims.get("userId", Long.class);
            return createAccessToken(userId, "refreshed", "refreshed", new String[0]);
        }
        catch (Exception e)
        {
            log.warn("Token refresh failed: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid or expired refresh token", e);
        }
    }

    private boolean isTokenExpired(Claims claims)
    {
        return claims.getExpiration().before(new Date());
    }

    /**
     * 签发访问令牌（兼容 Ruoyi-Cloud — 含 userKey 参数）
     * userKey 与 Redis login_tokens 前缀一致
     *
     * @param userKey JWT user_key claim，必须与 Redis key 一致
     */
    public String createAccessTokenWithUserKey(Long userId, String username, String productLine, String[] channels, String userKey)
    {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidity * 1000L);

        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .setIssuer("spacemv-iam")
            .setIssuedAt(now)
            .setExpiration(expiry)
            .claim("user_key", userKey)
            .claim("user_id", userId)
            .claim("username", username)
            .claim("userId", userId)
            .claim("product_line", productLine)
            .claim("channels", channels)
            .claim("type", "access")
            .signWith(SignatureAlgorithm.HS512, signingKeyBytes)
            .compact();
    }
}
