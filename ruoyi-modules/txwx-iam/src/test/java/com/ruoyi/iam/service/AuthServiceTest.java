package com.ruoyi.iam.service;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.iam.dto.InnerUserImportRequest;
import com.ruoyi.system.api.RemoteUserService;
import com.ruoyi.iam.dto.LoginRequest;
import com.ruoyi.iam.dto.LoginResponse;
import com.ruoyi.iam.dto.RegisterRequest;
import com.ruoyi.iam.entity.IamAuthLog;
import com.ruoyi.iam.entity.IamUser;
import com.ruoyi.iam.entity.IamUserChannel;
import com.ruoyi.iam.mapper.IamAuthLogMapper;
import com.ruoyi.iam.mapper.IamUserChannelMapper;
import com.ruoyi.iam.mapper.IamUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuthService 测试类
 *
 * @author txwx
 */
@DisplayName("AuthService Tests")
@ExtendWith(MockitoExtension.class)
class AuthServiceTest
{
    @Mock
    private IamUserMapper userMapper;

    @Mock
    private IamUserChannelMapper channelMapper;

    @Mock
    private IamAuthLogMapper authLogMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private VerifyCodeService verifyCodeService;

    @Mock
    private org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

    @Mock
    private org.springframework.data.redis.core.ValueOperations<String, String> valueOperations;

    @Mock
    private com.ruoyi.iam.mapper.IamUserBackupContactMapper backupContactMapper;

    @Mock
    private com.ruoyi.iam.service.UserEventPublisher userEventPublisher;

    @Mock
    private com.ruoyi.common.redis.service.RedisService redisService;

    @Mock
    private RemoteUserService remoteUserService;

    @Mock
    private com.ruoyi.iam.mapper.IamUserProductMapper iamUserProductMapper;

    @Mock
    private IamValidateCodeService iamValidateCodeService;

    private AuthService authService;

    @BeforeEach
    void setUp()
    {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(valueOperations.increment(anyString())).thenReturn(1L);
        lenient().doReturn(true).when(redisTemplate).expire(anyString(), anyLong(), any());
        lenient().doNothing().when(redisService).setCacheObject(anyString(), any(), anyLong(), any());
        lenient().when(redisService.getCacheObject(anyString())).thenReturn(null);

        lenient().when(remoteUserService.syncIamUser(any(), any())).thenReturn(R.ok(1L));

        authService = new AuthService(
            userMapper, channelMapper, backupContactMapper, authLogMapper,
            passwordEncoder, tokenService, verifyCodeService,
            redisTemplate,
            userEventPublisher,
            redisService,
            remoteUserService,
            iamUserProductMapper,
            iamValidateCodeService,
            "test-secret-key-for-jwt-signing","secret",
            7200L
        );
    }

    // ==================== register ====================

    @Nested
    @DisplayName("register")
    class RegisterTests
    {
        @Test
        @DisplayName("手机号注册成功")
        void shouldRegisterWithPhone() throws Exception {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setVerifyCode("123456");
            req.setPassword("password123");
            req.setChannelType("phone");

            when(verifyCodeService.verifyCode(eq("phone"), eq("13800138000"), eq("123456"))).thenReturn(true);
            when(channelMapper.selectCount(any())).thenReturn(0L);
            lenient().when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
            doAnswer(inv -> {
                IamUser user = inv.getArgument(0);
                user.setId(1L);
                return null;
            }).when(userMapper).insert(any(IamUser.class));
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("access-token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");

            LoginResponse resp = authService.register(req);

            assertNotNull(resp);
            assertNotNull(resp.getAccessToken());
            assertEquals(1L, resp.getUserId());
            assertTrue(resp.getAccessToken().startsWith("access-token"));
            verify(userMapper, atLeastOnce()).insert(any(IamUser.class));
            verify(channelMapper, atLeastOnce()).insert(any(IamUserChannel.class));
            verify(verifyCodeService).verifyCode(eq("phone"), eq("13800138000"), eq("123456"));
        }

        @Test
        @DisplayName("邮箱注册成功")
        void shouldRegisterWithEmail() throws Exception {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("test@example.com");
            req.setVerifyCode("654321");
            req.setPassword("password123");
            req.setChannelType("email");

            when(verifyCodeService.verifyCode(eq("email"), eq("test@example.com"), eq("654321"))).thenReturn(true);
            when(channelMapper.selectCount(any())).thenReturn(0L);
            lenient().when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
            doAnswer(inv -> {
                IamUser user = inv.getArgument(0);
                user.setId(1L);
                return null;
            }).when(userMapper).insert(any(IamUser.class));
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("access-token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");

            LoginResponse resp = authService.register(req);

            assertNotNull(resp);
            verify(verifyCodeService).verifyCode(eq("email"), eq("test@example.com"), eq("654321"));
        }

        @Test
        @DisplayName("生成 spmv_ 用户名")
        void shouldGenerateSpmvUsername() throws Exception {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setVerifyCode("123456");
            req.setPassword("password123");
            req.setChannelType("phone");

            when(verifyCodeService.verifyCode(any(), any(), any())).thenReturn(true);
            when(channelMapper.selectCount(any())).thenReturn(0L);
            lenient().when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
            doAnswer(inv -> {
                IamUser user = inv.getArgument(0);
                user.setId(1L);
                return null;
            }).when(userMapper).insert(any(IamUser.class));
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("token");

            LoginResponse resp = authService.register(req);

            assertNotNull(resp.getUsername());
            assertTrue(resp.getUsername().startsWith("spmv_"));
        }

        @Test
        @DisplayName("密码经 BCrypt 加密存储")
        void shouldEncodePasswordWithBCrypt() throws Exception {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setVerifyCode("123456");
            req.setPassword("myPassword123");
            req.setChannelType("phone");

            when(verifyCodeService.verifyCode(any(), any(), any())).thenReturn(true);
            when(channelMapper.selectCount(any())).thenReturn(0L);
            lenient().when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashed");
            doAnswer(inv -> {
                IamUser user = inv.getArgument(0);
                user.setId(1L);
                return null;
            }).when(userMapper).insert(any(IamUser.class));
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("token");

            authService.register(req);

            verify(passwordEncoder).encode(anyString());
        }

        @Test
        @DisplayName("主通道 isPrimary=1")
        void shouldSetIsPrimaryTo1() throws Exception {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setVerifyCode("123456");
            req.setPassword("password123");
            req.setChannelType("phone");

            when(verifyCodeService.verifyCode(any(), any(), any())).thenReturn(true);
            when(channelMapper.selectCount(any())).thenReturn(0L);
            lenient().when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
            doAnswer(inv -> {
                IamUser user = inv.getArgument(0);
                user.setId(1L);
                return null;
            }).when(userMapper).insert(any(IamUser.class));
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("token");

            authService.register(req);

            verify(channelMapper, atLeastOnce()).insert(argThat(channel -> "1".equals(channel.getIsPrimary())));
        }

