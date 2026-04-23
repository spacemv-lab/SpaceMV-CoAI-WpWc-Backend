package com.txwx.social.crm.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.crm.domain.po.TxwxUserLogoutPO;
import com.txwx.social.crm.domain.po.TxwxUserRegisterPO;
import com.txwx.social.crm.service.ITxwxUserLogoutService;
import com.txwx.social.crm.service.ITxwxUserRegisterService;
import com.txwx.social.crm.service.ITxwxVerifyCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * 账号注销控制器
 *
 * @author txwx
 * @date 2026-04-20
 */
@RestController
@RequestMapping("/user/logout")
@RequiredArgsConstructor
@Validated
@Tag(name = "账号注销")
@Slf4j
public class UserLogoutController extends BaseController {

    @Autowired
    private ITxwxVerifyCodeService verifyCodeService;

    @Autowired
    private ITxwxUserLogoutService userLogoutService;

    @Autowired
    private ITxwxUserRegisterService userRegisterService;

    /**
     * 提交注销申请
     *
     * @param request 注销请求
     * @return 结果
     */
    @PostMapping("/apply")
    @Operation(summary = "提交注销申请")
    public R<Map<String, Object>> applyLogout(@Valid @RequestBody ApplyRequest request) {
        // 1. 获取当前用户ID
        String username = SecurityUtils.getUsername();
        if (StringUtils.isEmpty(username)) {
            return R.fail("未登录");
        }

        Long userId = SecurityUtils.getUserId();

        // 2. 检查是否已存在冷却中的注销申请
        TxwxUserLogoutPO existing = userLogoutService.selectByUserId(userId);
        if (existing != null && "0".equals(existing.getStatus())) {
            return R.fail("已有冷却中的注销申请，请勿重复提交");
        }

        // 3. 验证验证码
        boolean verified = verifyCodeService.verifyCode(request.getAccount(), request.getVerifyCode());
        if (!verified) {
            return R.fail("验证码错误或已过期");
        }

        // 4. 创建注销申请（7天冷却期）
        TxwxUserLogoutPO logout = new TxwxUserLogoutPO();
        logout.setUserId(userId);
        logout.setApplyTime(System.currentTimeMillis());
        logout.setCoolEndTime(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000); // 7天后
        logout.setStatus("0"); // 冷却中
        logout.setCreateBy(username);
        logout.setUpdateBy(username);

        userLogoutService.save(logout);

        // 5. 更新注册用户状态为注销中
        TxwxUserRegisterPO register = userRegisterService.selectByUserId(userId);
        if (register != null) {
            register.setStatus("2"); // 注销中
            register.setUpdateBy(username);
            userRegisterService.updateById(register);
        }

        log.info("注销申请提交成功: userId={}, logoutId={}", userId, logout.getLogoutId());

        Map<String, Object> result = new HashMap<>();
        result.put("logoutId", logout.getLogoutId());
        result.put("coolEndTime", logout.getCoolEndTime());
        result.put("status", logout.getStatus());

        return R.ok(result);
    }

    /**
     * 撤销注销申请
     *
     * @param request 撤销请求
     * @return 结果
     */
    @PutMapping("/cancel")
    @Operation(summary = "撤销注销申请")
    public AjaxResult cancelLogout(@Valid @RequestBody CancelRequest request) {
        // 1. 获取当前用户ID
        String username = SecurityUtils.getUsername();
        if (StringUtils.isEmpty(username)) {
            return AjaxResult.error("未登录");
        }
        Long userId = SecurityUtils.getUserId();

        // 2. 查询注销申请
        TxwxUserLogoutPO logout = userLogoutService.selectByUserId(userId);
        if (logout == null) {
            return AjaxResult.error("注销申请不存在");
        }

        if (!"0".equals(logout.getStatus())) {
            return AjaxResult.error("只有冷却中的申请可撤销");
        }

        // 3. 更新注销申请状态为已撤销
        logout.setStatus("2"); // 已撤销
        logout.setUpdateBy(username);
        userLogoutService.updateById(logout);

        // 4. 更新注册用户状态为正常
        TxwxUserRegisterPO register = userRegisterService.selectByUserId(userId);
        if (register != null) {
            register.setStatus("0"); // 正常
            register.setUpdateBy(username);
            userRegisterService.updateById(register);
        }

        log.info("注销申请撤销成功: userId={}, logoutId={}", userId, logout.getLogoutId());

        return AjaxResult.success("注销申请撤销成功");
    }

    /**
     * 查询注销申请状态
     *
     * @param userId 用户ID
     * @return 结果
     */
    @GetMapping("/status")
    @Operation(summary = "查询注销申请状态")
    public R<Map<String, Object>> getLogoutStatus(@NotNull(message = "用户ID不能为空") @RequestParam Long userId) {
        // 1. 查询注销申请
        TxwxUserLogoutPO logout = userLogoutService.selectByUserId(userId);
        if (logout == null) {
            return R.ok(null);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("logoutId", logout.getLogoutId());
        result.put("applyTime", logout.getApplyTime());
        result.put("coolEndTime", logout.getCoolEndTime());
        result.put("status", logout.getStatus());
        result.put("statusDesc", getStatusDesc(logout.getStatus()));

        return R.ok(result);
    }

    /**
     * 获取状态描述
     */
    private String getStatusDesc(String status) {
        switch (status) {
            case "0":
                return "冷却中";
            case "1":
                return "已注销";
            case "2":
                return "已撤销";
            default:
                return "未知";
        }
    }

    /**
     * 注销申请请求
     */
    @Data
    public static class ApplyRequest {
        @NotNull(message = "用户ID不能为空")
        private Long userId;

        @NotBlank(message = "账号不能为空")
        private String account;

        @NotBlank(message = "验证码不能为空")
        private String verifyCode;
    }

    /**
     * 撤销注销申请请求
     */
    @Data
    public static class CancelRequest {
        @NotNull(message = "注销ID不能为空")
        private Long logoutId;

        @NotNull(message = "用户ID不能为空")
        private Long userId;
    }
}
