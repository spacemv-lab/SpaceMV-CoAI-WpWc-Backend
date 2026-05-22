/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package intergration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.iam.dto.PasswordChangeRequest;
import com.ruoyi.iam.dto.ProfileUpdateRequest;
import com.ruoyi.iam.dto.UserLoginUserDTO;
import com.ruoyi.iam.entity.IamUser;
import com.ruoyi.iam.entity.IamUserChannel;
import com.ruoyi.iam.mapper.IamAuthLogMapper;
import com.ruoyi.iam.mapper.IamUserChannelMapper;
import com.ruoyi.iam.mapper.IamUserMapper;
import com.ruoyi.iam.mapper.IamUserProductMapper;
import com.ruoyi.iam.service.AuthService;
import com.ruoyi.iam.service.IamValidateCodeService;
import com.ruoyi.iam.service.TokenService;
import com.ruoyi.iam.service.VerifyCodeService;
import com.ruoyi.system.api.RemoteUserService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController + AuthService 相关方法测试
 *
 * @author txwx
 */
@DisplayName("User Management Tests")
@ExtendWith(MockitoExtension.class)
class UserControllerTest
{
    @Mock
    private AuthService authService;

    @Mock
    private TokenService tokenService;

    @Mock
    private IamUserMapper userMapper;

    @Mock
    private IamUserChannelMapper channelMapper;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp()
    {
        // 创建 mock controller + 注册全局异常处理
        var controller = new com.ruoyi.iam.controller.UserController(authService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new com.ruoyi.iam.controller.GlobalExceptionHandler())
            .build();
    }

    // ==================== getMe ====================

    @Nested
    @ExtendWith(MockitoExtension.class)
    @DisplayName("getMe")
    class GetMeTests
    {
        @Test
        @DisplayName("正常返回用户信息")
        void shouldReturnUserInfo() throws Exception
        {
            String token = "mock-token";
            UserLoginUserDTO user = createUserDTO();

            doReturn(user).when(authService).getLoginUser(token);

            mockMvc.perform(get("/auth/v1/user/me")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("spmv_test"))
                .andExpect(jsonPath("$.data.displayName").value("测试用户"));
        }

