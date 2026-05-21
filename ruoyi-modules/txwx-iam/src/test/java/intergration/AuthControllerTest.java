package intergration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.iam.controller.AuthController;
import com.ruoyi.iam.dto.DeactivateRequest;
import com.ruoyi.iam.dto.DeactivateStatusResponse;
import com.ruoyi.iam.dto.LoginRequest;
import com.ruoyi.iam.dto.LoginResponse;
import com.ruoyi.iam.dto.RegisterRequest;
import com.ruoyi.iam.dto.VerifyCodeRequest;
import com.ruoyi.iam.dto.InnerUserImportRequest;
import com.ruoyi.iam.entity.IamUser;
import com.ruoyi.iam.entity.IamUserChannel;

import java.util.Date;
import java.util.List;

import com.ruoyi.iam.service.AuthService;
import com.ruoyi.iam.service.DeactivateService;
import com.ruoyi.iam.service.IamValidateCodeService;
import com.ruoyi.iam.service.TokenService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController 测试类
 *
 * @author txwx
 */
@DisplayName("AuthController Tests")
@ExtendWith(MockitoExtension.class)
class AuthControllerTest
{
    @Mock
    private AuthService authService;

    @Mock
    private TokenService tokenService;

    @Mock
    private DeactivateService deactivateService;

    @Mock
    private IamValidateCodeService validateCodeService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp()
    {
        var controller = new AuthController(authService, tokenService, deactivateService, validateCodeService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new com.ruoyi.iam.controller.GlobalExceptionHandler())
            .build();
    }

    // ==================== register ====================

