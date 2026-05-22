/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.service;

import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.iam.dto.ChannelResponse;
import com.ruoyi.iam.entity.IamUserChannel;
import com.ruoyi.iam.mapper.IamUserChannelMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserChannelService 测试类
 *
 * @author txwx
 */
@DisplayName("UserChannelService Tests")
@ExtendWith(MockitoExtension.class)
class UserChannelServiceTest
{
    @Mock
    private IamUserChannelMapper channelMapper;

    @Mock
    private VerifyCodeService verifyCodeService;

    @Mock
    private com.ruoyi.iam.mapper.IamUserBackupContactMapper backupContactMapper;

    @Mock
    private com.ruoyi.iam.mapper.IamUserProductMapper iamUserProductMapper;

    @Mock
    private com.ruoyi.system.api.RemoteUserService remoteUserService;

    private UserChannelService service;

    @BeforeEach
    void setUp()
    {
        service = new UserChannelService(channelMapper, backupContactMapper, verifyCodeService, iamUserProductMapper, remoteUserService);
    }

    // ==================== bindChannel ====================

    @Nested
    @DisplayName("bindChannel")
    class BindChannelTests
    {
        @Test
        @DisplayName("绑定手机号成功")
        void shouldBindPhone()
        {
            when(verifyCodeService.verifyCode(eq("phone"), eq("13800138000"), eq("123456"))).thenReturn(true);
            when(channelMapper.selectCount(any())).thenReturn(0L);
            doAnswer(inv -> {
                IamUserChannel ch = inv.getArgument(0);
                ch.setId(1L);
                return null;
            }).when(channelMapper).insert(any(IamUserChannel.class));

            assertDoesNotThrow(() -> service.bindChannel(1L, "phone", "13800138000", "123456"));

            verify(channelMapper).insert(argThat(ch ->
                "phone".equals(ch.getChannelType())
                && "13800138000".equals(ch.getChannelAccount())
                && "spacemv-coai".equals(ch.getProductLine())
            ));
        }

        @Test
        @DisplayName("绑定邮箱成功")
        void shouldBindEmail()
        {
            when(verifyCodeService.verifyCode(eq("email"), eq("test@example.com"), eq("654321"))).thenReturn(true);
            when(channelMapper.selectCount(any())).thenReturn(0L);
            doAnswer(inv -> {
                IamUserChannel ch = inv.getArgument(0);
                ch.setId(1L);
                return null;
            }).when(channelMapper).insert(any(IamUserChannel.class));

            assertDoesNotThrow(() -> service.bindChannel(1L, "email", "test@example.com", "654321"));
        }

        @Test
        @DisplayName("验证码错误应拒绝")
        void shouldRejectInvalidCode()
        {
            when(verifyCodeService.verifyCode(eq("phone"), eq("13800138000"), eq("wrong"))).thenReturn(false);

            ServiceException ex = assertThrows(ServiceException.class, () -> service.bindChannel(1L, "phone", "13800138000", "wrong"));
            assertEquals("验证码错误或已过期", ex.getMessage());
            verify(channelMapper, never()).insert(any());
        }

        @Test
        @DisplayName("已绑定同类型应拒绝")
        void shouldRejectDuplicateType()
        {
            when(verifyCodeService.verifyCode(eq("phone"), eq("13800138000"), eq("123456"))).thenReturn(true);
            when(channelMapper.selectCount(any())).thenReturn(1L, 0L); // 用户已有 + 全局无

            ServiceException ex = assertThrows(ServiceException.class, () -> service.bindChannel(1L, "phone", "13800138000", "123456"));
            assertEquals("您已绑定该类型通道", ex.getMessage());
        }

        @Test
        @DisplayName("通道已被他人注册应拒绝")
        void shouldRejectGlobalDuplicate()
        {
            when(verifyCodeService.verifyCode(eq("phone"), eq("13800138000"), eq("123456"))).thenReturn(true);
            when(channelMapper.selectCount(any())).thenReturn(0L, 1L); // 用户无 + 全局有

            ServiceException ex = assertThrows(ServiceException.class, () -> service.bindChannel(1L, "phone", "13800138000", "123456"));
            assertEquals("该通道已被注册", ex.getMessage());
        }
    }

    // ==================== unbindChannel ====================

    @Nested
    @DisplayName("unbindChannel")
    class UnbindChannelTests
    {
        private IamUserChannel mockPhoneChannel()
        {
            IamUserChannel ch = new IamUserChannel();
            ch.setId(1L);
            ch.setUserId(1L);
            ch.setChannelType("phone");
            ch.setChannelAccount("13800138000");
            ch.setIsPrimary("1");
            ch.setStatus("0");
            return ch;
        }

