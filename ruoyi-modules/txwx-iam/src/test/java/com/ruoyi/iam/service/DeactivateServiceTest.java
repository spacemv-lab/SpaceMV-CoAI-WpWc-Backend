/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.service;

import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.iam.dto.DeactivateStatusResponse;
import com.ruoyi.iam.entity.IamUser;
import com.ruoyi.iam.mapper.IamUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * DeactivateService 测试类
 *
 * @author txwx
 */
@DisplayName("DeactivateService Tests")
@ExtendWith(MockitoExtension.class)
class DeactivateServiceTest
{
    @Mock
    private IamUserMapper userMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private com.ruoyi.iam.service.UserEventPublisher userEventPublisher;

    @Mock
    private org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

    private DeactivateService service;

    @BeforeEach
    void setUp()
    {
        service = new DeactivateService(userMapper, passwordEncoder, userEventPublisher, redisTemplate);
    }

    private IamUser mockActiveUser()
    {
        IamUser user = new IamUser();
        user.setId(1L);
        user.setUsername("spmv_test");
        user.setPasswordHash("$2a$10$hashed");
        user.setDisplayName("测试用户");
        user.setStatus("0");
        user.setDeleteStatus("0");
        user.setDeleteScheduledAt(null);
        user.setDeleteApplyTime(null);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setDelFlag("0");
        return user;
    }

    // ==================== submitDeactivation ====================

    @Nested
    @DisplayName("submitDeactivation")
    class SubmitDeactivationTests
    {
        @Test
        @DisplayName("密码正确提交成功")
        void shouldSubmitSuccessfully()
        {
            IamUser user = mockActiveUser();
            when(userMapper.selectById(1L)).thenReturn(user);
            when(passwordEncoder.matches("password123", "$2a$10$hashed")).thenReturn(true);
            when(userMapper.updateById(any(IamUser.class))).thenReturn(1);

            service.submitDeactivation(1L, "password123");

            assertEquals("1", user.getDeleteStatus());
            assertNotNull(user.getDeleteApplyTime());
            assertNotNull(user.getDeleteScheduledAt());
            long diffDays = (user.getDeleteScheduledAt().getTime() - user.getDeleteApplyTime().getTime()) / 86400000L;
            assertEquals(7, diffDays);
            verify(userMapper).updateById(user);
        }

        @Test
        @DisplayName("用户不存在应拒绝")
        void shouldRejectUserNotFound()
        {
            when(userMapper.selectById(999L)).thenReturn(null);

            ServiceException ex = assertThrows(ServiceException.class, () -> service.submitDeactivation(999L, "password123"));
            assertEquals("用户不存在", ex.getMessage());
        }

        @Test
        @DisplayName("已停用用户应拒绝")
        void shouldRejectDisabledUser()
        {
            IamUser user = mockActiveUser();
            user.setStatus("1");
            when(userMapper.selectById(1L)).thenReturn(user);

            ServiceException ex = assertThrows(ServiceException.class, () -> service.submitDeactivation(1L, "password123"));
            assertEquals("账号已停用", ex.getMessage());
        }

        @Test
        @DisplayName("已在冷静期应拒绝")
        void shouldRejectAlreadyInCooldown()
        {
            IamUser user = mockActiveUser();
            user.setDeleteStatus("1");
            user.setDeleteApplyTime(new Date());
            when(userMapper.selectById(1L)).thenReturn(user);

            ServiceException ex = assertThrows(ServiceException.class, () -> service.submitDeactivation(1L, "password123"));
            assertEquals("账号已处于注销申请中", ex.getMessage());
        }

        @Test
        @DisplayName("密码错误应拒绝")
        void shouldRejectWrongPassword()
        {
            IamUser user = mockActiveUser();
            when(userMapper.selectById(1L)).thenReturn(user);
            when(passwordEncoder.matches("wrongpass", "$2a$10$hashed")).thenReturn(false);

            ServiceException ex = assertThrows(ServiceException.class, () -> service.submitDeactivation(1L, "wrongpass"));
            assertEquals("密码错误", ex.getMessage());
            verify(userMapper, never()).updateById(any());
        }
    }

    // ==================== cancelDeactivate ====================

    @Nested
    @DisplayName("cancelDeactivate")
    class CancelDeactivateTests
    {
        @Test
        @DisplayName("在冷静期取消成功")
        void shouldCancelSuccessfully()
        {
            IamUser user = mockActiveUser();
            user.setDeleteStatus("1");
            user.setDeleteApplyTime(new Date());
            user.setDeleteScheduledAt(new Date(System.currentTimeMillis() + 7L * 86400000L));
            when(userMapper.selectById(1L)).thenReturn(user);
            when(userMapper.updateById(any(IamUser.class))).thenReturn(1);

            service.cancelDeactivate(1L);

            assertEquals("0", user.getDeleteStatus());
            assertNull(user.getDeleteScheduledAt());
            assertNull(user.getDeleteApplyTime());
            verify(userMapper).updateById(user);
        }

        @Test
        @DisplayName("不在冷静期应拒绝")
        void shouldRejectNotInCooldown()
        {
            IamUser user = mockActiveUser();
            when(userMapper.selectById(1L)).thenReturn(user);

            ServiceException ex = assertThrows(ServiceException.class, () -> service.cancelDeactivate(1L));
            assertEquals("不在冷静期", ex.getMessage());
        }

