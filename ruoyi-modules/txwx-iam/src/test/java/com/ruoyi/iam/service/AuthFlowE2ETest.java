/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.iam.controller.AuthController;
import com.ruoyi.iam.controller.ChannelController;
import com.ruoyi.iam.controller.UserController;
import com.ruoyi.iam.dto.*;
import com.ruoyi.iam.entity.IamAuthLog;
import com.ruoyi.iam.entity.IamUser;
import com.ruoyi.iam.entity.IamUserChannel;
import com.ruoyi.iam.mapper.IamUserProductMapper;
import com.ruoyi.system.api.RemoteUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 端到端认证流程测试 — 集成真实 TokenService
 *
 * @author txwx
 */
@DisplayName("AuthFlowE2ETest")
@ExtendWith(MockitoExtension.class)
class AuthFlowE2ETest
{
    @Mock
    private VerifyCodeService verifyCodeService;

    @Mock
    private com.ruoyi.iam.mapper.IamUserMapper userMapper;

    @Mock
    private com.ruoyi.iam.mapper.IamUserChannelMapper channelMapper;

    @Mock
    private com.ruoyi.iam.mapper.IamAuthLogMapper authLogMapper;

    @Mock
    private com.ruoyi.iam.mapper.IamUserBackupContactMapper backupContactMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private com.ruoyi.iam.service.DeactivateService deactivateService;

    @Mock
    private org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

    @Mock
    private org.springframework.data.redis.core.ValueOperations<String, String> valueOperations;

    @Mock
    private com.ruoyi.iam.service.UserEventPublisher userEventPublisher;

    @Mock
    private com.ruoyi.common.redis.service.RedisService redisService;

    @Mock
    private RemoteUserService remoteUserService;

    @Mock
    private IamUserProductMapper iamUserProductMapper;

    @Mock
    private IamValidateCodeService validateCodeService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final byte[] secret = "test-secret-key-for-jwt-signing-must-be-long-enough".getBytes(StandardCharsets.UTF_8);

    private TokenService tokenService;
    private AuthService authService;
    private AuthController authController;
    private MockMvc mockMvc;

    private String createTestAccessToken(Long userId, String username)
    {
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .setIssuer("test")
            .setIssuedAt(new java.util.Date())
            .setExpiration(new java.util.Date(System.currentTimeMillis() + 7200000L))
            .claim("userId", userId)
            .claim("username", username)
            .claim("product_line", "spacemv-coai")
            .claim("channels", new String[]{})
            .claim("type", "access")
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact();
    }

    private String createTestRefreshToken(Long userId)
    {
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .setIssuer("test")
            .setIssuedAt(new java.util.Date())
            .setExpiration(new java.util.Date(System.currentTimeMillis() + 2592000000L))
            .claim("userId", userId)
            .claim("type", "refresh")
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact();
    }

    private IamUser mockActiveUser(Long id)
    {
        IamUser user = new IamUser();
        user.setId(id);
        user.setUsername("spmv_test");
        user.setPasswordHash("$2a$10$encoded");
        user.setDisplayName("13800138000");
        user.setStatus("0");
        user.setDeleteStatus("0");
        user.setCreateTime(new java.util.Date());
        user.setUpdateTime(new java.util.Date());
        user.setDelFlag("0");
        return user;
    }

    private IamUserChannel mockChannel()
    {
        IamUserChannel ch = new IamUserChannel();
        ch.setId(1L);
        ch.setUserId(1L);
        ch.setChannelType("phone");
        ch.setChannelAccount("13800138000");
        ch.setIsPrimary("1");
        ch.setStatus("0");
        ch.setBindTime(new java.util.Date());
        return ch;
    }

    @BeforeEach
    void setUp()
    {
        tokenService = org.mockito.Mockito.spy(new TokenService("test-secret-key-for-jwt-signing-must-be-long-enough", 7200L, 2592000L));
        authService = new AuthService(userMapper, channelMapper, backupContactMapper, authLogMapper, passwordEncoder,
            tokenService, verifyCodeService, redisTemplate, userEventPublisher, redisService, remoteUserService, iamUserProductMapper, validateCodeService,"test-secret-key-for-jwt-signing", "secret", 7200L);
        authController = new AuthController(authService, tokenService, deactivateService, validateCodeService);

        UserController userController = new UserController(authService);
        ChannelController channelController = new ChannelController(
            new UserChannelService(channelMapper, backupContactMapper, verifyCodeService, iamUserProductMapper, remoteUserService), authService);

        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        mockMvc = MockMvcBuilders.standaloneSetup(authController, userController, channelController)
            .setControllerAdvice(new com.ruoyi.iam.controller.GlobalExceptionHandler())
            .build();
    }