        @Test
        @DisplayName("解绑非主通道成功")
        void shouldUnbindNonPrimary()
        {
            IamUserChannel ch = mockPhoneChannel();
            ch.setIsPrimary("0");
            when(channelMapper.selectOne(any())).thenReturn(ch);
            when(verifyCodeService.verifyCode(eq("phone"), eq("13800138000"), eq("123456"))).thenReturn(true);
            when(channelMapper.updateById(any())).thenReturn(1);

            assertDoesNotThrow(() -> service.unbindChannel(1L, "phone", "123456", null));

            assertEquals("1", ch.getStatus());
            verify(channelMapper).updateById(ch);
        }

        @Test
        @DisplayName("解绑主通道但有其他通道允许")
        void shouldUnbindPrimaryWithOtherChannel()
        {
            IamUserChannel ch = mockPhoneChannel();
            when(channelMapper.selectOne(any())).thenReturn(ch);
            when(verifyCodeService.verifyCode(eq("phone"), eq("13800138000"), eq("123456"))).thenReturn(true);
            when(channelMapper.selectCount(any())).thenReturn(1L); // 有其他通道
            when(channelMapper.updateById(any())).thenReturn(1);

            assertDoesNotThrow(() -> service.unbindChannel(1L, "phone", "123456", null));

            assertEquals("1", ch.getStatus());
        }

        @Test
        @DisplayName("解绑唯一主通道应拒绝")
        void shouldRejectUnbindOnlyPrimary()
        {
            IamUserChannel ch = mockPhoneChannel();
            when(channelMapper.selectOne(any())).thenReturn(ch);
            when(verifyCodeService.verifyCode(eq("phone"), eq("13800138000"), eq("123456"))).thenReturn(true);
            when(channelMapper.selectCount(any())).thenReturn(0L); // 无其他通道

            ServiceException ex = assertThrows(ServiceException.class, () -> service.unbindChannel(1L, "phone", "123456", null));
            assertEquals("主通道不能单独解绑，请先绑定其他通道", ex.getMessage());
        }

        @Test
        @DisplayName("通道不存在应拒绝")
        void shouldRejectNotFound()
        {
            when(channelMapper.selectOne(any())).thenReturn(null);

            ServiceException ex = assertThrows(ServiceException.class, () -> service.unbindChannel(1L, "phone", "123456", null));
            assertEquals("通道不存在", ex.getMessage());
        }

        @Test
        @DisplayName("验证码错误应拒绝")
        void shouldRejectWrongCode()
        {
            IamUserChannel ch = mockPhoneChannel();
            when(channelMapper.selectOne(any())).thenReturn(ch);
            when(verifyCodeService.verifyCode(eq("phone"), eq("13800138000"), eq("wrong"))).thenReturn(false);

            ServiceException ex = assertThrows(ServiceException.class, () -> service.unbindChannel(1L, "phone", "wrong", null));
            assertEquals("验证码错误", ex.getMessage());
        }
    }

    // ==================== listChannels ====================

    @Nested
    @DisplayName("listChannels")
    class ListChannelsTests
    {
        @Test
        @DisplayName("正常返回通道列表")
        void shouldReturnChannels()
        {
            IamUserChannel ch = new IamUserChannel();
            ch.setId(1L);
            ch.setChannelType("phone");
            ch.setChannelAccount("13800138000");
            ch.setIsPrimary("1");
            ch.setBindTime(new Date());
            ch.setStatus("0");

            when(channelMapper.selectList(any())).thenReturn(Collections.singletonList(ch));

            List<ChannelResponse> channels = service.listChannels(1L);

            assertEquals(1, channels.size());
            assertEquals(1L, channels.get(0).getId());
            assertTrue(channels.get(0).getChannelAccount().contains("***"));
            assertEquals("1", channels.get(0).getIsPrimary());
        }

        @Test
        @DisplayName("空列表返回空数组")
        void shouldReturnEmpty()
        {
            when(channelMapper.selectList(any())).thenReturn(Collections.emptyList());

            List<ChannelResponse> channels = service.listChannels(1L);

            assertNotNull(channels);
            assertTrue(channels.isEmpty());
        }