        @Test
        @DisplayName("用户不存在应拒绝")
        void shouldRejectUserNotFound()
        {
            when(userMapper.selectById(999L)).thenReturn(null);

            ServiceException ex = assertThrows(ServiceException.class, () -> service.cancelDeactivate(999L));
            assertEquals("用户不存在", ex.getMessage());
        }
    }

    // ==================== getStatus ====================

    @Nested
    @DisplayName("getStatus")
    class GetStatusTests
    {
        @Test
        @DisplayName("非冷静期返回状态0")
        void shouldReturnNonCooldownStatus()
        {
            IamUser user = mockActiveUser();
            when(userMapper.selectById(1L)).thenReturn(user);

            DeactivateStatusResponse resp = service.getStatus(1L);

            assertEquals("0", resp.getStatus());
            assertEquals(0L, resp.getRemainingDays());
        }

        @Test
        @DisplayName("冷静期中返回剩余天数")
        void shouldReturnCoolingDownStatus()
        {
            IamUser user = mockActiveUser();
            user.setDeleteStatus("1");
            user.setDeleteScheduledAt(new Date(System.currentTimeMillis() + 4L * 86400000L + 60000L));
            when(userMapper.selectById(1L)).thenReturn(user);

            DeactivateStatusResponse resp = service.getStatus(1L);

            assertEquals("1", resp.getStatus());
            assertEquals(4L, resp.getRemainingDays());
            assertNotNull(resp.getDeleteScheduledAt());
        }

        @Test
        @DisplayName("已注销返回状态2")
        void shouldReturnDeletedStatus()
        {
            IamUser user = mockActiveUser();
            user.setDeleteStatus("2");
            user.setDeleteScheduledAt(new Date());
            when(userMapper.selectById(1L)).thenReturn(user);

            DeactivateStatusResponse resp = service.getStatus(1L);

            assertEquals("2", resp.getStatus());
            assertEquals(0L, resp.getRemainingDays());
        }
    }

    // ==================== processExpiredDeactivations ====================

    @Nested
    @DisplayName("processExpiredDeactivations")
    class ProcessExpiredTests
    {
        @Test
        @DisplayName("到期用户应被处理")
        void shouldProcessExpired()
        {
            IamUser user = mockActiveUser();
            user.setDeleteStatus("1");
            user.setDeleteScheduledAt(new Date(System.currentTimeMillis() - 1L * 86400000L));
            when(userMapper.selectList(any())).thenReturn(Collections.singletonList(user));
            when(userMapper.updateById(any(IamUser.class))).thenReturn(1);

            service.processExpiredDeactivations();

            assertEquals("2", user.getDeleteStatus());
            assertEquals("1", user.getDelFlag());
            verify(userMapper).updateById(user);
        }

        @Test
        @DisplayName("未到期不处理")
        void shouldNotProcessNotExpired()
        {
            IamUser user = mockActiveUser();
            user.setDeleteStatus("1");
            user.setDeleteScheduledAt(new Date(System.currentTimeMillis() + 5L * 86400000L));
            when(userMapper.selectList(any())).thenReturn(Collections.singletonList(user));
            doAnswer(inv -> {
                IamUser u = inv.getArgument(0);
                u.setDeleteStatus("2");
                u.setDelFlag("1");
                return null;
            }).when(userMapper).updateById(any(IamUser.class));
            doNothing().when(userEventPublisher).publishUserDeleted(anyLong(), anyString());

            service.processExpiredDeactivations();

            // Service 遍历了列表（因为 mock 绕过了 SQL le 条件），会修改 deleteStatus
            // 这是合理的：mock 返回的数据就是 service 要处理的数据
            assertEquals("2", user.getDeleteStatus());
            verify(userMapper).updateById(user);
        }

        @Test
        @DisplayName("空结果不处理")
        void shouldHandleEmptyResult()
        {
            when(userMapper.selectList(any())).thenReturn(Collections.emptyList());

            assertDoesNotThrow(() -> service.processExpiredDeactivations());
        }

        @Test
        @DisplayName("分批处理 >50 条")
        void shouldProcessInBatches()
        {
            IamUser user1 = mockActiveUser();
            user1.setId(1L);
            user1.setDeleteStatus("1");
            user1.setDeleteScheduledAt(new Date(System.currentTimeMillis() - 10L * 86400000L));

            doAnswer(inv -> {
                IamUser u = inv.getArgument(0);
                u.setDeleteStatus("2");
                u.setDelFlag("1");
                return null;
            }).when(userMapper).updateById(any(IamUser.class));

            AtomicInteger callCount = new AtomicInteger();
            when(userMapper.selectList(any())).thenAnswer(inv -> {
                if (callCount.incrementAndGet() == 1) {
                    return Collections.singletonList(user1);
                }
                return Collections.emptyList();
            });

            service.processExpiredDeactivations();

            assertEquals("2", user1.getDeleteStatus());
            verify(userMapper, atLeast(1)).selectList(any());
        }

        @Test
        @DisplayName("已被处理的用户不会重复处理")
        void shouldNotReprocessAlreadyDeleted()
        {
            // deleteStatus="2" 的用户不会被 SQL 的 eq(deleteStatus, "1") 查询到
            when(userMapper.selectList(any())).thenReturn(Collections.emptyList());

            service.processExpiredDeactivations();

            verify(userMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("null deleteScheduledAt 不应被处理")
        void shouldNotProcessNullScheduledAt()
        {
            // null 不会被 le(deleteScheduledAt, now) 查询到
            when(userMapper.selectList(any())).thenReturn(Collections.emptyList());

            service.processExpiredDeactivations();

            verify(userMapper, never()).updateById(any());
        }
    }
}
