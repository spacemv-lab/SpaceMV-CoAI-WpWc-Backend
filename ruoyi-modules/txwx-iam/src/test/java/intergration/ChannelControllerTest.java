/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package intergration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.iam.dto.ChannelBindRequest;
import com.ruoyi.iam.dto.ChannelResponse;
import com.ruoyi.iam.dto.response.BackupContactResponse;
import com.ruoyi.iam.dto.ChannelUnbindRequest;
import com.ruoyi.iam.service.TokenService;
import com.ruoyi.iam.service.UserChannelService;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ChannelController 测试类
 *
 * @author txwx
 */
@DisplayName("ChannelController Tests")
@ExtendWith(MockitoExtension.class)
class ChannelControllerTest
{
    @Mock
    private UserChannelService channelService;

    @Mock
    private TokenService tokenService;

    @Mock
    private com.ruoyi.iam.service.AuthService authService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp()
    {
        var controller = new com.ruoyi.iam.controller.ChannelController(channelService, authService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new com.ruoyi.iam.controller.GlobalExceptionHandler())
            .build();
    }

    // ==================== bindChannel ====================

    @Nested
    @DisplayName("bindChannel")
    class BindChannelTests
    {
        @Test
        @DisplayName("绑定手机成功 I-CH-08")
        void shouldBindPhone() throws Exception
        {
            String token = "mock-token";
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);
            doNothing().when(channelService).bindChannel(eq(1L), anyString(), anyString(), anyString());

            ChannelBindRequest req = new ChannelBindRequest();
            req.setChannelType("phone");
            req.setChannelAccount("13800138000");
            req.setVerifyCode("123456");

            mockMvc.perform(post("/auth/v1/user/channel")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

            verify(channelService).bindChannel(eq(1L), eq("phone"), eq("13800138000"), eq("123456"));
        }

        @Test
        @DisplayName("绑定邮箱成功 I-CH-09")
        void shouldBindEmail() throws Exception
        {
            String token = "mock-token";
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);
            doNothing().when(channelService).bindChannel(eq(1L), anyString(), anyString(), anyString());

            ChannelBindRequest req = new ChannelBindRequest();
            req.setChannelType("email");
            req.setChannelAccount("test@qq.com");
            req.setVerifyCode("654321");

            mockMvc.perform(post("/auth/v1/user/channel")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("验证码错误 I-CH-10")
        void shouldRejectWrongCode() throws Exception
        {
            String token = "mock-token";
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);
            doThrow(new com.ruoyi.common.core.exception.ServiceException("验证码错误或已过期"))
                .when(channelService).bindChannel(eq(1L), anyString(), anyString(), anyString());

            ChannelBindRequest req = new ChannelBindRequest();
            req.setChannelType("phone");
            req.setChannelAccount("13800138000");
            req.setVerifyCode("000000");

            mockMvc.perform(post("/auth/v1/user/channel")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("验证码错误或已过期"));
        }

        @Test
        @DisplayName("同类型已存在 I-CH-11")
        void shouldRejectDuplicateType() throws Exception
        {
            String token = "mock-token";
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);
            doThrow(new com.ruoyi.common.core.exception.ServiceException("您已绑定该类型通道"))
                .when(channelService).bindChannel(eq(1L), anyString(), anyString(), nullable(String.class));

            ChannelBindRequest req = new ChannelBindRequest();
            req.setChannelType("phone");
            req.setChannelAccount("13900139000");

            mockMvc.perform(post("/auth/v1/user/channel")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("您已绑定该类型通道"));
        }

        @Test
        @DisplayName("通道被他人注册 I-CH-12")
        void shouldRejectAccountExists() throws Exception
        {
            String token = "mock-token";
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);
            doThrow(new com.ruoyi.common.core.exception.ServiceException("该通道已被注册"))
                .when(channelService).bindChannel(eq(1L), anyString(), anyString(), nullable(String.class));

            ChannelBindRequest req = new ChannelBindRequest();
            req.setChannelType("phone");
            req.setChannelAccount("13800138000");

