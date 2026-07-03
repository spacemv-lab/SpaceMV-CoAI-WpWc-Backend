/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Token service for JWT issue/validate/parse/refresh.
 */
@Slf4j
@Service
public class TokenService
{
    private final Key signingKey;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    public TokenService(
        @Value("${iam.jwt.secret}") String secret,
        @Value("${iam.jwt.access-token-validity:7200}") long accessTokenValidity,
        @Value("${iam.jwt.refresh-token-validity:2592000}") long refreshTokenValidity)
    {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public String createAccessToken(Long userId, String username, String productLine, String[] channels)
    {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidity * 1000L);
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
            .signWith(signingKey, SignatureAlgorithm.HS512)
            .compact();
    }

    public String createRefreshToken(Long userId)
    {
        return createRefreshToken(userId, null, null);
    }

    public String createRefreshToken(Long userId, String username, String productLine)
    {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenValidity * 1000L);

        io.jsonwebtoken.JwtBuilder builder = Jwts.builder()
            .setSubject(String.valueOf(userId))
            .setIssuer("spacemv-iam")
            .setIssuedAt(now)
            .setExpiration(expiry)
            .claim("userId", userId)
            .claim("type", "refresh");

        if (username != null)
        {
            builder.claim("username", username);
        }
        if (productLine != null)
        {
            builder.claim("product_line", productLine);
        }

        return builder.signWith(signingKey, SignatureAlgorithm.HS256).compact();
    }

    public Claims parseToken(String token)
    {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token)
            .getBody();

        if (isTokenExpired(claims))
        {
            throw new IllegalArgumentException("Token expired");
        }
        return claims;
    }

    public boolean validateToken(String token)
    {
        try
        {
            Claims claims = parseToken(token);
            if ("refresh".equals(claims.get("type", String.class)))
            {
                return false;
            }
            return !isTokenExpired(claims);
        }
        catch (Exception e)
        {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public Long getUserId(String token)
    {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    public String getUsername(String token)
    {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    public String getProductLine(String token)
    {
        Claims claims = parseToken(token);
        return claims.get("product_line", String.class);
    }

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
            String username = claims.get("username", String.class);
            String productLine = claims.get("product_line", String.class);
            return createAccessToken(userId,
                username != null ? username : "refreshed",
                productLine != null ? productLine : "refreshed",
                new String[0]);
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
            .signWith(signingKey, SignatureAlgorithm.HS512)
            .compact();
    }
}
