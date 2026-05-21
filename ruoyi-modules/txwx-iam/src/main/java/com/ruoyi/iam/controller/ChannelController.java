package com.ruoyi.iam.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.iam.annotation.CaptchaValidate;
import com.ruoyi.iam.dto.ChannelBindRequest;
import com.ruoyi.iam.dto.ChannelResponse;
import com.ruoyi.iam.dto.ChannelUnbindRequest;
import com.ruoyi.iam.dto.response.BackupContactResponse;
import com.ruoyi.iam.filter.IamTokenValidationFilter;
import com.ruoyi.iam.service.AuthService;
import com.ruoyi.iam.service.UserChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 通道管理
 * <p>
 * 注意：不走 tokenService.parseToken() 手工解析 token，
 * 而是直接读取 {@link IamTokenValidationFilter} 已设置好的 {@code iam_user_id} 请求属性，
 * 避免 RuoYi 硬编码密钥 vs IAM Nacos 密钥不一致导致的 SignatureException。
 *
 * @author txwx
 */
@RestController
@RequestMapping("/auth/v1/user/channel")
@Tag(name = "02--【IAM】--通道管理")
public class ChannelController
{
    private final UserChannelService channelService;
    private final AuthService authService;

    public ChannelController(UserChannelService channelService, AuthService authService)
    {
        this.channelService = channelService;
        this.authService = authService;
    }

    /**
     * 绑定通道
     */
    @PostMapping
    @Operation(summary = "绑定通道")
    public R<String> bindChannel(HttpServletRequest request, @Parameter(description = "绑定请求") @RequestBody ChannelBindRequest req)
    {
        // 参数校验
        if (req.getChannelAccount() == null || req.getChannelAccount().isEmpty())
        {
            throw new ServiceException("缺少通道账号");
        }

        Long userId = (Long) request.getAttribute(IamTokenValidationFilter.IAM_USER_ID_ATTR);
        if (userId == null)
        {
            throw new IllegalArgumentException("Missing iam_user_id attribute — filter may not have run");
        }

        channelService.bindChannel(userId, req.getChannelType(), req.getChannelAccount(), req.getVerifyCode());
        return R.ok("绑定成功");
    }

    /**
     * 解绑通道（v1.1: email 双验证）
     */
    @DeleteMapping
    @Operation(summary = "解绑通道")
    public R<String> unbindChannel(HttpServletRequest request, @Parameter(description = "解绑请求") @RequestBody ChannelUnbindRequest req)
    {
        Long userId = (Long) request.getAttribute(IamTokenValidationFilter.IAM_USER_ID_ATTR);
        if (userId == null)
        {
            throw new IllegalArgumentException("Missing iam_user_id attribute — filter may not have run");
        }

        // v1.1: email 解绑需要密码校验
        if ("email".equals(req.getChannelType()) && req.getPassword() != null)
        {
            authService.validateEmailUnbindPassword(userId, req.getPassword());
        }

        channelService.unbindChannel(userId, req.getChannelType(), req.getVerifyCode(), req.getPassword());
        return R.ok("解绑成功");
    }

    /**
     * 查询通道列表
     */
    @GetMapping
    @Operation(summary = "查询通道列表")
    public R<List<ChannelResponse>> listChannels(HttpServletRequest request)
    {
        Long userId = (Long) request.getAttribute(IamTokenValidationFilter.IAM_USER_ID_ATTR);
        if (userId == null)
        {
            throw new IllegalArgumentException("Missing iam_user_id attribute — filter may not have run");
        }

        List<ChannelResponse> channels = channelService.listChannels(userId);
        return R.ok(channels);
    }

    /**
     * 查询备用联系方式（v1.1 新增）
     */
    @GetMapping("/backup")
    @Operation(summary = "查询备用联系方式")
    public R<BackupContactResponse> getBackupContact(HttpServletRequest request)
    {
        Long userId = (Long) request.getAttribute(IamTokenValidationFilter.IAM_USER_ID_ATTR);
        if (userId == null)
        {
            throw new IllegalArgumentException("Missing iam_user_id attribute — filter may not have run");
        }

        BackupContactResponse resp = channelService.queryBackupContact(userId);
        return R.ok(resp);
    }
}