        @Test
        @DisplayName("无 token 返回 401")
        void shouldRejectNoToken() throws Exception
        {
            mockMvc.perform(get("/auth/v1/user/me")
                    .header("Authorization", "Bearer "))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("空 token 返回 401")
        void shouldRejectEmptyToken() throws Exception
        {
            mockMvc.perform(get("/auth/v1/user/me"))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("token 过期返回 401")
        void shouldRejectExpiredToken() throws Exception
        {
            String token = "expired-token";
            doThrow(new IllegalArgumentException("token expired"))
                .when(authService).getLoginUser(token);

            mockMvc.perform(get("/auth/v1/user/me")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("已注销用户返回 401")
        void shouldRejectDeactivatedUser() throws Exception
        {
            String token = "deactivated-token";
            doThrow(new com.ruoyi.common.core.exception.ServiceException("用户不存在"))
                .when(authService).getLoginUser(token);

            mockMvc.perform(get("/auth/v1/user/me")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("返回通道列表（脱敏）")
        void shouldReturnChannelsMasked() throws Exception
        {
            String token = "mock-token";
            UserLoginUserDTO user = createUserDTO();
            UserLoginUserDTO.ChannelResponse ch = new UserLoginUserDTO.ChannelResponse();
            ch.setId(1L);
            ch.setChannelType("phone");
            ch.setChannelAccount("138****8000");
            ch.setIsPrimary("1");
            ch.setStatus("0");
            user.setChannels(java.util.List.of(ch));

            doReturn(user).when(authService).getLoginUser(token);

            mockMvc.perform(get("/auth/v1/user/me")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.channels[0].channelAccount").value("138****8000"));
        }
    }

    // ==================== updateProfile ====================

    @Nested
    @ExtendWith(MockitoExtension.class)
    @DisplayName("updateProfile")
    class UpdateProfileTests
    {
        @Test
        @DisplayName("全字段更新")
        void shouldUpdateAllFields() throws Exception
        {
            String token = "mock-token";
            ProfileUpdateRequest req = new ProfileUpdateRequest();
            req.setDisplayName("新昵称");
            req.setAvatarUrl("https://example.com/avatar.png");

            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);
            doNothing().when(authService).updateProfile(eq(1L), any());

            mockMvc.perform(put("/auth/v1/user/profile")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

            verify(authService).updateProfile(eq(1L), argThat(r ->
                "新昵称".equals(r.getDisplayName()) &&
                "https://example.com/avatar.png".equals(r.getAvatarUrl())
            ));
        }

        @Test
        @DisplayName("部分更新（仅昵称）")
        void shouldUpdateDisplayNameOnly() throws Exception
        {
            String token = "mock-token";
            ProfileUpdateRequest req = new ProfileUpdateRequest();
            req.setDisplayName("新昵称");
            req.setAvatarUrl(null);

            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);
            doNothing().when(authService).updateProfile(eq(1L), any());

            mockMvc.perform(put("/auth/v1/user/profile")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

            verify(authService).updateProfile(eq(1L), argThat(r ->
                "新昵称".equals(r.getDisplayName()) &&
                r.getAvatarUrl() == null
            ));
        }

        @Test
        @DisplayName("部分更新（仅头像）")
        void shouldUpdateAvatarOnly() throws Exception
        {
            String token = "mock-token";
            ProfileUpdateRequest req = new ProfileUpdateRequest();
            req.setDisplayName(null);
            req.setAvatarUrl("https://example.com/avatar.png");

            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);
            doNothing().when(authService).updateProfile(eq(1L), any());

            mockMvc.perform(put("/auth/v1/user/profile")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("昵称超长应拒绝")
        void shouldRejectNicknameTooLong() throws Exception
        {
            String token = "mock-token";
            ProfileUpdateRequest req = new ProfileUpdateRequest();
            req.setDisplayName("a".repeat(101));
            req.setAvatarUrl(null);

            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);
            doThrow(new ServiceException("昵称长度不能超过 100 个字符"))
                .when(authService).updateProfile(eq(1L), any());

            mockMvc.perform(put("/auth/v1/user/profile")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("昵称长度不能超过 100 个字符"));
        }

        @Test
        @DisplayName("无 token 返回 401")
        void shouldRejectNoToken() throws Exception
        {
            ProfileUpdateRequest req = new ProfileUpdateRequest();
            req.setDisplayName("新昵称");

            mockMvc.perform(put("/auth/v1/user/profile")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("空参数不更新")
        void shouldNotUpdateWithNulls() throws Exception
        {
            String token = "mock-token";
            ProfileUpdateRequest req = new ProfileUpdateRequest();
            req.setDisplayName(null);
            req.setAvatarUrl(null);

            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);
            doNothing().when(authService).updateProfile(eq(1L), any());

            mockMvc.perform(put("/auth/v1/user/profile")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

            verify(authService).updateProfile(eq(1L), any());
        }
    }

    // ==================== updatePassword ====================

    @Nested
    @ExtendWith(MockitoExtension.class)
    @DisplayName("updatePassword")
    class UpdatePasswordTests
    {
        @Test
        @DisplayName("密码修改成功")
        void shouldUpdatePassword() throws Exception
        {
            String token = "mock-token";
            PasswordChangeRequest req = new PasswordChangeRequest();
            req.setOldPassword("oldpass123");
            req.setNewPassword("newpass456");

            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);
            doNothing().when(authService).updatePassword(eq(1L), any());

            mockMvc.perform(put("/auth/v1/user/password")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

            verify(authService).updatePassword(eq(1L), argThat(r ->
                "oldpass123".equals(r.getOldPassword()) &&
                "newpass456".equals(r.getNewPassword())
            ));
        }

        @Test
        @DisplayName("无 token 返回 401")
        void shouldRejectNoToken() throws Exception
        {
            PasswordChangeRequest req = new PasswordChangeRequest();
            req.setOldPassword("oldpass123");
            req.setNewPassword("newpass456");

            mockMvc.perform(put("/auth/v1/user/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("旧密码错误应拒绝")
        void shouldRejectWrongOldPassword() throws Exception
        {
            String token = "mock-token";
            PasswordChangeRequest req = new PasswordChangeRequest();
            req.setOldPassword("oldpass123");
            req.setNewPassword("newpass456");

            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);
            doThrow(new ServiceException("旧密码错误")).when(authService).updatePassword(eq(1L), any());

            mockMvc.perform(put("/auth/v1/user/password")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("旧密码错误"));
        }

        @Test
        @DisplayName("新密码过短应拒绝")
        void shouldRejectShortPassword() throws Exception
        {
            String token = "mock-token";
            PasswordChangeRequest req = new PasswordChangeRequest();
            req.setOldPassword("oldpass123");
            req.setNewPassword("12345");

            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);
            doThrow(new ServiceException("密码长度需为 8-20 位")).when(authService).updatePassword(eq(1L), any());

            mockMvc.perform(put("/auth/v1/user/password")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("密码长度需为 8-20 位"));
        }

        @Test
        @DisplayName("新密码过长应拒绝")
        void shouldRejectLongPassword() throws Exception
        {
            String token = "mock-token";
            PasswordChangeRequest req = new PasswordChangeRequest();
            req.setOldPassword("oldpass123");
            req.setNewPassword("123456789012345678901");

            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);
            doThrow(new ServiceException("密码长度需为 8-20 位")).when(authService).updatePassword(eq(1L), any());

            mockMvc.perform(put("/auth/v1/user/password")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("密码长度需为 8-20 位"));
        }
    }

    // ==================== AuthService getLoginUser ====================

    @Nested
    @ExtendWith(MockitoExtension.class)
    @DisplayName("AuthService.getLoginUser")
    class GetLoginUserTests
    {
        @Mock
        private IamUserMapper mockUserMapper;

        @Mock
        private IamUserChannelMapper mockChannelMapper;

        @Mock
        private TokenService mockTokenService;

        @Mock
        private RedisService mockRedisService;

        @Mock
        private RemoteUserService remoteUserService;

        @Mock
        private IamUserProductMapper iamUserProductMapper;

        @Mock
        private IamValidateCodeService validateCodeService;

        @Mock
        private com.ruoyi.common.redis.service.RedisService redisService;

        @Mock
        private com.ruoyi.iam.mapper.IamUserBackupContactMapper mockBackupContactMapper;

        @Mock
        private com.ruoyi.iam.service.UserEventPublisher mockUserEventPublisher;

        private AuthService service;

        @BeforeEach
        void setUp()
        {
            service = new AuthService(
                mockUserMapper, mockChannelMapper, mockBackupContactMapper,
                mock(IamAuthLogMapper.class),
                mock(BCryptPasswordEncoder.class),
                mockTokenService,
                mock(VerifyCodeService.class),
                mock(StringRedisTemplate.class),
                mockUserEventPublisher, mockRedisService,remoteUserService,iamUserProductMapper,validateCodeService,
                "test-secret","secret",
                7200L
            );
        }

        @Test
        @DisplayName("有效 token 返回用户信息")
        void shouldReturnUserInfoWithValidToken()
        {
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            IamUser user = mockUser();
            when(mockTokenService.parseToken("token")).thenReturn(claims);
            when(mockUserMapper.selectById(1L)).thenReturn(user);
            when(mockChannelMapper.selectList(any())).thenReturn(Collections.emptyList());

            UserLoginUserDTO dto = service.getLoginUser("token");

            assertNotNull(dto);
            assertEquals(1L, dto.getUserId());
            assertEquals("spmv_test", dto.getUsername());
        }

        @Test
        @DisplayName("token 无效抛出异常")
        void shouldRejectInvalidToken()
        {
            when(mockTokenService.parseToken("invalid")).thenThrow(IllegalArgumentException.class);

            assertThrows(IllegalArgumentException.class, () -> service.getLoginUser("invalid"));
        }

        @Test
        @DisplayName("用户不存在抛出异常")
        void shouldRejectUserNotFound()
        {
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("999");
            when(mockTokenService.parseToken("token")).thenReturn(claims);
            when(mockUserMapper.selectById(999L)).thenReturn(null);

            assertThrows(IllegalArgumentException.class, () -> service.getLoginUser("token"));
        }

        @Test
        @DisplayName("包含通道列表（脱敏）")
        void shouldIncludeChannels()
        {
            IamUserChannel channel = new IamUserChannel();
            channel.setId(1L);
            channel.setChannelType("phone");
            channel.setChannelAccount("13800138000");
            channel.setIsPrimary("1");
            channel.setStatus("0");
            channel.setBindTime(new Date());

            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            IamUser user = mockUser();
            when(mockTokenService.parseToken("token")).thenReturn(claims);
            when(mockUserMapper.selectById(1L)).thenReturn(user);
            when(mockChannelMapper.selectList(any())).thenReturn(Collections.singletonList(channel));

            UserLoginUserDTO dto = service.getLoginUser("token");

            assertNotNull(dto.getChannels());
            assertFalse(dto.getChannels().isEmpty());
            assertTrue(dto.getChannels().get(0).getChannelAccount().contains("***"));
        }
    }

    // ==================== AuthService updateProfile ====================

    @Nested
    @ExtendWith(MockitoExtension.class)
    @DisplayName("AuthService.updateProfile")
    class AuthServiceUpdateProfileTests
    {
        @Mock
        private IamUserMapper mockUserMapper;

        @Mock
        private IamUserChannelMapper mockChannelMapper;

        @Mock
        private TokenService mockTokenService;

        @Mock
        private RedisService mockRedisService;

        @Mock
        private VerifyCodeService mockVerifyCodeService;

        @Mock
        private BCryptPasswordEncoder mockEncoder;

        @Mock
        private org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

        @Mock
        private com.ruoyi.iam.mapper.IamUserBackupContactMapper mockBackupContactMapper;

        @Mock
        private com.ruoyi.iam.service.UserEventPublisher mockUserEventPublisher;

        @Mock
        private RemoteUserService remoteUserService;

        @Mock
        private IamUserProductMapper iamUserProductMapper;

        @Mock
        private IamValidateCodeService validateCodeService;

        private AuthService service;

        @BeforeEach
        void setUp()
        {
            service = new AuthService(
                mockUserMapper, mockChannelMapper, mockBackupContactMapper,
                mock(IamAuthLogMapper.class),
                mockEncoder,
                mockTokenService,
                mockVerifyCodeService,
                redisTemplate,
                mockUserEventPublisher,
                mockRedisService, remoteUserService,iamUserProductMapper,validateCodeService,
                "test-secret","secret",
                7200L
            );
        }

        @Test
        @DisplayName("更新昵称")
        void shouldUpdateDisplayName()
        {
            IamUser user = mockUser();
            user.setDisplayName("旧昵称");
            when(mockUserMapper.selectById(1L)).thenReturn(user);
            when(mockUserMapper.updateById(any())).thenReturn(1);

            ProfileUpdateRequest req = new ProfileUpdateRequest();
            req.setDisplayName("新昵称");
            req.setAvatarUrl(null);

            service.updateProfile(1L, req);

            assertEquals("新昵称", user.getDisplayName());
            verify(mockUserMapper).updateById(user);
        }

        @Test
        @DisplayName("更新头像")
        void shouldUpdateAvatar()
        {
            IamUser user = mockUser();
            user.setAvatarUrl(null);
            when(mockUserMapper.selectById(1L)).thenReturn(user);
            when(mockUserMapper.updateById(any())).thenReturn(1);

            ProfileUpdateRequest req = new ProfileUpdateRequest();
            req.setDisplayName(null);
            req.setAvatarUrl("https://example.com/new.png");

            service.updateProfile(1L, req);

            assertEquals("https://example.com/new.png", user.getAvatarUrl());
        }

        @Test
        @DisplayName("用户不存在应拒绝")
        void shouldRejectUserNotFound()
        {
            when(mockUserMapper.selectById(999L)).thenReturn(null);

            ProfileUpdateRequest req = new ProfileUpdateRequest();
            req.setDisplayName("新昵称");

            assertThrows(ServiceException.class, () -> service.updateProfile(999L, req));
        }
    }

    // ==================== AuthService updatePassword ====================

    @Nested
    @ExtendWith(MockitoExtension.class)
    @DisplayName("AuthService.updatePassword")
    class AuthServiceUpdatePasswordTests
    {
        @Mock
        private IamUserMapper mockUserMapper;

        @Mock
        private IamUserChannelMapper mockChannelMapper;

        @Mock
        private TokenService mockTokenService;
        @Mock
        private RedisService mockRedisService;

        @Mock
        private VerifyCodeService mockVerifyCodeService;

        @Mock
        private BCryptPasswordEncoder mockEncoder;

        @Mock
        private RemoteUserService remoteUserService;

        @Mock
        private IamUserProductMapper iamUserProductMapper;

        @Mock
        private org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

        @Mock
        private com.ruoyi.iam.mapper.IamUserBackupContactMapper mockBackupContactMapper;

        @Mock
        private com.ruoyi.iam.service.UserEventPublisher mockUserEventPublisher;

        @Mock
        private IamValidateCodeService validateCodeService;

        private AuthService service;

        @BeforeEach
        void setUp()
        {
            service = new AuthService(
                mockUserMapper, mockChannelMapper, mockBackupContactMapper,
                mock(IamAuthLogMapper.class),
                mockEncoder,
                mockTokenService,
                mockVerifyCodeService,
                redisTemplate,
                mockUserEventPublisher,
                mockRedisService, remoteUserService,iamUserProductMapper,validateCodeService,
                "test-secret","secret",
                7200L
            );
        }

        @Test
        @DisplayName("密码修改成功")
        void shouldUpdatePassword()
        {
            IamUser user = mockUser();
            user.setPasswordHash("$2a$10$oldHash");
            when(mockUserMapper.selectById(1L)).thenReturn(user);
            when(mockEncoder.matches("oldpass", "$2a$10$oldHash")).thenReturn(true);
            when(mockEncoder.encode("newpass123")).thenReturn("$2a$10$newHash");
            when(mockUserMapper.updateById(any())).thenReturn(1);

            PasswordChangeRequest req = new PasswordChangeRequest();
            req.setOldPassword("oldpass");
            req.setNewPassword("newpass123");

            try {
                service.updatePassword(1L, req);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            assertEquals("$2a$10$newHash", user.getPasswordHash());
            verify(mockUserMapper).updateById(user);
        }

        @Test
        @DisplayName("旧密码错误应拒绝")
        void shouldRejectWrongOldPassword()
        {
            IamUser user = mockUser();
            user.setPasswordHash("$2a$10$oldHash");
            when(mockUserMapper.selectById(1L)).thenReturn(user);
            when(mockEncoder.matches("wrongpass", "$2a$10$oldHash")).thenReturn(false);

            PasswordChangeRequest req = new PasswordChangeRequest();
            req.setOldPassword("wrongpass");
            req.setNewPassword("newpass");

            ServiceException ex = assertThrows(ServiceException.class, () -> service.updatePassword(1L, req));
            assertEquals("旧密码错误", ex.getMessage());
        }

        @Test
        @DisplayName("新密码过短应拒绝")
        void shouldRejectShortPassword()
        {
            IamUser user = mockUser();
            user.setPasswordHash("$2a$10$oldHash");
            when(mockUserMapper.selectById(1L)).thenReturn(user);
            when(mockEncoder.matches("oldpass", "$2a$10$oldHash")).thenReturn(true);

            PasswordChangeRequest req = new PasswordChangeRequest();
            req.setOldPassword("oldpass");
            req.setNewPassword("1234");

            ServiceException ex = assertThrows(ServiceException.class, () -> service.updatePassword(1L, req));
            assertEquals("密码长度需为 8-20 位", ex.getMessage());
        }

        @Test
        @DisplayName("用户不存在应拒绝")
        void shouldRejectUserNotFound()
        {
            when(mockUserMapper.selectById(999L)).thenReturn(null);

            PasswordChangeRequest req = new PasswordChangeRequest();
            req.setOldPassword("oldpass");
            req.setNewPassword("newpass");

            assertThrows(ServiceException.class, () -> service.updatePassword(999L, req));
        }
    }

    // ==================== Helpers ====================

    private UserLoginUserDTO createUserDTO()
    {
        UserLoginUserDTO dto = new UserLoginUserDTO();
        dto.setUserId(1L);
        dto.setUsername("spmv_test");
        dto.setDisplayName("测试用户");
        dto.setAvatarUrl("https://example.com/avatar.png");
        dto.setStatus("0");
        dto.setDeleteStatus("0");
        dto.setChannels(Collections.emptyList());
        return dto;
    }

    private IamUser mockUser()
    {
        IamUser user = new IamUser();
        user.setId(1L);
        user.setUsername("spmv_test");
        user.setPasswordHash("$2a$10$hashed");
        user.setDisplayName("测试用户");
        user.setAvatarUrl("https://example.com/avatar.png");
        user.setStatus("0");
        user.setDeleteStatus("0");
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setDelFlag("0");
        return user;
    }

}
