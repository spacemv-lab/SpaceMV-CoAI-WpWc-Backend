package com.ruoyi.iam.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UsernameGenerator 测试类
 *
 * @author txwx
 */
@DisplayName("UsernameGenerator Tests")
class UsernameGeneratorTest
{
    @Test
    @DisplayName("应生成 spmv_ 前缀的用户名")
    void shouldGenerateSpmvPrefix()
    {
        String username = UsernameGenerator.generate();
        assertTrue(username.startsWith("spmv_"));
    }

    @Test
    @DisplayName("用户名总长度应为 spmv_(5) + 8 = 13 位")
    void shouldHaveCorrectLength()
    {
        String username = UsernameGenerator.generate();
        assertEquals(13, username.length());
    }

    @Test
    @DisplayName("spmv_ 后的 8 位应仅包含小写字母和数字")
    void shouldContainOnlyLowercaseAndDigits()
    {
        String username = UsernameGenerator.generate();
        String suffix = username.substring(5); // 去掉 spmv_
        assertTrue(suffix.matches("[a-z0-9]{8}"));
    }

    @Test
    @DisplayName("连续生成应返回不同用户名（唯一性）")
    void shouldGenerateDifferentUsernames()
    {
        Set<String> usernames = new HashSet<>();
        for (int i = 0; i < 100; i++)
        {
            usernames.add(UsernameGenerator.generate());
        }
        // SecureRandom 产生重复的概率极低，100 次不应有重复
        assertEquals(100, usernames.size());
    }

    @Test
    @DisplayName("应能生成多个不同的用户名")
    void shouldGenerateMultipleDistinctUsernames()
    {
        String u1 = UsernameGenerator.generate();
        String u2 = UsernameGenerator.generate();
        assertNotEquals(u1, u2);
    }
}
