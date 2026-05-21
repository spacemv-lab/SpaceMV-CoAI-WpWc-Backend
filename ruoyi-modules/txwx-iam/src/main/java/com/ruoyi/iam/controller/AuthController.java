package com.ruoyi.iam.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.iam.filter.IamTokenValidationFilter;
import com.ruoyi.iam.dto.*;
import com.ruoyi.iam.service.AuthService;
import com.ruoyi.iam.service.DeactivateService;
import com.ruoyi.iam.service.IamValidateCodeService;
import com.ruoyi.iam.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 认证入口 — 注册/登录/token刷新/验证码
 *
 * @author txwx
 */
@RestController
@RequestMapping("/auth/v1")
@Tag(name = "01--【IAM】--认证")
public class AuthController
{
    public final AuthService authService;
    private final TokenService tokenService;
    private final DeactivateService deactivateService;
    private final IamValidateCodeService validateCodeService;

    public AuthController(AuthService authService, TokenService tokenService,
                          DeactivateService deactivateService, IamValidateCodeService validateCodeService)
    {
        this.authService = authService;
        this.tokenService = tokenService;
        this.deactivateService = deactivateService;
        this.validateCodeService = validateCodeService;
    }

    /**
     * 获取图形验证码（迁移自 Gateway /code）
     */
    @GetMapping("/code")
    @Operation(summary = "获取图形验证码")
    public R<Map<String, Object>> captcha() throws java.io.IOException
    {
        Map<String, Object> captcha = validateCodeService.createCaptcha();
        return R.ok(captcha);
    }

    /**
     * 校验图形验证码（迁移自 Gateway /auth/checkHuman）
     * <p>
     * 前端在调用注册/登录/修改密码/解绑等敏感操作前，先调此接口验证人机。
     * 验证成功后再调用业务接口，业务接口上的 {@code @CaptchaValidate} 会二次校验。
     */
    @PostMapping("/checkHuman")
    @Operation(summary = "校验图形验证码")
    public R<String> checkHuman(@Parameter(description = "验证码请求") @RequestBody CaptchaCheckRequest req)
    {
        validateCodeService.checkCaptcha(req.getCode(), req.getUuid());
        return R.ok("成功");
    }

    /**
     * 字段唯一性校验（前端注册/登录页调用）
     * <p>
     * fieldType 支持：username / phone / email / all
     * <pre>
     * - "all"：跨所有字段统一校验（username + phone + email 任一被占用返回 false）
     * </pre>
     */
    @GetMapping(value = "/checkunique", params = "fieldType")
    @Operation(summary = "字段唯一性校验（三参数版）")
    public R<Boolean> checkUnique(
            @Parameter(description = "字段类型: username/phone/email/all") @RequestParam String fieldType,
            @Parameter(description = "字段值") @RequestParam String fieldValue,
            @Parameter(description = "产品线标识") @RequestParam String productLine)
    {
        boolean unique = authService.checkFieldUnique(fieldType, fieldValue, productLine);
        return R.ok(unique);
    }