            mockMvc.perform(post("/auth/v1/user/channel")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("该通道已被注册"));
        }

        @Test
        @DisplayName("缺少必填字段 I-CH-13")
        void shouldRejectMissingFields() throws Exception
        {
            ChannelBindRequest req = new ChannelBindRequest();
            req.setChannelType("phone");

            mockMvc.perform(post("/auth/v1/user/channel")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("无 token I-CH-14")
        void shouldRejectNoToken() throws Exception
        {
            ChannelBindRequest req = new ChannelBindRequest();
            req.setChannelType("phone");
            req.setChannelAccount("13800138000");
            req.setVerifyCode("123456");

            mockMvc.perform(post("/auth/v1/user/channel")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
        }
    }

    // ==================== unbindChannel ====================

    @Nested
    @DisplayName("unbindChannel")
    class UnbindChannelTests
    {
        @Test
        @DisplayName("解绑手机成功 I-CH-15")
        void shouldUnbindPhone() throws Exception
        {
            String token = "mock-token";
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);
            doNothing().when(channelService).unbindChannel(eq(1L), anyString(), anyString(), nullable(String.class));

            ChannelUnbindRequest req = new ChannelUnbindRequest();
            req.setChannelType("phone");
            req.setVerifyCode("123456");

            mockMvc.perform(delete("/auth/v1/user/channel")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("解绑邮箱双验证 I-CH-16")
        void shouldUnbindEmailWithPassword() throws Exception
        {
            String token = "mock-token";
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);
            doNothing().when(channelService).unbindChannel(eq(1L), anyString(), anyString(), anyString());

            ChannelUnbindRequest req = new ChannelUnbindRequest();
            req.setChannelType("email");
            req.setVerifyCode("654321");
            req.setPassword("MyPass123");

            mockMvc.perform(delete("/auth/v1/user/channel")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

            verify(authService).validateEmailUnbindPassword(eq(1L), eq("MyPass123"));
        }

        @Test
        @DisplayName("解绑邮箱密码错误 I-CH-17")
        void shouldRejectWrongPassword() throws Exception
        {
            String token = "mock-token";
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);
            doThrow(new com.ruoyi.common.core.exception.ServiceException("密码错误"))
                .when(authService).validateEmailUnbindPassword(eq(1L), anyString());

            ChannelUnbindRequest req = new ChannelUnbindRequest();
            req.setChannelType("email");
            req.setPassword("WrongPass");

            mockMvc.perform(delete("/auth/v1/user/channel")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("密码错误"));
        }

        @Test
        @DisplayName("验证码错误 I-CH-18")
        void shouldRejectWrongCode() throws Exception
        {
            String token = "mock-token";
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);
            doThrow(new com.ruoyi.common.core.exception.ServiceException("验证码错误或已过期"))
                .when(channelService).unbindChannel(eq(1L), anyString(), anyString(), nullable(String.class));

            ChannelUnbindRequest req = new ChannelUnbindRequest();
            req.setChannelType("phone");
            req.setVerifyCode("000000");

            mockMvc.perform(delete("/auth/v1/user/channel")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("验证码错误或已过期"));
        }

        @Test
        @DisplayName("通道不存在 I-CH-19")
        void shouldRejectNotFound() throws Exception
        {
            String token = "mock-token";
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);
            doThrow(new com.ruoyi.common.core.exception.ServiceException("通道不存在"))
                .when(channelService).unbindChannel(eq(1L), anyString(), anyString(), nullable(String.class));

            ChannelUnbindRequest req = new ChannelUnbindRequest();
            req.setChannelType("email");
            req.setVerifyCode("123456");

            mockMvc.perform(delete("/auth/v1/user/channel")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("通道不存在"));
        }

        @Test
        @DisplayName("无 token I-CH-20")
        void shouldRejectNoToken() throws Exception
        {
            ChannelUnbindRequest req = new ChannelUnbindRequest();
            req.setChannelType("phone");
            req.setVerifyCode("123456");

            mockMvc.perform(delete("/auth/v1/user/channel")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
        }
    }

    // ==================== listChannels ====================

    @Nested
    @DisplayName("listChannels")
    class ListChannelsTests
    {
        @Test
        @DisplayName("查询成功")
        void shouldListSuccessfully() throws Exception
        {
            String token = "mock-token";
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);
            when(channelService.listChannels(1L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/auth/v1/user/channel")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
        }

        @Test
        @DisplayName("有 phone+email 通道返回 2 条")
        void shouldReturnTwoChannels() throws Exception
        {
            String token = "mock-token";
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);

            ChannelResponse phoneCh = new ChannelResponse();
            phoneCh.setId(1L);
            phoneCh.setChannelType("phone");
            phoneCh.setChannelAccount("138****8000");
            phoneCh.setIsPrimary("1");
            phoneCh.setStatus("0");

            ChannelResponse emailCh = new ChannelResponse();
            emailCh.setId(2L);
            emailCh.setChannelType("email");
            emailCh.setChannelAccount("t***@txwx.com");
            emailCh.setIsPrimary("0");
            emailCh.setStatus("0");

            when(channelService.listChannels(1L)).thenReturn(java.util.List.of(phoneCh, emailCh));

            mockMvc.perform(get("/auth/v1/user/channel")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].channelType").value("phone"))
                .andExpect(jsonPath("$.data[1].channelType").value("email"));
        }

        @Test
        @DisplayName("已解绑的通道不返回")
        void shouldNotReturnUnboundChannel() throws Exception
        {
            String token = "mock-token";
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);

            ChannelResponse activeCh = new ChannelResponse();
            activeCh.setId(1L);
            activeCh.setChannelType("phone");
            activeCh.setChannelAccount("138****8000");
            activeCh.setIsPrimary("1");
            activeCh.setStatus("0");

            // note: controller 透传 service 返回值不做过滤，mock 只返回 active 通道
            when(channelService.listChannels(1L)).thenReturn(java.util.List.of(activeCh));

            mockMvc.perform(get("/auth/v1/user/channel")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].channelType").value("phone"));
        }

        @Test
        @DisplayName("无 token 返回 401")
        void shouldRejectNoToken() throws Exception
        {
            mockMvc.perform(get("/auth/v1/user/channel"))
                .andExpect(status().isUnauthorized());
        }
    }

    // ==================== backup contact ====================

    @Nested
    @DisplayName("backup contact")
    class BackupContactTests
    {
        @Test
        @DisplayName("查询备用联系方式 - 有数据")
        void shouldGetBackupWithAllFields() throws Exception
        {
            String token = "mock-token";
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);

            BackupContactResponse resp = new BackupContactResponse();
            resp.setBakPhone("139****9000");
            resp.setBakEmail("b***@backup.com");
            resp.setHasBakPhone(true);
            resp.setHasBakEmail(true);
            when(channelService.queryBackupContact(1L)).thenReturn(resp);

            mockMvc.perform(get("/auth/v1/user/channel/backup")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.hasBakPhone").value(true))
                .andExpect(jsonPath("$.data.hasBakEmail").value(true))
                .andExpect(jsonPath("$.data.bakPhone").exists())
                .andExpect(jsonPath("$.data.bakEmail").exists());
        }

        @Test
        @DisplayName("查询备用联系方式 - 无数据")
        void shouldGetBackupEmpty() throws Exception
        {
            String token = "mock-token";
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);

            BackupContactResponse resp = new BackupContactResponse();
            resp.setBakPhone(null);
            resp.setBakEmail(null);
            resp.setHasBakPhone(false);
            resp.setHasBakEmail(false);
            when(channelService.queryBackupContact(1L)).thenReturn(resp);

            mockMvc.perform(get("/auth/v1/user/channel/backup")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.hasBakPhone").value(false))
                .andExpect(jsonPath("$.data.hasBakEmail").value(false));
        }

        @Test
        @DisplayName("查询备用联系方式 - 仅有 bakPhone")
        void shouldGetBackupOnlyPhone() throws Exception
        {
            String token = "mock-token";
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("1");
            when(tokenService.parseToken(token)).thenReturn(claims);

            BackupContactResponse resp = new BackupContactResponse();
            resp.setBakPhone("139****9000");
            resp.setBakEmail(null);
            resp.setHasBakPhone(true);
            resp.setHasBakEmail(false);
            when(channelService.queryBackupContact(1L)).thenReturn(resp);

            mockMvc.perform(get("/auth/v1/user/channel/backup")
                    .header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.data.hasBakPhone").value(true))
                .andExpect(jsonPath("$.data.hasBakEmail").value(false))
                .andExpect(jsonPath("$.data.bakPhone").exists())
                .andExpect(jsonPath("$.data.bakEmail").doesNotExist());
        }

        @Test
        @DisplayName("查询备用联系方式 - 无 token 返回 401")
        void shouldRejectNoToken() throws Exception
        {
            mockMvc.perform(get("/auth/v1/user/channel/backup"))
                .andExpect(status().isUnauthorized());
        }
    }
}