        @Test
        @DisplayName("通道脱敏")
        void shouldMaskChannels()
        {
            IamUserChannel ch = new IamUserChannel();
            ch.setId(1L);
            ch.setChannelType("phone");
            ch.setChannelAccount("13800138000");
            ch.setIsPrimary("1");
            ch.setStatus("0");

            IamUserChannel emailCh = new IamUserChannel();
            emailCh.setId(2L);
            emailCh.setChannelType("email");
            emailCh.setChannelAccount("test@example.com");
            emailCh.setIsPrimary("0");
            emailCh.setStatus("0");

            when(channelMapper.selectList(any())).thenReturn(java.util.List.of(ch, emailCh));

            List<ChannelResponse> channels = service.listChannels(1L);

            assertEquals(2, channels.size());
            assertTrue(channels.get(0).getChannelAccount().contains("***"));
            assertTrue(channels.get(1).getChannelAccount().contains("**"));
        }

        @Test
        @DisplayName("解绑的通道不返回")
        void shouldNotReturnUnboundChannels()
        {
            IamUserChannel ch = new IamUserChannel();
            ch.setId(1L);
            ch.setChannelType("phone");
            ch.setChannelAccount("13800138000");
            ch.setIsPrimary("1");
            ch.setStatus("0");

            IamUserChannel unboundCh = new IamUserChannel();
            unboundCh.setId(2L);
            unboundCh.setChannelType("email");
            unboundCh.setChannelAccount("test@example.com");
            unboundCh.setIsPrimary("0");
            unboundCh.setStatus("1"); // unbound

            when(channelMapper.selectList(any())).thenReturn(java.util.List.of(ch));
            // SQL 层已通过 .eq(Status, "0") 过滤了解绑通道

            List<ChannelResponse> channels = service.listChannels(1L);

            assertEquals(1, channels.size());
            assertEquals("phone", channels.get(0).getChannelType());
        }
    }

    // ==================== queryBackupContact ====================

    @Nested
    @DisplayName("queryBackupContact")
    class QueryBackupContactTests
    {
        @Test
        @DisplayName("有备用联系方式返回脱敏值")
        void shouldReturnMaskedBackupContact()
        {
            com.ruoyi.iam.entity.IamUserBackupContact backup = new com.ruoyi.iam.entity.IamUserBackupContact();
            backup.setId(1L);
            backup.setUserId(1L);
            backup.setBakPhone("13900139000");
            backup.setBakEmail("backup@example.com");
            when(backupContactMapper.selectByUserId(anyLong())).thenReturn(backup);

            com.ruoyi.iam.dto.response.BackupContactResponse resp = service.queryBackupContact(1L);

            assertNotNull(resp);
            assertTrue(resp.isHasBakPhone());
            assertTrue(resp.isHasBakEmail());
            assertTrue(resp.getBakPhone().contains("***"));
            assertTrue(resp.getBakEmail().contains("**"));
        }

        @Test
        @DisplayName("无备用联系方式返回空")
        void shouldReturnNullWhenNoBackup()
        {
            when(backupContactMapper.selectByUserId(anyLong())).thenReturn(null);

            com.ruoyi.iam.dto.response.BackupContactResponse resp = service.queryBackupContact(999L);

            assertNull(resp);
        }

        @Test
        @DisplayName("仅有 bakPhone")
        void shouldReturnOnlyBakPhone()
        {
            com.ruoyi.iam.entity.IamUserBackupContact backup = new com.ruoyi.iam.entity.IamUserBackupContact();
            backup.setId(1L);
            backup.setUserId(1L);
            backup.setBakPhone("13900139000");
            backup.setBakEmail(null);
            when(backupContactMapper.selectByUserId(any())).thenReturn(backup);

            com.ruoyi.iam.dto.response.BackupContactResponse resp = service.queryBackupContact(1L);

            assertTrue(resp.isHasBakPhone());
            assertFalse(resp.isHasBakEmail());
        }

        @Test
        @DisplayName("仅有 bakEmail")
        void shouldReturnOnlyBakEmail()
        {
            com.ruoyi.iam.entity.IamUserBackupContact backup = new com.ruoyi.iam.entity.IamUserBackupContact();
            backup.setId(1L);
            backup.setUserId(1L);
            backup.setBakPhone(null);
            backup.setBakEmail("backup@example.com");
            when(backupContactMapper.selectByUserId(any())).thenReturn(backup);

            com.ruoyi.iam.dto.response.BackupContactResponse resp = service.queryBackupContact(1L);

            assertFalse(resp.isHasBakPhone());
            assertTrue(resp.isHasBakEmail());
        }
    }
}
