/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TokenService 测试类（jjwt 0.9.1 API）
 *
 * @author txwx
 */
@DisplayName("TokenService Tests")
class TokenServiceTest
{
    private static final String TEST_SECRET = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";

    private TokenService tokenService;

    @BeforeEach
    void setUp()
    {
        tokenService = new TokenService(
            TEST_SECRET,
            7200,
            2592000
        );
    }

    // ==================== createAccessToken ====================

    @Nested
    @DisplayName("createAccessToken")
    class CreateAccessTokenTests
    {
        @Test
        @DisplayName("应返回非空 JWT 字符串")
        void shouldCreateToken()
        {
            String token = tokenService.createAccessToken(10001L, "spmv_test01", "spacemv-coai", new String[]{"phone:138****8000"});
            assertNotNull(token);
            assertTrue(token.startsWith("eyJ"));
        }

        @Test
        @DisplayName("签发的 token 应能被正确解析")
        void shouldParseCorrectly()
        {
            String token = tokenService.createAccessToken(10001L, "spmv_test01", "spacemv-coai", new String[]{"phone:138****8000"});

            Claims claims = tokenService.parseToken(token);

            assertEquals("10001", claims.getSubject());
            assertEquals(10001L, claims.get("userId", Long.class));
            assertEquals("spmv_test01", claims.get("username", String.class));
            assertEquals("spacemv-coai", claims.get("product_line", String.class));
            assertEquals("spacemv-iam", claims.getIssuer());
        }

        @Test
        @DisplayName("空 channels 数组应能正常签发")
        void shouldHandleEmptyChannels()
        {
            String token = tokenService.createAccessToken(10002L, "spmv_test02", "spacemv-coai", new String[0]);
            assertNotNull(token);

            Claims claims = tokenService.parseToken(token);
            assertEquals(10002L, claims.get("userId", Long.class));
        }

        @Test
        @DisplayName("多个 channel 应被正确保存")
        void shouldHandleMultipleChannels()
        {
            String token = tokenService.createAccessToken(10003L, "spmv_test03", "spacemv-coai",
                new String[]{"phone:138****8000", "email:z**@txwx.com"});
            assertNotNull(token);

            Claims claims = tokenService.parseToken(token);
            Object channels = claims.get("channels");
            assertNotNull(channels);
        }
    }

    // ==================== validateToken ====================

    @Nested
    @DisplayName("validateToken")
    class ValidateTokenTests
    {
        @Test
        @DisplayName("有效 token 应返回 true")
        void validTokenShouldReturnTrue()
        {
            String token = tokenService.createAccessToken(10001L, "spmv_test01", "spacemv-coai", new String[]{});
            assertTrue(tokenService.validateToken(token));
        }

        @Test
        @DisplayName("无效 token 应返回 false")
        void invalidTokenShouldReturnFalse()
        {
            assertFalse(tokenService.validateToken("invalid.token.here"));
        }

        @Test
        @DisplayName("null token 应返回 false")
        void nullTokenShouldReturnFalse()
        {
            assertFalse(tokenService.validateToken(null));
        }

        @Test
        @DisplayName("被篡改的 token 应返回 false")
        void tamperedTokenShouldReturnFalse()
        {
            String original = tokenService.createAccessToken(10001L, "spmv_test01", "spacemv-coai", new String[]{});
            String tampered = original.substring(0, original.length() - 5) + "XXX";
            assertFalse(tokenService.validateToken(tampered));
        }

        @Test
        @DisplayName("过期 token 应返回 false")
        void expiredTokenShouldReturnFalse()
        {
            TokenService shortLived = new TokenService(TEST_SECRET, 0, 2592000);
            String token = shortLived.createAccessToken(10001L, "spmv_test01", "spacemv-coai", new String[]{});

            try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

            assertFalse(shortLived.validateToken(token));
        }
    }

    // ==================== parseToken helpers ====================

    @Nested
    @DisplayName("parseToken getters")
    class ParseTokenGettersTests
    {
        @Test
        @DisplayName("应正确提取 userId")
        void shouldExtractUserId()
        {
            String token = tokenService.createAccessToken(10001L, "spmv_test01", "spacemv-coai", new String[]{});
            Long userId = tokenService.getUserId(token);
            assertEquals(10001L, userId);
        }

        @Test
        @DisplayName("应正确提取 username")
        void shouldExtractUsername()
        {
            String token = tokenService.createAccessToken(10001L, "spmv_test01", "spacemv-coai", new String[]{});
            String username = tokenService.getUsername(token);
            assertEquals("spmv_test01", username);
        }