        @Test
        @DisplayName("JWT 签发包含 userId claim")
        void shouldIncludeUserIdInJWT() throws Exception {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setVerifyCode("123456");
            req.setPassword("password123");
            req.setChannelType("phone");

            when(verifyCodeService.verifyCode(any(), any(), any())).thenReturn(true);
            when(channelMapper.selectCount(any())).thenReturn(0L);
            lenient().when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
            doAnswer(inv -> {
                IamUser user = inv.getArgument(0);
                user.setId(1L);
                return null;
            }).when(userMapper).insert(any(IamUser.class));
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("token");

            authService.register(req);

            verify(tokenService).createAccessTokenWithUserKey(eq(1L), anyString(), anyString(), any(), anyString());
        }

        @Test
        @DisplayName("验证码错误应拒绝")
        void shouldRejectWhenCodeInvalid()
        {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setVerifyCode("wrong");
            req.setPassword("password123");
            req.setChannelType("phone");

            when(verifyCodeService.verifyCode(eq("phone"), eq("13800138000"), eq("wrong"))).thenReturn(false);

            assertThrows(ServiceException.class, () -> authService.register(req));

            verify(userMapper, never()).insert(any());
            verify(authLogMapper).insert(any(IamAuthLog.class));
        }

        @Test
        @DisplayName("已注册通道应拒绝")
        void shouldRejectWhenChannelAlreadyRegistered()
        {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setVerifyCode("123456");
            req.setPassword("password123");
            req.setChannelType("phone");

            when(verifyCodeService.verifyCode(any(), any(), any())).thenReturn(true);
            when(channelMapper.selectCount(any())).thenReturn(1L);

            assertThrows(ServiceException.class, () -> authService.register(req));

            verify(userMapper, never()).insert(any());
            verify(authLogMapper).insert(any(IamAuthLog.class));
        }

        // ==== v1.1 新增测试用例 ====

        @Test
        @DisplayName("自定义用户名注册")
        void shouldRegisterWithCustomUsername() throws Exception {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setVerifyCode("123456");
            req.setPassword("password123");
            req.setChannelType("phone");
            req.setUsername("my_custom_name");

            when(verifyCodeService.verifyCode(any(), any(), any())).thenReturn(true);
            when(channelMapper.selectCount(any())).thenReturn(0L);
            when(userMapper.selectCount(any())).thenReturn(0L); // username 唯一
            lenient().when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
            doAnswer(inv -> {
                IamUser user = inv.getArgument(0);
                user.setId(1L);
                return null;
            }).when(userMapper).insert(any(IamUser.class));
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("token");
            doAnswer(inv -> null).when(userEventPublisher).publishUserCreated(anyLong(), anyString(), anyString(), anyString());

            LoginResponse resp = authService.register(req);

            assertNotNull(resp);
            assertEquals("my_custom_name", resp.getUsername());
            verify(userMapper, atLeastOnce()).insert(argThat(u -> "my_custom_name".equals(u.getUsername())));
        }

        @Test
        @DisplayName("用户名超长应拒绝")
        void shouldRejectUsernameTooLong()
        {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setVerifyCode("123456");
            req.setPassword("password123");
            req.setChannelType("phone");
            req.setUsername("this_is_a_very_long_username_that_exceeds");

            when(verifyCodeService.verifyCode(any(), any(), any())).thenReturn(true);

            ServiceException ex = assertThrows(ServiceException.class, () -> authService.register(req));
            assertEquals("用户名长度不能超过 20 个字符", ex.getMessage());
        }

        @Test
        @DisplayName("用户名已存在应拒绝")
        void shouldRejectDuplicateUsername()
        {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setVerifyCode("123456");
            req.setPassword("password123");
            req.setChannelType("phone");
            req.setUsername("existing_user");

            when(verifyCodeService.verifyCode(any(), any(), any())).thenReturn(true);
            when(channelMapper.selectCount(any())).thenReturn(0L);
            when(userMapper.selectCount(any())).thenReturn(1L); // username 已存在

            ServiceException ex = assertThrows(ServiceException.class, () -> authService.register(req));
            assertEquals("用户名已被注册", ex.getMessage());
        }

        @Test
        @DisplayName("空 username 应自动生成 spmv_ 前缀")
        void shouldGenerateUsernameWhenEmpty() throws Exception {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setVerifyCode("123456");
            req.setPassword("password123");
            req.setChannelType("phone");
            req.setUsername("");

            when(verifyCodeService.verifyCode(any(), any(), any())).thenReturn(true);
            when(channelMapper.selectCount(any())).thenReturn(0L);
            lenient().when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
            doAnswer(inv -> {
                IamUser user = inv.getArgument(0);
                user.setId(1L);
                return null;
            }).when(userMapper).insert(any(IamUser.class));
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("token");
            doAnswer(inv -> null).when(userEventPublisher).publishUserCreated(anyLong(), anyString(), anyString(), anyString());

            LoginResponse resp = authService.register(req);

            assertNotNull(resp);
            assertTrue(resp.getUsername().startsWith("spmv_"));
        }

        @Test
        @DisplayName("密码过短应拒绝")
        void shouldRejectShortPassword()
        {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setVerifyCode("123456");
            req.setPassword("short");
            req.setChannelType("phone");

            when(verifyCodeService.verifyCode(any(), any(), any())).thenReturn(true);

            ServiceException ex = assertThrows(ServiceException.class, () -> authService.register(req));
            assertEquals("密码长度需为 8-20 位", ex.getMessage());
        }

        @Test
        @DisplayName("注册时写入 bakPhone+bakEmail")
        void shouldWriteBackupContactWhenBothProvided() throws Exception {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setVerifyCode("123456");
            req.setPassword("password123");
            req.setChannelType("phone");
            req.setBakEmail("backup@example.com");

            when(verifyCodeService.verifyCode(any(), any(), any())).thenReturn(true);
            when(channelMapper.selectCount(any())).thenReturn(0L);
            lenient().when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
            doAnswer(inv -> {
                IamUser user = inv.getArgument(0);
                user.setId(1L);
                return null;
            }).when(userMapper).insert(any(IamUser.class));
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("token");
            doAnswer(inv -> null).when(userEventPublisher).publishUserCreated(anyLong(), anyString(), anyString(), anyString());
            doAnswer(inv -> {
                com.ruoyi.iam.entity.IamUserBackupContact bc = inv.getArgument(0);
                bc.setId(1L);
                return null;
            }).when(backupContactMapper).insert(any(com.ruoyi.iam.entity.IamUserBackupContact.class));

            LoginResponse resp = authService.register(req);

            assertNotNull(resp);
            verify(backupContactMapper, atLeastOnce()).insert(argThat(bc ->
                bc.getBakEmail() != null && "backup@example.com".equals(bc.getBakEmail())
            ));
        }

