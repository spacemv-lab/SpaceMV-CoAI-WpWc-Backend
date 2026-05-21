package intergration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.iam.controller.AuthController;
import com.ruoyi.iam.dto.DeactivateRequest;
import com.ruoyi.iam.dto.DeactivateStatusResponse;
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
 * Deactivate 功能集成测试（I-DACT-01 到 I-DACT-14）
 * 注：注销端点在 AuthController 中，此处单独分组测试
 *
 * @author txwx
 */
@DisplayName("Deactivate Integration Tests (I-DACT)")
@ExtendWith(MockitoExtension.class)
class DeactivateControllerTest
{
    @Mock
    private DeactivateService deactivateService;

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthService authService;

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

    // ==================== POST /deactivate ====================

    @Nested
    @DisplayName("POST /deactivate")
    class SubmitDeactivateTests
    {
        private DeactivateRequest buildRequest()
        {
            DeactivateRequest req = new DeactivateRequest();
            req.setPassword("MyPass123");
            return req;
        }

        private Claims buildClaims()
        {
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            return claims;
        }

        @Test
        @DisplayName("提交注销申请成功 I-DACT-01")
        void shouldSubmitSuccessfully() throws Exception
        {
            String token = "mock-token";
            Claims claims = mock(Claims.class, withSettings().lenient());
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);
            doNothing().when(deactivateService).submitDeactivation(eq(1L), eq("MyPass123"));

