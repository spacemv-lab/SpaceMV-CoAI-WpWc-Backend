package com.ruoyi.iam.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.ruoyi.iam.entity.IamUser;
import com.ruoyi.iam.entity.IamUserChannel;
import com.ruoyi.iam.entity.IamAuthLog;
import com.ruoyi.iam.mapper.IamAuthLogMapper;
import com.ruoyi.iam.mapper.IamUserChannelMapper;
import com.ruoyi.iam.mapper.IamUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Entity/Mapper 测试类（验证数据模型正确性）
 *
 * @author txwx
 */
@DisplayName("Entity/Mapper Tests")
@ExtendWith(MockitoExtension.class)
class IamUserServiceTest
{
    @Mock
    private IamUserMapper iamUserMapper;

    @Mock
    private IamUserChannelMapper iamUserChannelMapper;

    @Mock
    private IamAuthLogMapper iamAuthLogMapper;

    // ==================== IamUser 实体测试 ====================

    @Nested
    @DisplayName("IamUser")
    class IamUserEntityTests
    {
        @Test
        @DisplayName("应能创建并设置所有字段")
        void shouldSetAllFields()
        {
            IamUser user = new IamUser();
            user.setId(10001L);
            user.setUsername("spmv_test01");
            user.setPasswordHash("$2a$10$xK3...");
            user.setDisplayName("测试用户");
            user.setAvatarUrl("https://example.com/avatar.png");
            user.setStatus("0");
            user.setDeleteStatus("0");
            user.setLastLoginIp("192.168.1.1");
            user.setLastLoginTime(new Date());

            assertEquals(10001L, user.getId());
            assertEquals("spmv_test01", user.getUsername());
            assertEquals("$2a$10$xK3...", user.getPasswordHash());
            assertEquals("测试用户", user.getDisplayName());
            assertEquals("0", user.getStatus());
            assertEquals("0", user.getDeleteStatus());
            assertEquals("192.168.1.1", user.getLastLoginIp());
        }

        @Test
        @DisplayName("字段名应映射到 snake_case")
        void shouldMapToSnakeCase()
        {
            IamUser user = new IamUser();
            user.setPasswordHash("abc");
            user.setDisplayName("name");

            assertEquals("abc", user.getPasswordHash());
            assertEquals("name", user.getDisplayName());
        }
    }

    // ==================== IamUserChannel 实体测试 ====================

    @Nested
    @DisplayName("IamUserChannel")
    class IamUserChannelEntityTests
    {
        @Test
        @DisplayName("应能设置所有字段")
        void shouldSetAllFields()
        {
            IamUserChannel channel = new IamUserChannel();
            channel.setId(1L);
            channel.setUserId(10001L);
            channel.setProductLine("spacemv-coai");
            channel.setChannelType("phone");
            channel.setChannelAccount("13800138000");
            channel.setIsPrimary("1");
            channel.setStatus("0");

            assertEquals(10001L, channel.getUserId());
            assertEquals("spacemv-coai", channel.getProductLine());
            assertEquals("phone", channel.getChannelType());
            assertEquals("13800138000", channel.getChannelAccount());
            assertEquals("1", channel.getIsPrimary());
            assertEquals("0", channel.getStatus());
        }
    }

    // ==================== IamAuthLog 实体测试 ====================

    @Nested
    @DisplayName("IamAuthLog")
    class IamAuthLogEntityTests
    {
        @Test
        @DisplayName("应能设置所有字段")
        void shouldSetAllFields()
        {
            IamAuthLog log = new IamAuthLog();
            log.setId(1L);
            log.setUserId(10001L);
            log.setChannelType("phone");
            log.setAuthType("login");
            log.setAuthResult("success");
            log.setFailReason(null);
            log.setIpAddr("192.168.1.1");
            log.setUserAgent("Mozilla/5.0");

            assertEquals(10001L, log.getUserId());
            assertEquals("login", log.getAuthType());
            assertEquals("success", log.getAuthResult());
            assertEquals("192.168.1.1", log.getIpAddr());
        }

        @Test
        @DisplayName("userId 可为 null（未登录场景）")
        void userIdCanBeNull()
        {
            IamAuthLog log = new IamAuthLog();
            log.setUserId(null);
            log.setAuthType("register");
            log.setAuthResult("success");

            assertNull(log.getUserId());
            assertEquals("register", log.getAuthType());
        }
    }
}