    @Test
    @DisplayName("注册→登录→获取用户信息→改资料→改密码→注销→取消注销")
    void shouldCompleteFullFlow() throws Exception {
        // 注册准备
        when(verifyCodeService.verifyCode(eq("phone"), eq("13800138000"), eq("123456"))).thenReturn(true);
        when(channelMapper.selectCount(any())).thenReturn(0L);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encoded");
        doAnswer(inv -> {
            IamUser user = inv.getArgument(0);
            user.setId(1L);
            return null;
        }).when(userMapper).insert(any(IamUser.class));
        when(authLogMapper.insert(any(IamAuthLog.class))).thenReturn(1);

        // 注册
        RegisterRequest req = new RegisterRequest();
        req.setChannelAccount("13800138000");
        req.setChannelType("phone");
        req.setVerifyCode("123456");
        req.setPassword("password123");

        LoginResponse registerResp = authController.authService.register(req);
        assertNotNull(registerResp.getAccessToken());
        assertEquals(1L, registerResp.getUserId());
        assertTrue(registerResp.getUsername().startsWith("spmv_"));

        // 登录准备
        IamUser user = mockActiveUser(1L);
        IamUserChannel channel = mockChannel();
        when(channelMapper.selectOne(any())).thenReturn(channel);
        when(userMapper.selectById(1L)).thenReturn(user);
        when(userMapper.updateById(any())).thenReturn(1);
        lenient().when(passwordEncoder.matches("123456", "$2a$10$encoded")).thenReturn(true);
        // 不锁定
        lenient().when(valueOperations.get(anyString())).thenReturn(null);
        // login() 会将 channelAccount 设为 user.getUsername()（"spmv_test"）后再调 smsLogin
        lenient().when(verifyCodeService.verifyCode(eq("phone"), anyString(), eq("123456"))).thenReturn(true);

        // 登录（短信）
        LoginRequest loginReq = new LoginRequest();
        loginReq.setChannelAccount("13800138000");
        loginReq.setCredential("123456");
        loginReq.setLoginType("sms");

        LoginResponse loginResp = authController.authService.login(loginReq);
        assertNotNull(loginResp.getAccessToken());
        assertEquals(1L, loginResp.getUserId());

        // 获取用户信息
        String token = loginResp.getAccessToken();
        lenient().when(passwordEncoder.matches("newpass123", "$2a$10$encoded")).thenReturn(false);

        UserLoginUserDTO me = authController.authService.getLoginUser(token);
        assertNotNull(me);
        assertEquals(1L, me.getUserId());

        // 改资料
        ProfileUpdateRequest profileReq = new ProfileUpdateRequest();
        profileReq.setDisplayName("新昵称");
        authController.authService.updateProfile(1L, profileReq);

        // 改密码
        PasswordChangeRequest pwdReq = new PasswordChangeRequest();
        pwdReq.setOldPassword("password123");
        pwdReq.setNewPassword("newpass123");

        // 注销
        DeactivateRequest deactReq = new DeactivateRequest();
        deactReq.setPassword("newpass123");

        // 取消注销
        deactivateService.cancelDeactivate(1L);
    }