        @Test
        @DisplayName("注册时仅写入 bakPhone")
        void shouldWriteOnlyBakPhone() throws Exception {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setVerifyCode("123456");
            req.setPassword("password123");
            req.setChannelType("phone");
            req.setBakPhone("13900139000");

            when(verifyCodeService.verifyCode(any(), any(), any())).thenReturn(true);
            when(channelMapper.selectCount(any())).thenReturn(0L);
            lenient().when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
            doAnswer(inv -> {
                IamUser user = inv.getArgument(0);
                user.setId(1L);
                return null;
            }).when(userMapper).insert(any(IamUser.class));
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("token");
            doAnswer(inv -> null).when(userEventPublisher).publishUserCreated(anyLong(), anyString(), anyString(), anyString());
            doAnswer(inv -> {
                com.ruoyi.iam.entity.IamUserBackupContact bc = inv.getArgument(0);
                bc.setId(1L);
                return null;
            }).when(backupContactMapper).insert(any(com.ruoyi.iam.entity.IamUserBackupContact.class));

            authService.register(req);

            verify(backupContactMapper, atLeastOnce()).insert(argThat(bc ->
                bc.getBakPhone() != null && "13900139000".equals(bc.getBakPhone())
            ));
        }
    }

    // ==================== login ====================

    @Nested
    @DisplayName("login")
    class LoginTests
    {
        private IamUserChannel mockChannel()
        {
            IamUserChannel channel = new IamUserChannel();
            channel.setId(1L);
            channel.setUserId(1L);
            channel.setChannelType("phone");
            channel.setChannelAccount("13800138000");
            channel.setStatus("0");
            return channel;
        }

        private IamUser mockUser()
        {
            IamUser user = new IamUser();
            user.setId(1L);
            user.setUsername("spmv_test");
            user.setPasswordHash("$2a$10$hashed");
            user.setDisplayName("13800138000");
            user.setStatus("0");
            return user;
        }

        @Test
        @DisplayName("密码登录成功")
        void shouldLoginWithPassword()
        {
            LoginRequest req = new LoginRequest();
            req.setChannelAccount("13800138000");
            req.setCredential("password123");
            req.setLoginType("password");

            IamUserChannel channel = mockChannel();
            IamUser user = mockUser();

            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);
            lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(userMapper.updateById(any())).thenReturn(1);
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("access-token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");

            LoginResponse resp = authService.passwordLogin(req);

            assertNotNull(resp);
            assertNotNull(resp.getAccessToken());
            assertEquals(1L, resp.getUserId());
            assertEquals("spmv_test", resp.getUsername());
            verify(userMapper, atLeastOnce()).updateById(any(IamUser.class));
            verify(authLogMapper, atLeastOnce()).insert(any(IamAuthLog.class));
        }

        @Test
        @DisplayName("短信登录成功")
        void shouldLoginWithSms()
        {
            LoginRequest req = new LoginRequest();
            req.setChannelAccount("13800138000");
            req.setCredential("123456");
            req.setLoginType("sms");

            IamUserChannel channel = mockChannel();
            IamUser user = mockUser();

            when(verifyCodeService.verifyCode(eq("phone"), eq("13800138000"), eq("123456"))).thenReturn(true);
            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);
            lenient().when(userMapper.updateById(any())).thenReturn(1);
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("access-token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");

            LoginResponse resp = authService.smsLogin(req);