        @Test
        @DisplayName("应正确提取 product_line")
        void shouldExtractProductLine()
        {
            String token = tokenService.createAccessToken(10001L, "spmv_test01", "spacemv-coai", new String[]{});
            String productLine = tokenService.getProductLine(token);
            assertEquals("spacemv-coai", productLine);
        }

        @Test
        @DisplayName("不同用户应生成不同 token 和 userId")
        void differentUsersShouldHaveDifferentTokens()
        {
            String token1 = tokenService.createAccessToken(10001L, "spmv_user01", "spacemv-coai", new String[]{});
            String token2 = tokenService.createAccessToken(10002L, "spmv_user02", "spacemv-coai", new String[]{});

            assertNotEquals(10001L, tokenService.getUserId(token2));
            assertEquals(10002L, tokenService.getUserId(token2));
        }
    }

    // ==================== refreshToken ====================

    @Nested
    @DisplayName("refreshToken")
    class RefreshTokenTests
    {
        @Test
        @DisplayName("有效的 refresh token 应返回新 access token")
        void validRefreshTokenShouldReturnNewToken()
        {
            String refreshToken = tokenService.createRefreshToken(10001L);
            String newToken = tokenService.refreshToken(refreshToken);

            assertNotNull(newToken);
            assertTrue(newToken.startsWith("eyJ"));
            assertEquals(10001L, tokenService.getUserId(newToken));
        }

        @Test
        @DisplayName("用 access token 做 refresh 应抛出异常")
        void accessTokenShouldFailAsRefreshToken()
        {
            String accessToken = tokenService.createAccessToken(10001L, "spmv_test01", "spacemv-coai", new String[]{});

            assertThrows(IllegalArgumentException.class, () -> tokenService.refreshToken(accessToken));
        }

        @Test
        @DisplayName("过期的 refresh token 应抛出异常")
        void expiredRefreshTokenShouldThrow()
        {
            TokenService shortLived = new TokenService(TEST_SECRET, 0, 0);
            String refreshToken = shortLived.createRefreshToken(10001L);

            try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

            assertThrows(IllegalArgumentException.class, () -> shortLived.refreshToken(refreshToken));
        }

        @Test
        @DisplayName("无效 refresh token 应抛出异常")
        void invalidRefreshTokenShouldThrow()
        {
            assertThrows(IllegalArgumentException.class, () -> tokenService.refreshToken("invalid.token.here"));
        }
    }

    // ==================== createAccessTokenWithUserKey ====================

    @Nested
    @DisplayName("createAccessTokenWithUserKey")
    class CreateAccessTokenWithUserKeyTests
    {
        @Test
        @DisplayName("应返回非空 JWT 字符串")
        void shouldCreateToken()
        {
            String userKey = "test-user-key-12345";
            String token = tokenService.createAccessTokenWithUserKey(10001L, "spmv_test01", "spacemv-coai", new String[]{}, userKey);
            assertNotNull(token);
            assertTrue(token.startsWith("eyJ"));
        }

        @Test
        @DisplayName("JWT 中包含 user_key claim")
        void shouldIncludeUserKey()
        {
            String userKey = "test-user-key-67890";
            String token = tokenService.createAccessTokenWithUserKey(10001L, "spmv_test01", "spacemv-coai", new String[]{}, userKey);

            Claims claims = tokenService.parseToken(token);
            assertEquals(userKey, claims.get("user_key", String.class));
        }

        @Test
        @DisplayName("JWT 中包含 user_id claim")
        void shouldIncludeUserId()
        {
            String token = tokenService.createAccessTokenWithUserKey(42L, "user42", "spacemv-coai", new String[]{}, "key");

            Claims claims = tokenService.parseToken(token);
            assertEquals(42L, claims.get("user_id", Long.class));
        }

        @Test
        @DisplayName("JWT 仍包含 userId claim（向后兼容）")
        void shouldIncludeUserIdBackwardCompat()
        {
            String token = tokenService.createAccessTokenWithUserKey(99L, "user99", "spacemv-coai", new String[]{}, "key");

            Claims claims = tokenService.parseToken(token);
            assertEquals(99L, claims.get("userId", Long.class));
        }

        @Test
        @DisplayName("与 createAccessToken 的 token 可互相解析")
        void shouldParseWithBothMethods()
        {
            String token1 = tokenService.createAccessToken(10001L, "spmv_test01", "spacemv-coai", new String[]{});
            Claims c1 = tokenService.parseToken(token1);
            assertEquals(10001L, c1.get("userId", Long.class));

            String token2 = tokenService.createAccessTokenWithUserKey(10001L, "spmv_test01", "spacemv-coai", new String[]{}, "key");
            Claims c2 = tokenService.parseToken(token2);
            assertEquals(10001L, c2.get("userId", Long.class));
        }
    }
}