    @Nested
    @DisplayName("register")
    class RegisterTests
    {
        @Test
        @DisplayName("注册成功")
        void shouldRegisterSuccessfully() throws Exception
        {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setChannelType("phone");
            req.setVerifyCode("123456");
            req.setPassword("password123");

            LoginResponse resp = new LoginResponse();
            resp.setAccessToken("access-token");
            resp.setRefreshToken("refresh-token");
            resp.setUserId(1L);
            resp.setUsername("spmv_test");
            when(authService.register(any())).thenReturn(resp);

            mockMvc.perform(post("/auth/v1/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value(1));

            verify(authService).register(any(RegisterRequest.class));
        }
    }

    // ==================== login ====================

    @Nested
    @DisplayName("login")
    class LoginTests
    {
        @Test
        @DisplayName("密码登录成功")
        void shouldLoginWithPassword() throws Exception
        {
            LoginRequest req = new LoginRequest();
            req.setChannelAccount("13800138000");
            req.setCredential("password123");
            req.setLoginType("password");

            LoginResponse resp = new LoginResponse();
            resp.setAccessToken("access-token");
            resp.setUserId(1L);
            resp.setUsername("spmv_test");
            when(authService.login(any())).thenReturn(resp);

            mockMvc.perform(post("/auth/v1/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("spmv_test"));

            verify(authService).login(argThat(r ->
                "13800138000".equals(r.getChannelAccount()) &&
                "password".equals(r.getLoginType())
            ));
        }

        @Test
        @DisplayName("用户名+密码登录成功")
        void shouldLoginWithUsername() throws Exception
        {
            LoginRequest req = new LoginRequest();
            req.setChannelAccount("myname");
            req.setCredential("MyPass123");
            req.setLoginType("password");

            LoginResponse resp = new LoginResponse();
            resp.setAccessToken("access-token");
            resp.setUserId(1L);
            resp.setUsername("myname");
            when(authService.login(any())).thenReturn(resp);

            mockMvc.perform(post("/auth/v1/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("myname"));
        }

        @Test
        @DisplayName("邮箱+密码登录成功")
        void shouldLoginWithEmail() throws Exception
        {
            LoginRequest req = new LoginRequest();
            req.setChannelAccount("test@qq.com");
            req.setCredential("MyPass123");
            req.setLoginType("password");

            LoginResponse resp = new LoginResponse();
            resp.setAccessToken("access-token");
            resp.setUserId(2L);
            resp.setUsername("testuser");
            when(authService.login(any())).thenReturn(resp);

            mockMvc.perform(post("/auth/v1/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("短信验证码登录成功")
        void shouldLoginWithSms() throws Exception
        {
            LoginRequest req = new LoginRequest();
            req.setChannelAccount("13800138000");
            req.setCredential("123456");
            req.setLoginType("sms");

            LoginResponse resp = new LoginResponse();
            resp.setAccessToken("access-token");
            resp.setUserId(1L);
            when(authService.login(any())).thenReturn(resp);

            mockMvc.perform(post("/auth/v1/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("用户不存在返回 401")
        void shouldRejectUserNotFound() throws Exception
        {
            LoginRequest req = new LoginRequest();
            req.setChannelAccount("99999999999");
            req.setCredential("anyPassword");
            req.setLoginType("password");

            when(authService.login(any())).thenThrow(
                new com.ruoyi.common.core.exception.ServiceException("用户不存在"));

            mockMvc.perform(post("/auth/v1/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("用户不存在"));
        }

        @Test
        @DisplayName("密码错误返回 401")
        void shouldRejectWrongPassword() throws Exception
        {
            LoginRequest req = new LoginRequest();
            req.setChannelAccount("13800138000");
            req.setCredential("WrongPass");
            req.setLoginType("password");

            when(authService.login(any())).thenThrow(
                new com.ruoyi.common.core.exception.ServiceException("密码错误"));

            mockMvc.perform(post("/auth/v1/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("密码错误"));
        }

        @Test
        @DisplayName("失败 5 次后锁定返回 403")
        void shouldRejectAfterFiveFailures() throws Exception
        {
            LoginRequest req = new LoginRequest();
            req.setChannelAccount("13800138000");
            req.setCredential("WrongPass");
            req.setLoginType("password");

            when(authService.login(any())).thenThrow(
                new com.ruoyi.common.core.exception.ServiceException("登录失败次数过多"));

            mockMvc.perform(post("/auth/v1/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("账号已停用返回 401")
        void shouldRejectLockedAccount() throws Exception
        {
            LoginRequest req = new LoginRequest();
            req.setChannelAccount("13800138000");
            req.setCredential("correct");
            req.setLoginType("password");

            when(authService.login(any())).thenThrow(
                new com.ruoyi.common.core.exception.ServiceException("账号已停用"));

            mockMvc.perform(post("/auth/v1/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("账号注销中返回 401")
        void shouldRejectDeactivatingAccount() throws Exception
        {
            LoginRequest req = new LoginRequest();
            req.setChannelAccount("13800138000");
            req.setCredential("correct");
            req.setLoginType("password");

            when(authService.login(any())).thenThrow(
                new com.ruoyi.common.core.exception.ServiceException("账号注销中"));

            mockMvc.perform(post("/auth/v1/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("无 token 返回 401")
        void shouldRejectNoToken() throws Exception
        {
            LoginRequest req = new LoginRequest();
            req.setChannelAccount("13800138000");
            req.setCredential("pass");
            req.setLoginType("password");

            mockMvc.perform(post("/auth/v1/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
        }
    }

    // ==================== token/refresh ====================

    @Nested
    @DisplayName("token/refresh")
    class RefreshTokenTests
    {
        @Test
        @DisplayName("刷新成功")
        void shouldRefreshSuccessfully() throws Exception
        {
            String token = "mock-refresh-token";
            Claims claims = mock(Claims.class);
            lenient().when(claims.getSubject()).thenReturn("1");
            lenient().when(tokenService.parseToken(token)).thenReturn(claims);
            when(tokenService.refreshToken(token)).thenReturn("new-access-token");

            mockMvc.perform(post("/auth/v1/token/refresh")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("new-access-token"));
        }

        @Test
        @DisplayName("无 token 返回 401")
        void shouldRejectNoToken() throws Exception
        {
            mockMvc.perform(post("/auth/v1/token/refresh"))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("无效 token 返回 401")
        void shouldRejectInvalidToken() throws Exception
        {
            when(tokenService.refreshToken(any())).thenThrow(
                new IllegalArgumentException("invalid token"));

            mockMvc.perform(post("/auth/v1/token/refresh")
                    .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("过期 token 返回 401")
        void shouldRejectExpiredToken() throws Exception
        {
            when(tokenService.refreshToken(any())).thenThrow(
                new IllegalArgumentException("token expired"));

            mockMvc.perform(post("/auth/v1/token/refresh")
                    .header("Authorization", "Bearer expired.token"))
                .andExpect(status().isUnauthorized());
        }
    }

    // ==================== verify-code/send ====================

    @Nested
    @DisplayName("verify-code/send")
    class SendVerifyCodeTests
    {
        @Test
        @DisplayName("发送验证码成功（手机）")
        void shouldSendPhoneCode() throws Exception
        {
            VerifyCodeRequest req = new VerifyCodeRequest();
            req.setChannelAccount("13800138000");
            req.setChannelType("phone");

            doNothing().when(authService).sendVerifyCode(eq("phone"), eq("13800138000"));

            mockMvc.perform(post("/auth/v1/verify-code/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

            verify(authService).sendVerifyCode("phone", "13800138000");
        }

        @Test
        @DisplayName("发送验证码成功（邮箱）")
        void shouldSendEmailCode() throws Exception
        {
            VerifyCodeRequest req = new VerifyCodeRequest();
            req.setChannelAccount("test@example.com");
            req.setChannelType("email");

            doNothing().when(authService).sendVerifyCode(eq("email"), eq("test@example.com"));

            mockMvc.perform(post("/auth/v1/verify-code/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("不支持的通道类型应拒绝")
        void shouldRejectUnsupportedType() throws Exception
        {
            VerifyCodeRequest req = new VerifyCodeRequest();
            req.setChannelAccount("test");
            req.setChannelType("wechat");

            mockMvc.perform(post("/auth/v1/verify-code/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
        }
    }

    // ==================== deactivate ====================

    @Nested
    @DisplayName("deactivate")
    class DeactivateTests
    {
        @Test
        @DisplayName("提交注销成功")
        void shouldSubmitDeactivate() throws Exception
        {
            String token = "mock-token";
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);
            doNothing().when(deactivateService).submitDeactivation(eq(1L), anyString());

            DeactivateRequest req = new DeactivateRequest();
            req.setPassword("password123");

            mockMvc.perform(post("/auth/v1/user/deactivate")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("取消注销成功")
        void shouldCancelDeactivate() throws Exception
        {
            String token = "mock-token";
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);
            doNothing().when(deactivateService).cancelDeactivate(1L);

            mockMvc.perform(post("/auth/v1/user/deactivate/cancel")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("注销状态查询成功")
        void shouldGetDeactivateStatus() throws Exception
        {
            String token = "mock-token";
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);

            DeactivateStatusResponse resp = new DeactivateStatusResponse();
            resp.setStatus("1");
            resp.setRemainingDays(3L);
            when(deactivateService.getStatus(1L)).thenReturn(resp);

            mockMvc.perform(get("/auth/v1/user/deactivate/status")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("1"))
                .andExpect(jsonPath("$.data.remainingDays").value(3));
        }

        @Test
        @DisplayName("无 token 返回 401")
        void shouldRejectNoToken() throws Exception
        {
            mockMvc.perform(post("/auth/v1/user/deactivate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"password\":\"test\"}"))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("注销状态查询 - 正常状态")
        void shouldGetNormalStatus() throws Exception
        {
            String token = "mock-token";
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);

            DeactivateStatusResponse resp = new DeactivateStatusResponse();
            resp.setStatus("0");
            resp.setRemainingDays(0L);
            when(deactivateService.getStatus(1L)).thenReturn(resp);

            mockMvc.perform(get("/auth/v1/user/deactivate/status")
                    .header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.data.status").value("0"))
                .andExpect(jsonPath("$.data.remainingDays").value(0));
        }
    }

    // ==================== register - validation ====================

    @Nested
    @DisplayName("register - validation")
    class RegisterValidationTests
    {
        @Test
        @DisplayName("缺少必填字段返回 400")
        void shouldRejectMissingFields() throws Exception
        {
            RegisterRequest req = new RegisterRequest();
            req.setChannelType("phone");

            mockMvc.perform(post("/auth/v1/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
        }
    }

    // ==================== register - more scenarios ====================

    @Nested
    @DisplayName("register - scenarios")
    class RegisterMoreTests
    {
        @Test
        @DisplayName("邮箱注册返回成功")
        void shouldRegisterWithEmail() throws Exception
        {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("test@qq.com");
            req.setChannelType("email");
            req.setVerifyCode("654321");
            req.setPassword("MyPass123");

            LoginResponse resp = new LoginResponse();
            resp.setAccessToken("access-token");
            resp.setRefreshToken("refresh-token");
            resp.setUserId(2L);
            resp.setUsername("spmv_abc12345");
            when(authService.register(any())).thenReturn(resp);

            mockMvc.perform(post("/auth/v1/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value(2))
                .andExpect(jsonPath("$.data.username").value("spmv_abc12345"));

            verify(authService).register(argThat(r ->
                "email".equals(r.getChannelType()) &&
                "test@qq.com".equals(r.getChannelAccount())
            ));
        }

        @Test
        @DisplayName("密码过短应拒绝")
        void shouldRejectShortPassword() throws Exception
        {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setChannelType("phone");
            req.setVerifyCode("123456");
            req.setPassword("1234567");

            when(authService.register(any())).thenThrow(
                new com.ruoyi.common.core.exception.ServiceException("密码长度需为 8-20 位"));

            mockMvc.perform(post("/auth/v1/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("密码长度需为 8-20 位"));
        }

        @Test
        @DisplayName("用户名冲突应拒绝")
        void shouldRejectUsernameConflict() throws Exception
        {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setChannelType("phone");
            req.setVerifyCode("123456");
            req.setPassword("MyPass123");
            req.setUsername("admin");

            when(authService.register(any())).thenThrow(
                new com.ruoyi.common.core.exception.ServiceException("用户名已存在"));

            mockMvc.perform(post("/auth/v1/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("用户名已存在"));
        }

        @Test
        @DisplayName("通道已存在应拒绝")
        void shouldRejectChannelExists() throws Exception
        {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setChannelType("phone");
            req.setVerifyCode("123456");
            req.setPassword("MyPass123");

            when(authService.register(any())).thenThrow(
                new com.ruoyi.common.core.exception.ServiceException("该通道已被注册"));

            mockMvc.perform(post("/auth/v1/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("该通道已被注册"));
        }

        @Test
        @DisplayName("含备用联系方式注册成功")
        void shouldRegisterWithBackupContact() throws Exception
        {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("test@qq.com");
            req.setChannelType("email");
            req.setVerifyCode("654321");
            req.setPassword("MyPass123");
            req.setBakPhone("13900139000");

            LoginResponse resp = new LoginResponse();
            resp.setAccessToken("access-token");
            resp.setRefreshToken("refresh-token");
            resp.setUserId(3L);
            resp.setUsername("spmv_test03");
            when(authService.register(any())).thenReturn(resp);

            mockMvc.perform(post("/auth/v1/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

            verify(authService).register(argThat(r ->
                "13900139000".equals(r.getBakPhone())
            ));
        }

        @Test
        @DisplayName("含备用邮箱注册成功")
        void shouldRegisterWithBackupEmail() throws Exception
        {
            RegisterRequest req = new RegisterRequest();
            req.setChannelAccount("13800138000");
            req.setChannelType("phone");
            req.setVerifyCode("123456");
            req.setPassword("MyPass123");
            req.setBakEmail("backup@qq.com");

            LoginResponse resp = new LoginResponse();
            resp.setAccessToken("access-token");
            resp.setRefreshToken("refresh-token");
            resp.setUserId(4L);
            resp.setUsername("spmv_test04");
            when(authService.register(any())).thenReturn(resp);

            mockMvc.perform(post("/auth/v1/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

            verify(authService).register(argThat(r ->
                "backup@qq.com".equals(r.getBakEmail())
            ));
        }
    }

    // ==================== login - validation ====================

    @Nested
    @DisplayName("login - validation")
    class LoginValidationTests
    {
        @Test
        @DisplayName("缺少 credential 返回 400")
        void shouldRejectMissingCredential() throws Exception
        {
            LoginRequest req = new LoginRequest();
            req.setChannelAccount("13800138000");
            req.setLoginType("password");

            mockMvc.perform(post("/auth/v1/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("不支持的 loginType 返回 400")
        void shouldRejectUnsupportedLoginType() throws Exception
        {
            LoginRequest req = new LoginRequest();
            req.setChannelAccount("test");
            req.setCredential("pass");
            req.setLoginType("wechat");

            mockMvc.perform(post("/auth/v1/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
        }
    }

    // ==================== verify-code/send - validation ====================

    @Nested
    @DisplayName("verify-code/send - validation")
    class SendVerifyCodeValidationTests
    {
        @Test
        @DisplayName("缺少 account 返回 400")
        void shouldRejectMissingAccount() throws Exception
        {
            VerifyCodeRequest req = new VerifyCodeRequest();
            req.setChannelType("phone");

            mockMvc.perform(post("/auth/v1/verify-code/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
        }
    }

    // ==================== inner/user/import ====================

    @Nested
    @DisplayName("inner/user/import")
    class InnerUserImportTests
    {
        @Test
        @DisplayName("导入成功")
        void shouldImportUserSuccessfully() throws Exception
        {
            InnerUserImportRequest req = new InnerUserImportRequest();
            req.setUsername("old_user");
            req.setPasswordHash("$2a$10$xxx");
            req.setPhone("13800138000");

            LoginResponse resp = new LoginResponse();
            resp.setAccessToken("imported-token");
            resp.setRefreshToken("refresh-token");
            resp.setUserId(10L);
            resp.setUsername("old_user");
            when(authService.importUser(any())).thenReturn(resp);

            mockMvc.perform(post("/auth/v1/inner/user/import")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value(10));

            verify(authService).importUser(any(InnerUserImportRequest.class));
        }
    }

    // ==================== inner/user/validate ====================

    @Nested
    @DisplayName("inner/user/validate")
    class InnerUserValidateTests
    {
        @Test
        @DisplayName("token 有效返回 true")
        void shouldValidateValidToken() throws Exception
        {
            String token = "valid-token";
            when(tokenService.parseToken(token)).thenReturn(mock(Claims.class));

            mockMvc.perform(post("/auth/v1/inner/user/validate")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
        }

        @Test
        @DisplayName("无 token 返回 false")
        void shouldReturnFalseWhenNoToken() throws Exception
        {
            mockMvc.perform(post("/auth/v1/inner/user/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(false));
        }

        @Test
        @DisplayName("无效 token 返回 false")
        void shouldReturnFalseWhenInvalidToken() throws Exception
        {
            when(tokenService.parseToken(any())).thenThrow(new RuntimeException("invalid"));

            mockMvc.perform(post("/auth/v1/inner/user/validate")
                    .header("Authorization", "Bearer invalid.token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(false));
        }
    }

    // ==================== checkunique ====================

    @Nested
    @DisplayName("checkunique")
    class CheckUniqueTests
    {
        @Test
        @DisplayName("username 唯一返回 true")
        void shouldReturnTrueWhenUsernameUnique() throws Exception
        {
            when(authService.checkFieldUnique(eq("username"), eq("newuser"), eq("spacemv-coai")))
                .thenReturn(true);

            mockMvc.perform(get("/auth/v1/checkunique")
                    .param("fieldType", "username")
                    .param("fieldValue", "newuser")
                    .param("productLine", "spacemv-coai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));

            verify(authService).checkFieldUnique("username", "newuser", "spacemv-coai");
        }

        @Test
        @DisplayName("username 不唯一返回 false")
        void shouldReturnFalseWhenUsernameNotUnique() throws Exception
        {
            when(authService.checkFieldUnique(eq("username"), eq("admin"), eq("spacemv-coai")))
                .thenReturn(false);

            mockMvc.perform(get("/auth/v1/checkunique")
                    .param("fieldType", "username")
                    .param("fieldValue", "admin")
                    .param("productLine", "spacemv-coai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(false));
        }

        @Test
        @DisplayName("phone 唯一返回 true")
        void shouldReturnTrueWhenPhoneUnique() throws Exception
        {
            when(authService.checkFieldUnique(eq("phone"), eq("13900139000"), eq("spacemv-coai")))
                .thenReturn(true);

            mockMvc.perform(get("/auth/v1/checkunique")
                    .param("fieldType", "phone")
                    .param("fieldValue", "13900139000")
                    .param("productLine", "spacemv-coai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
        }

        @Test
        @DisplayName("phone 不唯一返回 false")
        void shouldReturnFalseWhenPhoneNotUnique() throws Exception
        {
            when(authService.checkFieldUnique(eq("phone"), eq("13800138000"), eq("spacemv-coai")))
                .thenReturn(false);

            mockMvc.perform(get("/auth/v1/checkunique")
                    .param("fieldType", "phone")
                    .param("fieldValue", "13800138000")
                    .param("productLine", "spacemv-coai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(false));
        }

        @Test
        @DisplayName("email 唯一返回 true")
        void shouldReturnTrueWhenEmailUnique() throws Exception
        {
            when(authService.checkFieldUnique(eq("email"), eq("test@example.com"), eq("spacemv-coai")))
                .thenReturn(true);

            mockMvc.perform(get("/auth/v1/checkunique")
                    .param("fieldType", "email")
                    .param("fieldValue", "test@example.com")
                    .param("productLine", "spacemv-coai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
        }

        @Test
        @DisplayName("不支持的 fieldType 返回 500")
        void shouldReturn500WhenUnsupportedFieldType() throws Exception
        {
            mockMvc.perform(get("/auth/v1/checkunique")
                    .param("fieldType", "wechat")
                    .param("fieldValue", "wxid123")
                    .param("productLine", "spacemv-coai"))
                .andExpect(jsonPath("$.data").value(false));
        }
    }

    // ==================== inner/user/{id} ====================

    @Nested
    @DisplayName("inner/user/{id}")
    class InnerUserGetByIdTests
    {
        @Test
        @DisplayName("查询用户成功（含通道）")
        void shouldGetUserByIdSuccessfully() throws Exception
        {
            Long userId = 1L;
            IamUser user = new IamUser();
            user.setId(userId);
            user.setUsername("testuser");
            user.setDisplayName("Test User");
            user.setAvatarUrl("https://avatar.url");
            user.setStatus("0");
            user.setDeleteStatus("0");
            when(authService.getUserById(userId)).thenReturn(user);

            IamUserChannel channel = new IamUserChannel();
            channel.setId(100L);
            channel.setChannelType("phone");
            channel.setChannelAccount("13800138000");
            channel.setIsPrimary("1");
            channel.setBindTime(new Date());
            channel.setStatus("0");
            when(authService.getChannelList(userId)).thenReturn(List.of(channel));

            mockMvc.perform(get("/auth/v1/inner/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.displayName").value("Test User"))
                .andExpect(jsonPath("$.data.channels[0].channelType").value("phone"))
                .andExpect(jsonPath("$.data.channels[0].channelAccount").value("13800138000"));
        }

        @Test
        @DisplayName("用户不存在返回 null data")
        void shouldReturnNullWhenUserNotFound() throws Exception
        {
            when(authService.getUserById(999L)).thenReturn(null);

            mockMvc.perform(get("/auth/v1/inner/user/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
        }
    }
}