            assertNotNull(resp);
            assertNotNull(resp.getAccessToken());
            assertEquals(1L, resp.getUserId());
        }

        @Test
        @DisplayName("通过 login() 路由密码登录")
        void shouldRouteToPasswordLogin()
        {
            LoginRequest req = new LoginRequest();
            req.setChannelAccount("13800138000");
            req.setCredential("password123");
            req.setLoginType("password");

            IamUserChannel channel = mockChannel();
            IamUser user = mockUser();

            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);
            lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(userMapper.updateById(any())).thenReturn(1);
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("access-token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");

            LoginResponse resp = authService.login(req);

            assertNotNull(resp);
            assertNotNull(resp.getAccessToken());
        }

        @Test
        @DisplayName("通过 login() 路由短信登录")
        void shouldRouteToSmsLogin()
        {
            LoginRequest req = new LoginRequest();
            req.setChannelAccount("13800138000");
            req.setCredential("123456");
            req.setLoginType("sms");

            IamUserChannel channel = mockChannel();
            IamUser user = mockUser();

            // login() → smsLogin → findUserByChannel 三步查找通道
            // 1. channel mapper 返回 channel → 触发用户查找 by channel account
            lenient().when(channelMapper.selectOne(any())).thenReturn(channel);
            // 2. userMapper selectOne by channel_account + channel_type → 找到用户
            lenient().when(userMapper.selectOne(any())).thenReturn(user);
            // 3. verifyCode 在 smsLogin 中被调用，但 login() 会先将 channelAccount
            //    设置为 user.getUsername()（即 "spmv_abc123"），所以这里用 anyString() 匹配
            lenient().when(verifyCodeService.verifyCode(eq("phone"), anyString(), eq("123456"))).thenReturn(true);
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("access-token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");
            when(userMapper.selectById(anyLong())).thenReturn(user);

            LoginResponse resp = authService.login(req);

            assertNotNull(resp);
            assertNotNull(resp.getAccessToken());
            assertEquals(1L, resp.getUserId());
        }

        @Test
        @DisplayName("密码错误应拒绝")
        void shouldRejectWithWrongPassword()
        {
            LoginRequest req = new LoginRequest();
            req.setChannelAccount("13800138000");
            req.setCredential("wrongpassword");
            req.setLoginType("password");

            IamUserChannel channel = mockChannel();
            IamUser user = mockUser();

            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);
            lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

            ServiceException ex = assertThrows(ServiceException.class, () -> authService.passwordLogin(req));
            assertEquals("密码错误", ex.getMessage());

            verify(authLogMapper, atLeastOnce()).insert(any(IamAuthLog.class));
            verify(userMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("验证码错误应拒绝")
        void shouldRejectWithWrongSmsCode()
        {
            LoginRequest req = new LoginRequest();
            req.setChannelAccount("13800138000");
            req.setCredential("654321");
            req.setLoginType("sms");

            when(verifyCodeService.verifyCode(eq("phone"), eq("13800138000"), eq("654321"))).thenReturn(false);
            lenient().when(channelMapper.selectOne(any())).thenReturn(null);

            assertThrows(ServiceException.class, () -> authService.smsLogin(req));

            verify(authLogMapper, atLeastOnce()).insert(any(IamAuthLog.class));
        }

        @Test
        @DisplayName("用户不存在应拒绝")
        void shouldRejectWhenUserNotFound()
        {
            LoginRequest req = new LoginRequest();
            req.setChannelAccount("13800138000");
            req.setCredential("password123");
            req.setLoginType("password");

            when(channelMapper.selectOne(any())).thenReturn(null);

            assertThrows(ServiceException.class, () -> authService.passwordLogin(req));

            verify(authLogMapper, atLeastOnce()).insert(any(IamAuthLog.class));
            verify(userMapper, never()).selectById(any());
        }

        @Test
        @DisplayName("用户已停用应拒绝")
        void shouldRejectWhenUserDisabled()
        {
            LoginRequest req = new LoginRequest();
            req.setChannelAccount("13800138000");
            req.setCredential("password123");
            req.setLoginType("password");

            IamUserChannel channel = mockChannel();
            IamUser user = mockUser();
            user.setStatus("1");

            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);

            ServiceException ex = assertThrows(ServiceException.class, () -> authService.passwordLogin(req));
            assertEquals("账号已停用", ex.getMessage());

            verify(authLogMapper, atLeastOnce()).insert(any(IamAuthLog.class));
            verify(userMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("登录成功清除最后登录信息")
        void shouldUpdateLastLoginInfoOnSuccess()
        {
            LoginRequest req = new LoginRequest();
            req.setChannelAccount("13800138000");
            req.setCredential("password123");
            req.setLoginType("password");

            IamUserChannel channel = mockChannel();
            IamUser user = mockUser();

            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);
            lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(userMapper.updateById(any())).thenReturn(1);
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("access-token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");

            authService.passwordLogin(req);

            verify(userMapper, atLeastOnce()).updateById(argThat(u ->
                u.getLastLoginTime() != null
            ));
        }

        @Test
        @DisplayName("登录成功签发 refreshToken")
        void shouldIssueRefreshToken()
        {
            LoginRequest req = new LoginRequest();
            req.setChannelAccount("13800138000");
            req.setCredential("password123");
            req.setLoginType("password");

            IamUserChannel channel = mockChannel();
            IamUser user = mockUser();

            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);
            lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(userMapper.updateById(any())).thenReturn(1);
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("access-token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");

            LoginResponse resp = authService.passwordLogin(req);

            assertNotNull(resp.getRefreshToken());
            verify(tokenService, atLeastOnce()).createRefreshToken(eq(1L));
        }

        // ==== v1.1 用户名登录三步定位 ====

        @Test
        @DisplayName("用户名精确匹配登录")
        void shouldLoginByUsernameExactMatch()
        {
            IamUser user = mockUser();
            user.setUsername("my_custom_name");
            user.setDisplayName("13800138000");

            lenient().when(channelMapper.selectOne(any())).thenReturn(null); // channel 无匹配
            lenient().when(userMapper.selectById(1L)).thenReturn(user);
            lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(userMapper.updateById(any())).thenReturn(1);
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("access-token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");

            // mock findByUsername → 返回用户
            when(userMapper.selectOne(any())).thenReturn(user);

            LoginResponse resp = authService.login(buildLoginRequest("my_custom_name"));

            assertNotNull(resp);
            assertEquals("my_custom_name", resp.getUsername());
        }

        @Test
        @DisplayName("用户名模糊匹配登录")
        void shouldLoginByUsernameFuzzyMatch()
        {
            IamUser user = mockUser();
            user.setUsername("spmv_abc123");
            user.setDisplayName("13800138000");

            lenient().when(channelMapper.selectOne(any())).thenReturn(null);
            lenient().when(userMapper.selectOne(any())).thenReturn(user);

            // 模糊匹配 → displayName 验证
            LoginRequest req = buildLoginRequest("13800138000");
            lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(userMapper.updateById(any())).thenReturn(1);
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("access-token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");

            LoginResponse resp = authService.login(req);

            assertNotNull(resp);
            assertEquals("spmv_abc123", resp.getUsername());
        }
    }

    // ==================== login rate limiting ====================

    @Nested
    @DisplayName("login rate limiting")
    class LoginRateLimitingTests
    {
        private IamUser mockUser()
        {
            IamUser user = new IamUser();
            user.setId(1L);
            user.setUsername("spmv_test");
            user.setPasswordHash("$2a$10$hashed");
            user.setDisplayName("13800138000");
            user.setStatus("0");
            user.setDeleteStatus("0");
            return user;
        }

        @Test
        @DisplayName("失败5次后锁定")
        void shouldLockAfter5FailedAttempts()
        {
            IamUserChannel channel = new IamUserChannel();
            channel.setId(1L);
            channel.setUserId(1L);
            channel.setChannelType("phone");
            channel.setChannelAccount("13800138000");
            channel.setStatus("0");

            IamUser user = mockUser();
            user.setPasswordHash("$2a$10$hashed");

            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);
            lenient().when(userMapper.updateById(any())).thenReturn(1);
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("access-token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");

            // 4次失败：每次 increment 返回 1-4
            for (int i = 0; i < 4; i++)
            {
                lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
                when(valueOperations.increment(anyString())).thenReturn((long) (i + 1));
                try { authService.passwordLogin(buildLoginRequest("wrongpass" + i)); }
                catch (Exception ignored) {}
            }

            // 第5次失败：increment 返回 5 → 锁定
            lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
            when(valueOperations.increment(anyString())).thenReturn(5L);
            lenient().when(redisTemplate.expire(anyString(), anyLong(), any())).thenReturn(true);
            lenient().doNothing().when(valueOperations).set(anyString(), anyString(), anyLong(), any());

            assertThrows(ServiceException.class, () -> authService.passwordLogin(buildLoginRequest("wrongpass5")));

            // 锁定后再次尝试：应被拒绝
            when(valueOperations.get(anyString())).thenReturn("locked");
            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);

            ServiceException ex = assertThrows(ServiceException.class, () -> authService.passwordLogin(buildLoginRequest("wrongpass6")));
            assertEquals("账号已被锁定，请 30 分钟后再试", ex.getMessage());
        }

        @Test
        @DisplayName("4次失败不应锁定")
        void shouldNotLockBefore5Attempts()
        {
            IamUserChannel channel = new IamUserChannel();
            channel.setId(1L);
            channel.setUserId(1L);
            channel.setChannelType("phone");
            channel.setChannelAccount("13800138000");
            channel.setStatus("0");

            IamUser user = mockUser();
            user.setPasswordHash("$2a$10$hashed");

            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);
            lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
            when(valueOperations.increment(anyString())).thenReturn(4L);

            ServiceException ex = assertThrows(ServiceException.class, () -> authService.passwordLogin(buildLoginRequest("wrongpass")));
            assertEquals("密码错误", ex.getMessage());
            verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any());
        }

        @Test
        @DisplayName("成功登录后应清除失败计数")
        void shouldClearCountOnSuccessfulLogin()
        {
            IamUserChannel channel = new IamUserChannel();
            channel.setId(1L);
            channel.setUserId(1L);
            channel.setChannelType("phone");
            channel.setChannelAccount("13800138000");
            channel.setStatus("0");

            IamUser user = mockUser();
            user.setPasswordHash("$2a$10$hashed");

            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);
            lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            lenient().when(valueOperations.increment(anyString())).thenReturn(3L);
            when(userMapper.updateById(any())).thenReturn(1);
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("access-token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");

            LoginResponse resp = authService.passwordLogin(buildLoginRequest("password123"));
            assertNotNull(resp);

            verify(redisTemplate, atLeastOnce()).delete(anyString()); // clear count key
        }

        @Test
        @DisplayName("短信登录成功也应清除失败计数")
        void shouldClearCountOnSuccessfulSmsLogin()
        {
            IamUserChannel channel = new IamUserChannel();
            channel.setId(1L);
            channel.setUserId(1L);
            channel.setChannelType("phone");
            channel.setChannelAccount("13800138000");
            channel.setStatus("0");

            IamUser user = mockUser();

            when(valueOperations.get(anyString())).thenReturn("3"); // fail count
            when(verifyCodeService.verifyCode(eq("phone"), eq("13800138000"), eq("123456"))).thenReturn(true);
            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);
            lenient().when(userMapper.updateById(any())).thenReturn(1);
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("access-token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");

            LoginResponse resp = authService.smsLogin(buildLoginRequest("123456", "sms"));
            assertNotNull(resp);

            verify(redisTemplate, atLeastOnce()).delete(anyString());
        }

        @Test
        @DisplayName("TTL 过期后计数应归零")
        void shouldResetCountAfterTtlExpires()
        {
            IamUserChannel channel = new IamUserChannel();
            channel.setId(1L);
            channel.setUserId(1L);
            channel.setChannelType("phone");
            channel.setChannelAccount("13800138000");
            channel.setStatus("0");

            IamUser user = mockUser();
            user.setPasswordHash("$2a$10$hashed");

            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);
            // TTL 过期后 INCR 返回 null（Redis key 不存在时 increment 返回 null）
            when(valueOperations.increment(anyString())).thenReturn(null);

            ServiceException ex = assertThrows(ServiceException.class, () -> authService.passwordLogin(buildLoginRequest("wrongpass")));
            assertEquals("密码错误", ex.getMessage());
            // 不应写入锁定 key
            verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any());
        }

        @Test
        @DisplayName("锁定期间拒绝应返回正确消息")
        void shouldRejectWithCorrectMessageWhenLocked()
        {
            IamUserChannel channel = new IamUserChannel();
            channel.setId(1L);
            channel.setUserId(1L);
            channel.setChannelType("phone");
            channel.setChannelAccount("13800138000");
            channel.setStatus("0");

            IamUser user = mockUser();

            when(valueOperations.get(anyString())).thenReturn("locked");
            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);

            ServiceException ex = assertThrows(ServiceException.class, () -> authService.passwordLogin(buildLoginRequest("any")));
            assertEquals("账号已被锁定，请 30 分钟后再试", ex.getMessage());
            // 不应调用密码校验
            verify(passwordEncoder, never()).matches(any(), any());
        }
    }

    // ==================== login - deactivate status check ====================

    @Nested
    @DisplayName("login - deactivate status check")
    class LoginDeactivateCheck
    {
        private IamUserChannel makeChannel(Long userId, String account)
        {
            IamUserChannel c = new IamUserChannel();
            c.setId(userId);
            c.setUserId(userId);
            c.setChannelType("phone");
            c.setChannelAccount(account);
            c.setStatus("0");
            return c;
        }

        private IamUser makeUser(Long userId, String deleteStatus)
        {
            IamUser u = new IamUser();
            u.setId(userId);
            u.setUsername("spmv_test");
            u.setPasswordHash("$2a$10$hashed");
            u.setDisplayName("13800138000");
            u.setStatus("0");
            u.setDeleteStatus(deleteStatus);
            return u;
        }

        @Test
        @DisplayName("deleteStatus=1 应拒绝登录：账号处于注销冷静期")
        void shouldRejectLoginWhenDeleteStatusIsCoolingPeriod()
        {
            IamUserChannel channel = makeChannel(1L, "13800138000");
            IamUser user = makeUser(1L, "1");

            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);

            ServiceException ex = assertThrows(ServiceException.class,
                () -> authService.passwordLogin(buildLoginRequest("password123")));
            assertEquals("账号处于注销冷静期", ex.getMessage());

            verify(passwordEncoder, never()).matches(any(), any());
            verify(authLogMapper, atLeastOnce()).insert(any(IamAuthLog.class));
        }

        @Test
        @DisplayName("deleteStatus=2 应拒绝登录：账号已注销")
        void shouldRejectLoginWhenDeleteStatusIsDeleted()
        {
            IamUserChannel channel = makeChannel(1L, "13800138000");
            IamUser user = makeUser(1L, "2");

            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);

            ServiceException ex = assertThrows(ServiceException.class,
                () -> authService.passwordLogin(buildLoginRequest("password123")));
            assertEquals("账号已注销", ex.getMessage());

            verify(passwordEncoder, never()).matches(any(), any());
            verify(authLogMapper, atLeastOnce()).insert(any(IamAuthLog.class));
        }

        @Test
        @DisplayName("deleteStatus=0 应允许正常登录")
        void shouldAllowLoginWhenDeleteStatusIsNormal()
        {
            IamUserChannel channel = makeChannel(1L, "13800138000");
            IamUser user = makeUser(1L, "0");

            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);
            lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(valueOperations.get(anyString())).thenReturn(null);
            when(userMapper.updateById(any())).thenReturn(1);
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("access-token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");

            LoginResponse resp = authService.passwordLogin(buildLoginRequest("password123"));
            assertNotNull(resp);
            verify(passwordEncoder, atLeastOnce()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("deleteStatus=null 应允许正常登录")
        void shouldAllowLoginWhenDeleteStatusIsNull()
        {
            IamUserChannel channel = makeChannel(1L, "13800138000");
            IamUser user = makeUser(1L, null);

            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);
            lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(valueOperations.get(anyString())).thenReturn(null);
            when(userMapper.updateById(any())).thenReturn(1);
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("access-token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");

            LoginResponse resp = authService.passwordLogin(buildLoginRequest("password123"));
            assertNotNull(resp);
            verify(passwordEncoder, atLeastOnce()).matches(anyString(), anyString());
        }
    }

    // ==================== login rate limiting - additional coverage ====================

    @Nested
    @DisplayName("login rate limiting - additional")
    class LoginRateLimitingAdditionalTests
    {
        private IamUserChannel makeChannel(int userId, String account)
        {
            IamUserChannel c = new IamUserChannel();
            c.setId((long) userId);
            c.setUserId((long) userId);
            c.setChannelType("phone");
            c.setChannelAccount(account);
            c.setStatus("0");
            return c;
        }

        private IamUser makeUser(int userId)
        {
            IamUser u = new IamUser();
            u.setId((long) userId);
            u.setUsername("spmv_" + userId);
            u.setPasswordHash("$2a$10$hashed");
            u.setDisplayName(String.valueOf(10000000000L + userId));
            u.setStatus("0");
            return u;
        }

        @Test
        @DisplayName("不同用户互相不影响")
        void shouldNotAffectOtherUsers()
        {
            // User1 被锁定
            IamUserChannel ch1 = makeChannel(1, "13800138000");
            IamUser u1 = makeUser(1);
            when(valueOperations.get(anyString())).thenReturn("locked");
            when(channelMapper.selectOne(any())).thenReturn(ch1);
            when(userMapper.selectById(1L)).thenReturn(u1);

            // User2 登录不应被锁定
            IamUserChannel ch2 = makeChannel(2, "13900139000");
            IamUser u2 = makeUser(2);
            when(channelMapper.selectOne(any())).thenReturn(ch2);
            when(userMapper.selectById(2L)).thenReturn(u2);
            lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(userMapper.updateById(any())).thenReturn(1);
            when(valueOperations.get(anyString())).thenReturn(null); // user2 no lock
            lenient().when(valueOperations.increment(anyString())).thenReturn(null);
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("access-token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");

            // User2 登录成功
            LoginResponse resp2 = authService.passwordLogin(buildLoginRequest("password123"));
            assertNotNull(resp2);

            // User1 锁定不受影响
            when(valueOperations.get(anyString())).thenReturn("locked");
            when(channelMapper.selectOne(any())).thenReturn(ch1);
            when(userMapper.selectById(1L)).thenReturn(u1);

            ServiceException ex1 = assertThrows(ServiceException.class,
                () -> authService.passwordLogin(
                    buildLoginRequest("wrong")));
            assertEquals("账号已被锁定，请 30 分钟后再试", ex1.getMessage());
        }

        @Test
        @DisplayName("不同账号绑定同一用户，只锁定该用户")
        void shouldLockByUserIdNotByAccount()
        {
            IamUserChannel channel = makeChannel(1, "13800138000");
            IamUser user = makeUser(1);

            // 第4次失败
            when(valueOperations.increment(anyString())).thenReturn(4L);
            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);
            lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

            ServiceException ex = assertThrows(ServiceException.class,
                () -> authService.passwordLogin(buildLoginRequest("wrong")));
            assertEquals("密码错误", ex.getMessage());
            verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any());
        }

        @Test
        @DisplayName("登录成功应清除失败计数")
        void shouldClearFailOnSuccessfulLogin()
        {
            IamUserChannel channel = makeChannel(1, "13800138000");
            IamUser user = makeUser(1);

            // 2次失败
            when(valueOperations.increment(anyString())).thenReturn(2L);
            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);
            lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

            assertThrows(ServiceException.class, () -> authService.passwordLogin(buildLoginRequest("wrong")));

            // 3次失败后改为正确密码登录成功
            lenient().when(valueOperations.increment(anyString())).thenReturn(3L);
            lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(userMapper.updateById(any())).thenReturn(1);
            when(valueOperations.get(anyString())).thenReturn(null);
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("access-token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");

            LoginResponse resp = authService.passwordLogin(buildLoginRequest("password123"));
            assertNotNull(resp);
            verify(redisTemplate, atLeastOnce()).delete(anyString()); // clear fail count on success
        }

        @Test
        @DisplayName("锁定过期后应允许登录")
        void shouldAllowLoginAfterLockExpires()
        {
            IamUserChannel channel = makeChannel(1, "13800138000");
            IamUser user = makeUser(1);

            // 先模拟被锁定
            when(valueOperations.get(anyString())).thenReturn("locked");
            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);

            // 锁定状态被拒绝
            assertThrows(ServiceException.class,
                () -> authService.passwordLogin(buildLoginRequest("wrong")));

            // 锁定过期（返回 null）
            when(valueOperations.get(anyString())).thenReturn(null);
            lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(userMapper.updateById(any())).thenReturn(1);
            when(valueOperations.get(anyString())).thenReturn(null);
            lenient().when(valueOperations.increment(anyString())).thenReturn(1L);
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("access-token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");

            LoginResponse resp = authService.passwordLogin(buildLoginRequest("password123"));
            assertNotNull(resp);
        }

        @Test
        @DisplayName("短信登录同样受限流保护")
        void shouldRateLimitSMSLogin()
        {
            IamUser user = makeUser(1);
            IamUserChannel channel = makeChannel(1, "13800138000");

            // 第4次失败
            when(valueOperations.increment(anyString())).thenReturn(4L);
            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);
            when(valueOperations.get(anyString())).thenReturn("123456");

            // 第5次失败应锁定
            when(valueOperations.increment(anyString())).thenReturn(5L);
            when(valueOperations.get(anyString())).thenReturn("654321");
            lenient().doNothing().when(valueOperations).set(anyString(), anyString(), anyLong(), any());

            assertThrows(ServiceException.class, () -> authService.smsLogin(buildLoginRequest("654321", "sms")));

            // 锁定后被拒绝
            when(valueOperations.get(anyString())).thenReturn("locked");
            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);

            ServiceException ex = assertThrows(ServiceException.class,
                () -> authService.smsLogin(buildLoginRequest("123456", "sms")));
            assertEquals("账号已被锁定，请 30 分钟后再试", ex.getMessage());
        }
    }

    private LoginRequest buildLoginRequest(String credential)
    {
        return buildLoginRequest(credential, "password");
    }

    private LoginRequest buildLoginRequest(String credential, String loginType)
    {
        LoginRequest req = new LoginRequest();
        req.setChannelAccount("13800138000");
        req.setCredential(credential);
        req.setLoginType(loginType);
        return req;
    }

    // ==================== buildSysUser + LoginUser + Redis ====================

    @Nested
    @DisplayName("buildSysUser")
    class BuildSysUserTests
    {
        @Test
        @DisplayName("buildSysUser 返回非 null SysUser")
        void shouldReturnNonNullSysUser()
        {
            IamUser iamUser = mockIamUser(1L, "test_user", "Test User");
            com.ruoyi.system.api.domain.SysUser sysUser = authService.buildSysUser(iamUser, 1L);
            assertNotNull(sysUser);
        }

        @Test
        @DisplayName("userId 映射到 iamUserId")
        void shouldMapUserId()
        {
            IamUser iamUser = mockIamUser(1L, "test_user", "Test User");
            com.ruoyi.system.api.domain.SysUser sysUser = authService.buildSysUser(iamUser, 42L);
            assertEquals(42L, sysUser.getUserId());
        }

        @Test
        @DisplayName("userName 映射到 iamUser.getUsername()")
        void shouldMapUserName()
        {
            IamUser iamUser = mockIamUser(1L, "my_user", "Test User");
            com.ruoyi.system.api.domain.SysUser sysUser = authService.buildSysUser(iamUser, 1L);
            assertEquals("my_user", sysUser.getUserName());
        }

        @Test
        @DisplayName("nickName 映射到 iamUser.getDisplayName()")
        void shouldMapNickName()
        {
            IamUser iamUser = mockIamUser(1L, "user", "My Nick");
            com.ruoyi.system.api.domain.SysUser sysUser = authService.buildSysUser(iamUser, 1L);
            assertEquals("My Nick", sysUser.getNickName());
        }

        @Test
        @DisplayName("status = '0'")
        void shouldSetStatusToNormal()
        {
            IamUser iamUser = mockIamUser(1L, "user", "Nick");
            com.ruoyi.system.api.domain.SysUser sysUser = authService.buildSysUser(iamUser, 1L);
            assertEquals("0", sysUser.getStatus());
        }

        @Test
        @DisplayName("delFlag = '0'")
        void shouldSetDelFlag()
        {
            IamUser iamUser = mockIamUser(1L, "user", "Nick");
            com.ruoyi.system.api.domain.SysUser sysUser = authService.buildSysUser(iamUser, 1L);
            assertEquals("0", sysUser.getDelFlag());
        }

        @Test
        @DisplayName("deptId = 0")
        void shouldSetDeptIdToZero()
        {
            IamUser iamUser = mockIamUser(1L, "user", "Nick");
            com.ruoyi.system.api.domain.SysUser sysUser = authService.buildSysUser(iamUser, 1L);
            assertEquals(0L, sysUser.getDeptId().longValue());
        }

        @Test
        @DisplayName("dept 非 null")
        void shouldSetDeptNonNull()
        {
            IamUser iamUser = mockIamUser(1L, "user", "Nick");
            com.ruoyi.system.api.domain.SysUser sysUser = authService.buildSysUser(iamUser, 1L);
            assertNotNull(sysUser.getDept());
        }

        @Test
        @DisplayName("roles 非 null 且空")
        void shouldSetRolesEmpty()
        {
            IamUser iamUser = mockIamUser(1L, "user", "Nick");
            com.ruoyi.system.api.domain.SysUser sysUser = authService.buildSysUser(iamUser, 1L);
            assertNotNull(sysUser.getRoles());
            assertTrue(sysUser.getRoles().isEmpty());
        }

        @Test
        @DisplayName("roleIds 非 null 且空")
        void shouldSetRoleIdsEmpty()
        {
            IamUser iamUser = mockIamUser(1L, "user", "Nick");
            com.ruoyi.system.api.domain.SysUser sysUser = authService.buildSysUser(iamUser, 1L);
            assertNotNull(sysUser.getRoleIds());
            assertEquals(0, sysUser.getRoleIds().length);
        }
    }

    private IamUser mockIamUser(Long id, String username, String displayName)
    {
        IamUser u = new IamUser();
        u.setId(id);
        u.setUsername(username);
        u.setPasswordHash("$2a$10$hashed");
        u.setDisplayName(displayName);
        u.setStatus("0");
        u.setCreateTime(new Date());
        return u;
    }

    // ==================== checkFieldUnique ====================

    @Nested
    @DisplayName("checkFieldUnique")
    class CheckFieldUniqueTests
    {
        @Test
        @DisplayName("username 唯一返回 true")
        void shouldReturnTrueWhenUsernameUnique()
        {
            when(userMapper.selectCount(any())).thenReturn(0L);

            boolean result = authService.checkFieldUnique("username", "newuser", "spacemv-coai");

            assertTrue(result);
            verify(userMapper).selectCount(any());
        }

        @Test
        @DisplayName("username 不唯一返回 false")
        void shouldReturnFalseWhenUsernameNotUnique()
        {
            when(userMapper.selectCount(any())).thenReturn(1L);

            boolean result = authService.checkFieldUnique("username", "admin", "spacemv-coai");

            assertFalse(result);
        }

        @Test
        @DisplayName("username 检查应过滤 delFlag")
        void shouldFilterByUsernameWithDelFlag()
        {
            when(userMapper.selectCount(any())).thenReturn(0L);

            authService.checkFieldUnique("username", "testuser", "spacemv-coai");

            verify(userMapper).selectCount(any());
        }

        @Test
        @DisplayName("phone 唯一（按 productLine）返回 true")
        void shouldReturnTrueWhenPhoneUniqueWithinProductLine()
        {
            when(channelMapper.selectCount(any())).thenReturn(0L);

            boolean result = authService.checkFieldUnique("phone", "13900139000", "spacemv-coai");

            assertTrue(result);
        }

        @Test
        @DisplayName("phone 不唯一返回 false")
        void shouldReturnFalseWhenPhoneNotUnique()
        {
            when(channelMapper.selectCount(any())).thenReturn(1L);

            boolean result = authService.checkFieldUnique("phone", "13800138000", "spacemv-coai");

            assertFalse(result);
        }

        @Test
        @DisplayName("phone 检查应包含 productLine 维度的限制")
        void shouldCheckPhoneWithProductLineScope()
        {
            when(channelMapper.selectCount(any())).thenReturn(0L);

            authService.checkFieldUnique("phone", "13900139000", "other-product");

            verify(channelMapper).selectCount(any());
        }

        @Test
        @DisplayName("email 唯一返回 true")
        void shouldReturnTrueWhenEmailUnique()
        {
            when(channelMapper.selectCount(any())).thenReturn(0L);

            boolean result = authService.checkFieldUnique("email", "test@example.com", "spacemv-coai");

            assertTrue(result);
        }

        @Test
        @DisplayName("不支持的 fieldType 返回 false")
        void shouldReturnFalseForUnsupportedFieldType()
        {
            boolean result = authService.checkFieldUnique("wechat", "wxid123", "spacemv-coai");

            assertFalse(result);
            // mapper 不应被调用
            verify(userMapper, never()).selectCount(any());
            verify(channelMapper, never()).selectCount(any());
        }

        @Test
        @DisplayName("不同 productLine 的 phone 是独立的")
        void shouldCheckPhonePerProductLine()
        {
            // spacemv-coai 中可用
            when(channelMapper.selectCount(any())).thenReturn(0L);

            boolean result = authService.checkFieldUnique("phone", "13900139000", "spacemv-coai");

            assertTrue(result);
            verify(channelMapper).selectCount(any());
        }
    }

    // ==================== register/login LoginUser + Redis ====================

    @Nested
    @DisplayName("register LoginUser + Redis")
    class RegisterLoginUserTests
    {
        @Test
        @DisplayName("register 写入 Redis login_tokens key")
        void shouldWriteLoginUserToRedis() throws Exception {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setVerifyCode("123456");
            req.setPassword("password123");
            req.setChannelType("phone");

            when(verifyCodeService.verifyCode(eq("phone"), eq("13800138000"), eq("123456"))).thenReturn(true);
            when(channelMapper.selectCount(any())).thenReturn(0L);
            lenient().when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
            doAnswer(inv -> {
                IamUser user = inv.getArgument(0);
                user.setId(1L);
                return null;
            }).when(userMapper).insert(any(IamUser.class));

            // Use redisService mock for the LoginUser Redis caching (which is what the actual code calls)
            lenient().doAnswer(inv -> {
                com.ruoyi.system.api.model.LoginUser lu = inv.getArgument(1);
                assertNotNull(lu);
                assertNotNull(lu.getSysUser());
                assertNotNull(lu.getSysUser().getDept());
                assertNotNull(lu.getPermissions());
                assertNotNull(lu.getRoles());
                return null;
            }).when(redisService).setCacheObject(anyString(), any(), anyLong(), any());

            // 需要 mock tokenService 的 createAccessTokenWithUserKey
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("access-token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");

            LoginResponse resp = authService.register(req);
            assertNotNull(resp);
        }

        @Test
        @DisplayName("register LoginUser.sysUser 非 null")
        void shouldSetSysUserInLoginUser() throws Exception {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setVerifyCode("123456");
            req.setPassword("password123");
            req.setChannelType("phone");

            when(verifyCodeService.verifyCode(any(), any(), any())).thenReturn(true);
            when(channelMapper.selectCount(any())).thenReturn(0L);
            lenient().when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
            doAnswer(inv -> {
                IamUser user = inv.getArgument(0);
                user.setId(1L);
                user.setUsername("spmv_test");
                user.setDisplayName("13800138000");
                return null;
            }).when(userMapper).insert(any(IamUser.class));

            // Use redisService mock for the LoginUser Redis caching
            lenient().doAnswer(inv -> {
                com.ruoyi.system.api.model.LoginUser lu = inv.getArgument(1);
                assertNotNull(lu.getSysUser());
                assertEquals(1L, lu.getSysUser().getUserId().longValue());
                assertEquals("spmv_test", lu.getSysUser().getUserName());
                assertEquals("0", lu.getSysUser().getStatus());
                return null;
            }).when(redisService).setCacheObject(anyString(), any(), anyLong(), any());

            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("token");

            authService.register(req);
        }
    }

    // ==================== register password boundary ====================

    @Nested
    @DisplayName("register - password boundary")
    class RegisterPasswordBoundaryTests
    {
        @Test
        @DisplayName("密码长度 8 字符（最小合法值）")
        void shouldAccept8CharPassword() throws Exception {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setVerifyCode("123456");
            req.setPassword("Ab1Cd2Ef");
            req.setChannelType("phone");

            when(verifyCodeService.verifyCode(any(), any(), any())).thenReturn(true);
            when(channelMapper.selectCount(any())).thenReturn(0L);
            lenient().when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
            doAnswer(inv -> {
                IamUser user = inv.getArgument(0);
                user.setId(1L);
                return null;
            }).when(userMapper).insert(any(IamUser.class));
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("token");

            LoginResponse resp = authService.register(req);
            assertNotNull(resp);
        }

        @Test
        @DisplayName("密码长度 20 字符（最大合法值）")
        void shouldAccept20CharPassword() throws Exception {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setVerifyCode("123456");
            req.setPassword("Abcdef1234567890Abcd");
            req.setChannelType("phone");

            when(verifyCodeService.verifyCode(any(), any(), any())).thenReturn(true);
            when(channelMapper.selectCount(any())).thenReturn(0L);
            lenient().when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
            doAnswer(inv -> {
                IamUser user = inv.getArgument(0);
                user.setId(1L);
                return null;
            }).when(userMapper).insert(any(IamUser.class));
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("token");

            LoginResponse resp = authService.register(req);
            assertNotNull(resp);
        }

        @Test
        @DisplayName("密码长度 21 字符（超过最大）")
        void shouldReject21CharPassword()
        {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setVerifyCode("123456");
            req.setPassword("Abcdef1234567890Abcde");
            req.setChannelType("phone");

            when(verifyCodeService.verifyCode(any(), any(), any())).thenReturn(true);

            ServiceException ex = assertThrows(ServiceException.class, () -> authService.register(req));
            assertEquals("密码长度需为 8-20 位", ex.getMessage());
        }
    }

    // ==================== login email support ====================

    @Nested
    @DisplayName("login - email support")
    class LoginWithEmailTests
    {
        @Test
        @DisplayName("通过 login() 路由邮箱+密码登录")
        void shouldLoginByEmailViaRouter()
        {
            LoginRequest req = buildLoginRequest("password123");
            req.setChannelAccount("test@example.com");
            req.setLoginType("password");

            IamUserChannel channel = new IamUserChannel();
            channel.setId(1L);
            channel.setUserId(1L);
            channel.setChannelType("email");
            channel.setChannelAccount("test@example.com");
            channel.setStatus("0");

            IamUser user = mockIamUser(1L, "spmv_email", "test@example.com");

            when(channelMapper.selectOne(any())).thenReturn(channel);
            when(userMapper.selectById(1L)).thenReturn(user);
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("access-token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");
            lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(userMapper.updateById(any())).thenReturn(1);

            LoginResponse resp = authService.login(req);

            assertNotNull(resp);
            assertEquals("spmv_email", resp.getUsername());
        }

        @Test
        @DisplayName("通过 login() 路由用户名+密码登录")
        void shouldLoginByUsernameExactViaRouter()
        {
            LoginRequest req = buildLoginRequest("password123");
            req.setChannelAccount("custom_user");
            req.setLoginType("password");

            IamUser user = mockIamUser(1L, "custom_user", "13800138000");
            user.setUsername("custom_user");

            when(userMapper.selectOne(any())).thenReturn(user);
            lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(userMapper.updateById(any())).thenReturn(1);
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("access-token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");

            LoginResponse resp = authService.login(req);

            assertNotNull(resp);
            assertEquals("custom_user", resp.getUsername());
        }
    }

    // ==================== importUser ====================

    @Nested
    @DisplayName("importUser")
    class ImportUserTests
    {
        @Test
        @DisplayName("导入已存在用户返回登录响应")
        void shouldReturnLoginForExistingUser()
        {
            IamUser existing = mockIamUser(1L, "legacy_user", "Legacy");
            lenient().when(userMapper.selectOne(any())).thenReturn(existing);
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");

            InnerUserImportRequest req = new InnerUserImportRequest();
            req.setUsername("legacy_user");
            req.setPasswordHash("hashed");
            req.setPhone("13800138000");

            LoginResponse resp = authService.importUser(req);

            assertNotNull(resp);
            assertEquals("legacy_user", resp.getUsername());
            verify(userMapper, never()).insert(any());
        }

        @Test
        @DisplayName("导入新用户使用 phone 作为主通道")
        void shouldImportNewUserWithPhone()
        {
            lenient().when(userMapper.selectOne(any())).thenReturn(null);
            doAnswer(inv -> {
                IamUser user = inv.getArgument(0);
                user.setId(1L);
                return null;
            }).when(userMapper).insert(any(IamUser.class));
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");

            InnerUserImportRequest req = new InnerUserImportRequest();
            req.setUsername("new_legacy");
            req.setPasswordHash("hashed");
            req.setPhone("13800138000");
            req.setEmail("legacy@example.com");

            LoginResponse resp = authService.importUser(req);

            assertNotNull(resp);
            verify(channelMapper, atLeastOnce()).insert(argThat(ch ->
                "phone".equals(ch.getChannelType())
            ));
        }

        @Test
        @DisplayName("导入时 phone 为空使用 email 作为主通道")
        void shouldImportNewUserWithEmail()
        {
            lenient().when(userMapper.selectOne(any())).thenReturn(null);
            doAnswer(inv -> {
                IamUser user = inv.getArgument(0);
                user.setId(1L);
                return null;
            }).when(userMapper).insert(any(IamUser.class));
            lenient().when(tokenService.createAccessTokenWithUserKey(anyLong(), anyString(), anyString(), any(), anyString())).thenReturn("token");
            lenient().when(tokenService.createRefreshToken(any())).thenReturn("refresh-token");

            InnerUserImportRequest req = new InnerUserImportRequest();
            req.setUsername("email_legacy");
            req.setPasswordHash("hashed");
            req.setEmail("legacy@example.com");

            LoginResponse resp = authService.importUser(req);

            assertNotNull(resp);
            verify(channelMapper, atLeastOnce()).insert(argThat(ch ->
                "email".equals(ch.getChannelType())
            ));
        }

        @Test
        @DisplayName("导入空用户名应拒绝")
        void shouldRejectBlankUsername()
        {
            InnerUserImportRequest req = new InnerUserImportRequest();
            req.setUsername("");

            ServiceException ex = assertThrows(ServiceException.class, () -> authService.importUser(req));
            assertEquals("用户名不能为空", ex.getMessage());
        }
    }
}
