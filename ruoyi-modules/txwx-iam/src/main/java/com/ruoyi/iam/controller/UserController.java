package com.ruoyi.iam.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.iam.annotation.CaptchaValidate;
import com.ruoyi.iam.dto.PasswordChangeRequest;
import com.ruoyi.iam.dto.ProfileUpdateRequest;
import com.ruoyi.iam.dto.UserLoginUserDTO;
import com.ruoyi.iam.filter.IamTokenValidationFilter;
import com.ruoyi.iam.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户信息管理
 * <p>
 * 注意：不走 tokenService.parseToken() 手工解析 token，
 * 而是直接读取 {@link IamTokenValidationFilter} 已设置好的 {@code iam_user_id} 请求属性，
 * 避免 RuoYi 硬编码密钥 vs IAM Nacos 密钥不一致导致的 SignatureException。
 *
 * @author txwx
 */
@RestController
@RequestMapping("/auth/v1/user")
@Tag(name = "03--【IAM】--用户管理")
public class UserController
{
    private final AuthService authService;

    public UserController(AuthService authService)
    {
        this.authService = authService;
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息")
    public R<UserLoginUserDTO> getMe(HttpServletRequest request)
    {
        Long userId = (Long) request.getAttribute(IamTokenValidationFilter.IAM_USER_ID_ATTR);
        if (userId == null)
        {
            throw new IllegalArgumentException("Missing iam_user_id attribute — filter may not have run");
        }
        UserLoginUserDTO user = authService.getLoginUserById(userId);
        return R.ok(user);
    }

    /**
     * 更新资料
     */
    @PutMapping("/profile")
    @Operation(summary = "更新用户资料")
    public R<Void> updateProfile(HttpServletRequest request, @Parameter(description = "资料更新请求") @RequestBody ProfileUpdateRequest req)
    {
        Long userId = (Long) request.getAttribute(IamTokenValidationFilter.IAM_USER_ID_ATTR);
        if (userId == null)
        {
            throw new IllegalArgumentException("Missing iam_user_id attribute — filter may not have run");
        }
        authService.updateProfile(userId, req);
        return R.ok();
    }

    /**
     * 重置密码
     */
    @CaptchaValidate
    @PutMapping("/password")
    @Operation(summary = "修改密码")
    public R<Void> updatePassword(HttpServletRequest request, @Parameter(description = "密码修改请求") @RequestBody PasswordChangeRequest req)
    {
        Long userId = (Long) request.getAttribute(IamTokenValidationFilter.IAM_USER_ID_ATTR);
        if (userId == null)
        {
            throw new IllegalArgumentException("Missing iam_user_id attribute — filter may not have run");
        }
        try {
            authService.updatePassword(userId, req);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return R.ok();
    }
}