    /**
     * 字段唯一性校验（前端注册页新接口，自动识别类型 + productLine 隔离）
     * <p>
     * 第一层：按 productLine 过滤（作用域隔离）<br>
     * 第二层：根据 accountName 自动识别：手机号→phone通道 / 含@→email通道 / 其他→username<br>
     * <b>另：所有类型均额外校验 display_name</b>
     * <p>
     * 使用方式：{@code GET /auth/v1/checkunique?accountName=xxx&productLine=spacemv-coai}
     */
    @GetMapping(value = "/checkunique", params = {"accountName", "productLine"})
    @Operation(summary = "字段唯一性校验（自动识别类型+productLine版）")
    public R<Boolean> checkUniqueByAccountName(
            @Parameter(description = "待校验的值（手机号/邮箱/用户名）") @RequestParam String accountName,
            @Parameter(description = "产品线标识") @RequestParam String productLine)
    {
        boolean unique = authService.checkFieldUnique(accountName, productLine);
        return R.ok(unique);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public R<LoginResponse> register(@Parameter(description = "注册信息") @RequestBody RegisterRequest req) throws Exception {
        // 参数校验
        if (req.getChannelAccount() == null || req.getChannelAccount().isEmpty() ||
            req.getVerifyCode() == null || req.getVerifyCode().isEmpty() ||
            req.getPassword() == null || req.getPassword().isEmpty())
        {
            throw new com.ruoyi.common.core.exception.ServiceException("缺少必填参数");
        }
        LoginResponse resp = authService.register(req);
        return R.ok(resp);
    }

    /**
     * 快捷登录
     */
    //@CaptchaValidate
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public R<LoginResponse> login(@Parameter(description = "登录凭证") @RequestBody LoginRequest req)
    {
        // 参数校验
        if (req.getCredential() == null || req.getCredential().isEmpty())
        {
            throw new com.ruoyi.common.core.exception.ServiceException("缺少登录凭证");
        }
        if (req.getLoginType() == null ||
            (!"password".equals(req.getLoginType()) && !"sms".equals(req.getLoginType())))
        {
            throw new com.ruoyi.common.core.exception.ServiceException("不支持的登录方式");
        }

        LoginResponse resp = authService.login(req);
        if (resp == null)
        {
            return R.fail("登录失败");
        }
        return R.ok(resp);
    }

    /**
     * 退出登录
     * <p>
     * 从 Authorization header 解析 Bearer token，
     * 删除 Redis 中的 LoginUser 缓存，使当前 token 失效。
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public R<String> logout(HttpServletRequest request)
    {
        String token = extractToken(request);
        if (token == null)
        {
            return R.ok("已退出");
        }
        authService.logout(token);
        return R.ok("退出成功");
    }

    /**
     * 刷新 token
     */
    @PostMapping("/token/refresh")
    @Operation(summary = "刷新令牌")
    public R<String> refreshToken(HttpServletRequest request)
    {
        String token = extractToken(request);
        if (token == null)
        {
            throw new IllegalArgumentException("Missing token");
        }
        String newToken = tokenService.refreshToken(token);
        return R.ok(newToken);
    }

    /**
     * 校验验证码（迁移自 CRM /verify/code/check）
     * <p>
     * 自动识别手机号/邮箱，用于忘记密码等需要验证码校验的场景。
     * 注意：此接口会消费验证码（防重放），校验成功后不可重复使用同一验证码。
     *
     * @param account 手机号或邮箱
     * @param code    验证码
     * @return true=验证通过，false=失败
     */
    @PostMapping("/verify-code/check")
    @Operation(summary = "校验验证码")
    public R<Boolean> checkVerifyCode(
            @RequestParam String account,
            @RequestParam String code)
    {
        if (account == null || account.isEmpty() || code == null || code.isEmpty())
        {
            return R.ok(false);
        }
        boolean result = authService.checkVerifyCode(account, code);
        return R.ok(result);
    }

    /**
     * 发送验证码
     */
    @PostMapping("/verify-code/send")
    @Operation(summary = "发送验证码")
    public R<String> sendVerifyCode(@Parameter(description = "验证码请求") @RequestBody VerifyCodeRequest req)
    {
        if (req.getChannelAccount() == null || req.getChannelAccount().isEmpty())
        {
            throw new com.ruoyi.common.core.exception.ServiceException("缺少通道账号");
        }
        boolean flag = false;
        if ("phone".equals(req.getChannelType()))
        {
            flag = authService.sendVerifyCode("phone", req.getChannelAccount());
        }
        else if ("email".equals(req.getChannelType()))
        {
            flag = authService.sendVerifyCode("email", req.getChannelAccount());
        }
        else
        {
            throw new com.ruoyi.common.core.exception.ServiceException("不支持的通道类型");
        }
        return flag ? R.ok("验证码已发送") : R.fail("发送失败");
    }

    /**
     * 登录态验证码发送
     * <p>
     * 适用于已登录用户主动发送验证码的场景（如解绑、修改密码等）。
     * 需要 Authorization header 传入 IAM token。
     * <p>
     * 策略：<br>
     * 1. useFrontAccount=true → 直接用前端传入的 channelAccount<br>
     * 2. useFrontAccount=false（默认）→ 优先按用户 ID 查已绑定的通道账号<br>
     * 3. DB 无记录 → 用前端传入的 channelAccount 兜底
     */
    @PostMapping("/send-verify-code")
    @Operation(summary = "登录态验证码发送（优先查 DB 通道）")
    public R<String> sendAuthVerifyCode(HttpServletRequest request,
                                        @Parameter(description = "登录态验证码请求") @RequestBody SendAuthVerifyCodeRequest req)
    {
        Long userId = (Long) request.getAttribute(IamTokenValidationFilter.IAM_USER_ID_ATTR);
        if (userId == null)
        {
            throw new IllegalArgumentException("Missing iam_user_id attribute — filter may not have run");
        }
        if (req.getChannelType() == null || req.getChannelType().isEmpty())
        {
            throw new com.ruoyi.common.core.exception.ServiceException("缺少通道类型");
        }
        if (!"phone".equals(req.getChannelType()) && !"email".equals(req.getChannelType()))
        {
            throw new com.ruoyi.common.core.exception.ServiceException("不支持的通道类型");
        }
        if (req.getChannelAccount() == null || req.getChannelAccount().isEmpty())
        {
            throw new com.ruoyi.common.core.exception.ServiceException("缺少通道账号");
        }

        // 旧逻辑：查库校验所有权后再发送（注释保留）
        // authService.sendAuthVerifyCode(userId, req);
        // 新逻辑：直接用前端传过来的通道账号发送验证码，不再查库
        boolean flag = authService.sendVerifyCode(req.getChannelType(), req.getChannelAccount());
        return flag ? R.ok("验证码已发送") : R.fail("发送失败");
    }

    /**
     * 忘记密码 — 重置密码（迁移自 CRM forgetPassword 流程）
     * <p>
     * 前端流程：先调用 /verify-code/send 发送验证码，
     * 再调用此接口传入验证码 + 新密码完成重置。
     * 无需登录态。
     */
    @PostMapping("/password/forget/reset")
    @Operation(summary = "忘记密码重置")
    public R<String> forgetResetPassword(@Parameter(description = "忘记密码重置请求") @RequestBody ForgetPasswordRequest req)
    {
        if (req.getChannelAccount() == null || req.getChannelAccount().isEmpty())
        {
            throw new com.ruoyi.common.core.exception.ServiceException("缺少账号");
        }
        if (req.getVerifyCode() == null || req.getVerifyCode().isEmpty())
        {
            throw new com.ruoyi.common.core.exception.ServiceException("缺少验证码");
        }
        if (req.getNewPassword() == null || req.getNewPassword().isEmpty())
        {
            throw new com.ruoyi.common.core.exception.ServiceException("缺少新密码");
        }
        authService.resetPassword(req.getChannelAccount(), req.getVerifyCode(), req.getNewPassword());
        return R.ok("密码重置成功");
    }

    /**
     * 注销申请
     * <p>
     * 注意：不走 tokenService.parseToken() 重新解析 token，而是直接读取
     * {@link IamTokenValidationFilter} 已设置好的 {@code iam_user_id} 请求属性，
     * 避免 RuoYi 硬编码密钥 vs IAM Nacos 密钥不一致导致的 SignatureException。
     */
    @PostMapping("/user/deactivate")
    @Operation(summary = "提交注销申请")
    public R<String> deactivate(HttpServletRequest request, @Parameter(description = "注销密码") @RequestBody DeactivateRequest req)
    {
        Long userId = (Long) request.getAttribute(IamTokenValidationFilter.IAM_USER_ID_ATTR);
        if (userId == null)
        {
            throw new IllegalArgumentException("Missing iam_user_id attribute — filter may not have run");
        }
        deactivateService.submitDeactivation(userId, req.getPassword());
        return R.ok("注销申请已提交");
    }

    /**
     * 取消注销
     */
    @PostMapping("/user/deactivate/cancel")
    @Operation(summary = "取消注销申请")
    public R<String> cancelDeactivate(HttpServletRequest request)
    {
        Long userId = (Long) request.getAttribute(IamTokenValidationFilter.IAM_USER_ID_ATTR);
        if (userId == null)
        {
            throw new IllegalArgumentException("Missing iam_user_id attribute — filter may not have run");
        }
        deactivateService.cancelDeactivate(userId);
        return R.ok("已取消注销");
    }

    /**
     * 注销状态查询
     */
    @GetMapping("/user/deactivate/status")
    @Operation(summary = "查询注销状态")
    public R<DeactivateStatusResponse> deactivateStatus(HttpServletRequest request)
    {
        Long userId = (Long) request.getAttribute(IamTokenValidationFilter.IAM_USER_ID_ATTR);
        if (userId == null)
        {
            throw new IllegalArgumentException("Missing iam_user_id attribute — filter may not have run");
        }
        DeactivateStatusResponse resp = deactivateService.getStatus(userId);
        return R.ok(resp);
    }

    /**
     * 存量用户惰性迁移导入（内部接口）
     */
    @PostMapping("/inner/user/import")
    @Operation(summary = "存量用户惰性迁移导入")
    public R<LoginResponse> importUser(@Parameter(description = "用户导入请求") @RequestBody com.ruoyi.iam.dto.InnerUserImportRequest req)
    {
        LoginResponse resp = authService.importUser(req);
        return R.ok(resp);
    }

    /**
     * 内部接口：验证 token 有效性（供 Feign 调用）
     */
    @PostMapping("/inner/user/validate")
    @Operation(summary = "验证 Token 有效性")
    public R<Boolean> validateToken(HttpServletRequest request)
    {
        String token = extractToken(request);
        if (token == null)
        {
            return R.ok(false);
        }
        try
        {
            tokenService.parseToken(token);
            return R.ok(true);
        }
        catch (Exception e)
        {
            return R.ok(false);
        }
    }

    /**
     * 内部接口：按 ID 查询用户（供 Feign 调用）
     */
    @GetMapping("/inner/user/{id}")
    @Operation(summary = "按 ID 查询用户")
    public R<UserLoginUserDTO> getUserById(
            @Parameter(description = "用户 ID") @PathVariable("id") Long userId)
    {
        com.ruoyi.iam.entity.IamUser user = authService.getUserById(userId);
        if (user == null)
        {
            return R.ok(null);
        }
        UserLoginUserDTO dto = new UserLoginUserDTO();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setDisplayName(user.getDisplayName());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setStatus(user.getStatus());
        dto.setDeleteStatus(user.getDeleteStatus());

        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.ruoyi.iam.entity.IamUserChannel> wrapper =
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(com.ruoyi.iam.entity.IamUserChannel::getUserId, userId)
            .eq(com.ruoyi.iam.entity.IamUserChannel::getStatus, "0");
        java.util.List<com.ruoyi.iam.entity.IamUserChannel> channels =
            authService.getChannelList(userId);
        if (channels != null && !channels.isEmpty())
        {
            java.util.List<UserLoginUserDTO.ChannelResponse> channelDtos = channels.stream()
                .map(ch -> {
                    UserLoginUserDTO.ChannelResponse chDto = new UserLoginUserDTO.ChannelResponse();
                    chDto.setId(ch.getId());
                    chDto.setChannelType(ch.getChannelType());
                    chDto.setChannelAccount(ch.getChannelAccount());
                    chDto.setIsPrimary(ch.getIsPrimary());
                    chDto.setBindTime(ch.getBindTime() != null ? ch.getBindTime().toString() : null);
                    chDto.setStatus(ch.getStatus());
                    return chDto;
                })
                .collect(java.util.stream.Collectors.toList());
            dto.setChannels(channelDtos);
        }
        return R.ok(dto);
    }

    private String extractToken(HttpServletRequest request)
    {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer "))
        {
            String token = header.substring(7).trim();
            if (token.isEmpty())
            {
                return null;
            }
            return token;
        }
        return null;
    }
}