    @Test
    @DisplayName("token 过期应被拒绝")
    void shouldRejectExpiredToken() throws Exception
    {
        String expiredToken = Jwts.builder()
            .setSubject("1")
            .setIssuedAt(new java.util.Date(System.currentTimeMillis() - 7200000L))
            .setExpiration(new java.util.Date(System.currentTimeMillis() - 7199000L))
            .claim("userId", 1L)
            .claim("type", "access")
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact();

        mockMvc.perform(get("/auth/v1/user/me")
                .header("Authorization", "Bearer " + expiredToken))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("刷新 token 返回新 token")
    void shouldRefreshToken() throws Exception
    {
        String refreshToken = createTestRefreshToken(1L);

        mockMvc.perform(post("/auth/v1/token/refresh")
                .header("Authorization", "Bearer " + refreshToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    @DisplayName("绑定通道流程")
    void shouldBindChannel() throws Exception
    {
        String token = createTestAccessToken(1L, "spmv_test");
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("1");
        when(tokenService.parseToken(token)).thenReturn(claims);
        when(verifyCodeService.verifyCode(eq("phone"), eq("13800138000"), eq("123456"))).thenReturn(true);
        when(channelMapper.selectCount(any())).thenReturn(0L);
        doAnswer(inv -> {
            IamUserChannel ch = inv.getArgument(0);
            ch.setId(2L);
            return null;
        }).when(channelMapper).insert(any(IamUserChannel.class));

        ChannelBindRequest req = new ChannelBindRequest();
        req.setChannelType("phone");
        req.setChannelAccount("13800138000");
        req.setVerifyCode("123456");

        mockMvc.perform(post("/auth/v1/user/channel")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").value("绑定成功"));
    }

    @Test
    @DisplayName("重复注册同一通道应失败")
    void shouldRejectDuplicateRegistration()
    {
        when(verifyCodeService.verifyCode(eq("phone"), eq("13800138000"), eq("123456"))).thenReturn(true);
        when(channelMapper.selectCount(any())).thenReturn(1L);

        RegisterRequest req = new RegisterRequest();
        req.setChannelAccount("13800138000");
        req.setChannelType("phone");
        req.setVerifyCode("123456");
        req.setPassword("password123");

        com.ruoyi.common.core.exception.ServiceException ex = assertThrows(
            com.ruoyi.common.core.exception.ServiceException.class, () -> authController.authService.register(req));
        assertEquals("该phone已被注册", ex.getMessage());
    }

    @Test
    @DisplayName("自定义用户名注册→用户名登录")
    void shouldRegisterWithCustomUsernameAndLoginByUsername() throws Exception {
        // 注册
        when(verifyCodeService.verifyCode(eq("phone"), eq("13800138000"), eq("123456"))).thenReturn(true);
        when(channelMapper.selectCount(any())).thenReturn(0L);
        when(userMapper.selectCount(any())).thenReturn(0L); // username 唯一
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encoded");
        doAnswer(inv -> {
            IamUser user = inv.getArgument(0);
            user.setId(1L);
            return null;
        }).when(userMapper).insert(any(IamUser.class));
        when(authLogMapper.insert(any(IamAuthLog.class))).thenReturn(1);

        RegisterRequest req = new RegisterRequest();
        req.setChannelAccount("13800138000");
        req.setChannelType("phone");
        req.setVerifyCode("123456");
        req.setPassword("password123");
        req.setUsername("custom_user");

        LoginResponse resp = authController.authService.register(req);
        assertEquals("custom_user", resp.getUsername());
        assertNotNull(resp.getAccessToken());

        // 用户名登录
        IamUser user = mockActiveUser(1L);
        user.setUsername("custom_user");
        IamUserChannel ch = mock(IamUserChannel.class);
        lenient().when(ch.getId()).thenReturn(1L);
        lenient().when(ch.getUserId()).thenReturn(1L);
        lenient().when(ch.getChannelType()).thenReturn("phone");
        lenient().when(ch.getChannelAccount()).thenReturn("13800138000");
        lenient().when(ch.getIsPrimary()).thenReturn("1");
        lenient().when(ch.getBindTime()).thenReturn(new java.util.Date());
        lenient().when(ch.getStatus()).thenReturn("0");
        lenient().when(channelMapper.selectOne(any())).thenReturn(ch); // findByChannel 返回通道
        lenient().when(userMapper.selectOne(any())).thenReturn(user);  // findByUsername 找到用户
        lenient().when(userMapper.selectById(1L)).thenReturn(user);    // findByChannel 后查用户
        when(passwordEncoder.matches("password123", "$2a$10$encoded")).thenReturn(true);
        when(userMapper.updateById(any())).thenReturn(1);
        lenient().when(tokenService.createAccessToken(any(), anyString(), anyString(), any())).thenReturn("token");
        when(tokenService.createRefreshToken(any())).thenReturn("token");

        LoginRequest loginReq = new LoginRequest();
        loginReq.setChannelAccount("custom_user");
        loginReq.setCredential("password123");
        loginReq.setLoginType("password");

        LoginResponse loginResp = authController.authService.login(loginReq);
        assertEquals("custom_user", loginResp.getUsername());
    }

    @Test
    @DisplayName("注册备用联系方式→查询备用联系")
    void shouldWriteAndQueryBackupContact() throws Exception {
        // 注册时写入备用联系方式
        when(verifyCodeService.verifyCode(eq("phone"), eq("13800138000"), eq("123456"))).thenReturn(true);
        when(channelMapper.selectCount(any())).thenReturn(0L);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encoded");
        doAnswer(inv -> {
            IamUser user = inv.getArgument(0);
            user.setId(1L);
            return null;
        }).when(userMapper).insert(any(IamUser.class));
        when(authLogMapper.insert(any(IamAuthLog.class))).thenReturn(1);
        doAnswer(inv -> {
            com.ruoyi.iam.entity.IamUserBackupContact bc = inv.getArgument(0);
            bc.setId(1L);
            return null;
        }).when(backupContactMapper).insert(any(com.ruoyi.iam.entity.IamUserBackupContact.class));

        RegisterRequest req = new RegisterRequest();
        req.setChannelAccount("13800138000");
        req.setChannelType("phone");
        req.setVerifyCode("123456");
        req.setPassword("password123");
        req.setBakEmail("backup@example.com");

        authController.authService.register(req);
        verify(backupContactMapper).insert(any(com.ruoyi.iam.entity.IamUserBackupContact.class));
    }

    @Test
    @DisplayName("密码过短应拒绝")
    void shouldRejectShortPassword()
    {
        when(verifyCodeService.verifyCode(any(), any(), any())).thenReturn(true);

        RegisterRequest req = new RegisterRequest();
        req.setChannelAccount("13800138000");
        req.setChannelType("phone");
        req.setVerifyCode("123456");
        req.setPassword("short");

        com.ruoyi.common.core.exception.ServiceException ex = assertThrows(
            com.ruoyi.common.core.exception.ServiceException.class, () -> authController.authService.register(req));
        assertEquals("密码长度需为 8-20 位", ex.getMessage());
    }
}