            mockMvc.perform(post("/auth/v1/user/deactivate")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("密码错误应拒绝 I-DACT-02")
        void shouldRejectWrongPassword() throws Exception
        {
            String token = "mock-token";
            Claims claims = buildClaims(); when(tokenService.parseToken(token)).thenReturn(claims);
            doThrow(new com.ruoyi.common.core.exception.ServiceException("密码错误"))
                .when(deactivateService).submitDeactivation(eq(1L), eq("WrongPass"));

            DeactivateRequest req = new DeactivateRequest();
            req.setPassword("WrongPass");

            mockMvc.perform(post("/auth/v1/user/deactivate")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("密码错误"));
        }

        @Test
        @DisplayName("已注销中应拒绝 I-DACT-03")
        void shouldRejectAlreadyDeactivating() throws Exception
        {
            String token = "mock-token";
            Claims claims = buildClaims(); when(tokenService.parseToken(token)).thenReturn(claims);
            doThrow(new com.ruoyi.common.core.exception.ServiceException("账号已处于注销申请中"))
                .when(deactivateService).submitDeactivation(eq(1L), anyString());

            mockMvc.perform(post("/auth/v1/user/deactivate")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("账号已处于注销申请中"));
        }

        @Test
        @DisplayName("账号已停用应拒绝 I-DACT-04")
        void shouldRejectLockedAccount() throws Exception
        {
            String token = "mock-token";
            Claims claims = buildClaims(); when(tokenService.parseToken(token)).thenReturn(claims);
            doThrow(new com.ruoyi.common.core.exception.ServiceException("账号已停用"))
                .when(deactivateService).submitDeactivation(eq(1L), anyString());

            mockMvc.perform(post("/auth/v1/user/deactivate")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("无 token 返回 401 I-DACT-05")
        void shouldRejectNoToken() throws Exception
        {
            mockMvc.perform(post("/auth/v1/user/deactivate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("缺少密码应拒绝 I-DACT-06")
        void shouldRejectMissingPassword() throws Exception
        {
            String token = "mock-token";
            Claims claims = buildClaims(); when(tokenService.parseToken(token)).thenReturn(claims);
            doThrow(new com.ruoyi.common.core.exception.ServiceException("密码错误"))
                .when(deactivateService).submitDeactivation(eq(1L), isNull());
            DeactivateRequest req = new DeactivateRequest();
            req.setPassword(null);

            mockMvc.perform(post("/auth/v1/user/deactivate")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
        }
    }

    // ==================== GET /deactivate/status ====================

    @Nested
    @DisplayName("GET /deactivate/status")
    class GetStatusTests
    {
        private Claims buildClaims()
        {
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            return claims;
        }

        @Test
        @DisplayName("查询冷静期状态 I-DACT-07")
        void shouldReturnCoolingDownStatus() throws Exception
        {
            String token = "mock-token";
            Claims claims = buildClaims(); when(tokenService.parseToken(token)).thenReturn(claims);

            DeactivateStatusResponse resp = new DeactivateStatusResponse();
            resp.setStatus("1");
            resp.setRemainingDays(7L);
            resp.setDeleteScheduledAt(new java.util.Date(System.currentTimeMillis() + 7L * 86400000L));
            when(deactivateService.getStatus(1L)).thenReturn(resp);

            mockMvc.perform(get("/auth/v1/user/deactivate/status")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("1"))
                .andExpect(jsonPath("$.data.remainingDays").value(7))
                .andExpect(jsonPath("$.data.deleteScheduledAt").exists());
        }

        @Test
        @DisplayName("查询正常状态 I-DACT-08")
        void shouldReturnNormalStatus() throws Exception
        {
            String token = "mock-token";
            Claims claims = buildClaims(); when(tokenService.parseToken(token)).thenReturn(claims);

            DeactivateStatusResponse resp = new DeactivateStatusResponse();
            resp.setStatus("0");
            resp.setRemainingDays(0L);
            when(deactivateService.getStatus(1L)).thenReturn(resp);

            mockMvc.perform(get("/auth/v1/user/deactivate/status")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("0"))
                .andExpect(jsonPath("$.data.remainingDays").value(0));
        }

        @Test
        @DisplayName("查询已注销状态 I-DACT-09")
        void shouldReturnDeletedStatus() throws Exception
        {
            String token = "mock-token";
            Claims claims = buildClaims(); when(tokenService.parseToken(token)).thenReturn(claims);

            DeactivateStatusResponse resp = new DeactivateStatusResponse();
            resp.setStatus("2");
            resp.setRemainingDays(0L);
            resp.setDeleteScheduledAt(new java.util.Date());
            when(deactivateService.getStatus(1L)).thenReturn(resp);

            mockMvc.perform(get("/auth/v1/user/deactivate/status")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("2"));
        }

        @Test
        @DisplayName("无 token 返回 401 I-DACT-10")
        void shouldRejectNoToken() throws Exception
        {
            mockMvc.perform(get("/auth/v1/user/deactivate/status"))
                .andExpect(status().isUnauthorized());
        }
    }

    // ==================== POST /deactivate/cancel ====================

    @Nested
    @DisplayName("POST /deactivate/cancel")
    class CancelDeactivateTests
    {
        private Claims buildClaims()
        {
            Claims claims = mock(Claims.class, withSettings().lenient());
            when(claims.getSubject()).thenReturn("1");
            return claims;
        }

        @Test
        @DisplayName("取消注销成功 I-DACT-11")
        void shouldCancelSuccessfully() throws Exception
        {
            String token = "mock-token";
            Claims claims = buildClaims(); when(tokenService.parseToken(token)).thenReturn(claims);
            doNothing().when(deactivateService).cancelDeactivate(1L);

            mockMvc.perform(post("/auth/v1/user/deactivate/cancel")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("不在冷静期应拒绝 I-DACT-12")
        void shouldRejectNotInCooldown() throws Exception
        {
            String token = "mock-token";
            Claims claims = buildClaims(); when(tokenService.parseToken(token)).thenReturn(claims);
            doThrow(new com.ruoyi.common.core.exception.ServiceException("不在冷静期"))
                .when(deactivateService).cancelDeactivate(1L);

            mockMvc.perform(post("/auth/v1/user/deactivate/cancel")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("不在冷静期"));
        }

        @Test
        @DisplayName("已注销用户应拒绝 I-DACT-13")
        void shouldRejectDeletedUser() throws Exception
        {
            String token = "mock-token";
            Claims claims = buildClaims(); when(tokenService.parseToken(token)).thenReturn(claims);
            doThrow(new com.ruoyi.common.core.exception.ServiceException("账号已注销"))
                .when(deactivateService).cancelDeactivate(1L);

            mockMvc.perform(post("/auth/v1/user/deactivate/cancel")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("无 token 返回 401 I-DACT-14")
        void shouldRejectNoToken() throws Exception
        {
            mockMvc.perform(post("/auth/v1/user/deactivate/cancel"))
                .andExpect(status().isUnauthorized());
        }
    }
}
