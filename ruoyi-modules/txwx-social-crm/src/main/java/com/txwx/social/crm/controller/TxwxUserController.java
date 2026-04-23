package com.txwx.social.crm.controller;

import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.bean.BeanUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.api.RemoteUserService;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.api.domain.TxUser;
import com.ruoyi.system.api.model.LoginUser;
import com.txwx.social.crm.domain.po.TxwxUserRegisterPO;
import com.txwx.social.crm.service.ITxwxUserRegisterService;
import com.txwx.social.crm.service.ITxwxVerifyCodeService;
import com.txwx.social.crm.util.AccountUtil;
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

/**
 * 用户注册控制器
 *
 * @author txwx
 * @date 2026-04-20
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
@Tag(name = "用户注册")
@Slf4j
public class TxwxUserController extends BaseController {

    @Autowired
    private ITxwxVerifyCodeService verifyCodeService;

    @Autowired
    private ITxwxUserRegisterService userRegisterService;

    @Autowired
    private RemoteUserService remoteUserService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info/{accoutName}")
    public R<LoginUser> info(@PathVariable("accountName") String accountName)
    {
        String username = processUserName(accountName);
        if (StringUtils.isNull(username))
        {
            return R.fail("用户名或密码错误");
        }

        return remoteUserService.getUserInfo(username, SecurityConstants.INNER);
    }

    private String processUserName(String accountName) {
        if (AccountUtil.getAccountType(accountName) == 0) {
            return accountName;
        }
        TxwxUserRegisterPO registerPO = userRegisterService.selectByRegisterAccount(accountName);
        return registerPO.getUserName();
    }


    /**
     * 检查用户名是否已存在
     *
     * @param username 注册用户名
     * @return 结果
     */
    @GetMapping("/checkunique")
    @Operation(summary = "查询单个用户名唯一性")
    public R<Boolean> checkUserNameUnique(@NotNull(message = "用户名称不能为空") @RequestParam("username") String username) {
        TxwxUserRegisterPO register = userRegisterService.selectByUserName(username);
        if (register != null) {
            return R.ok(true);
        }
        R<LoginUser> queryUserRes = remoteUserService.getUserInfo(username, SecurityConstants.INNER);
        if (queryUserRes == null || !Constants.SUCCESS.equals(queryUserRes.getCode())) {
            return R.fail("用户服务不可用，请稍后再试");
        }
        LoginUser loginUser = queryUserRes.getData();
        return R.ok(loginUser == null);
    }

    /**
     * 用户注册（手机号/邮箱双模式）
     * @param request 注册请求
     * @return 结果
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public R<Long> register(@RequestBody TxUser request) {
        String phoneVerifyCode = request.getPhoneVerifyCode();
        String emailVerifyCode = request.getEmailVerifyCode();
        Integer registerType = 0;// 0-手机 1-邮箱
        String verifyAccount = request.getTxPhonenumber();
        if (!StringUtils.hasText(phoneVerifyCode) && !StringUtils.hasText(emailVerifyCode)) {
            return R.fail("未正确传入验证码");
        }
        String verifyCode = phoneVerifyCode;
        if (!StringUtils.hasText(verifyCode)) {
            verifyCode = emailVerifyCode;
            registerType = 1;
            verifyAccount = request.getTxEmail();
        }

        if (!StringUtils.hasText(verifyAccount)) {
            return R.fail("未正确传入有效账号");
        }
        // 1. 校验验证码
        boolean verified = verifyCodeService.verifyCode(verifyAccount, verifyCode);
        if (!verified) {
            return R.fail("验证码错误或已过期");
        }

        // 2. 检查用户名是否已存在
        TxwxUserRegisterPO existing = userRegisterService.selectByUserName(request.getUserName());
        if (existing != null) {
            return R.fail("账号已注册");
        }

        SysUser sysUser = getSysUser(request, registerType);

        // 这里需要写成INNER，不然会查不到
        R<Boolean> registerResult = remoteUserService.registerUserInfo(sysUser, SecurityConstants.INNER);
        if (registerResult == null || !R.isSuccess(registerResult)) {
            return R.fail("用户创建失败");
        }

        // 5. 查询用户ID
        R<LoginUser> userIdResult = remoteUserService.getUserInfo(request.getUserName(), SecurityConstants.INNER);
        if (userIdResult == null || !R.isSuccess(userIdResult)) {
            return R.fail("查询用户ID失败");
        }
        Long userId = userIdResult.getData().getUserid();

        // 6. 创建注册记录
        TxwxUserRegisterPO register = new TxwxUserRegisterPO();
        register.setUserId(userId);
        register.setUserName(request.getUserName());
        if (registerType == 0) {
            register.setBindPhone(request.getPhonenumber());
        }
        if (registerType == 1) {
            register.setBindEmail(request.getEmail());
        }
        register.setBakPhone(request.getPhonenumber());
        register.setBakEmail(register.getBindEmail());
        register.setRegisterTime(System.currentTimeMillis());
        register.setStatus("0");
        register.setCreateBy(request.getUserName());
        register.setUpdateBy(request.getUserName());

        userRegisterService.save(register);

        log.info("用户注册成功: userId={}, userName={}, registerAccount={}", userId, request.getUserName(), verifyAccount);

        return R.ok(userId);
    }

    private static SysUser getSysUser(TxUser request, Integer registerType) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyBeanProp(request, sysUser);
        if (registerType == 0) {
            sysUser.setPhonenumber(request.getTxPhonenumber());
        }
        if (registerType == 1) {
            sysUser.setEmail(request.getTxEmail());
        }
        return sysUser;
    }

    /**
     * 修改手机号
     *
     * @param request 修改请求
     * @return 结果
     */
    @PutMapping("/profile/modify/phone")
    @Operation(summary = "修改手机号")
    public AjaxResult modifyPhone(@Valid @RequestBody ModifyPhoneRequest request) {
        // 1. 获取当前用户ID
        String username = SecurityUtils.getUsername();
        if (StringUtils.isEmpty(username)) {
            return AjaxResult.error("未登录");
        }

        Long userId = SecurityUtils.getUserId();

        // 2. 查询注册信息
        TxwxUserRegisterPO register = userRegisterService.selectByUserId(userId);
        if (register == null) {
            return AjaxResult.error("用户注册信息不存在");
        }

        // 3. 验证原手机号验证码
        boolean verified = verifyCodeService.verifyCode(request.getOldPhone(), request.getOldVerifyCode());
        if (!verified) {
            return AjaxResult.error("原手机号验证码错误");
        }

        // 4. 验证新手机号验证码
        verified = verifyCodeService.verifyCode(request.getNewPhone(), request.getNewVerifyCode());
        if (!verified) {
            return AjaxResult.error("新手机号验证码错误");
        }

        // 5. 更新注册信息
        register.setBindPhone(request.getNewPhone());
        register.setUpdateBy(username);
        userRegisterService.updateById(register);

        log.info("手机号修改成功: userId={}, newPhone={}", userId, request.getNewPhone());

        return AjaxResult.success("手机号修改成功");
    }

    /**
     * 修改邮箱
     *
     * @param request 修改请求
     * @return 结果
     */
    @PutMapping("/profile/modify/email")
    @Operation(summary = "修改邮箱")
    public AjaxResult modifyEmail(@Valid @RequestBody ModifyEmailRequest request) {
        // 1. 获取当前用户ID
        String username = SecurityUtils.getUsername();
        if (StringUtils.isEmpty(username)) {
            return AjaxResult.error("未登录");
        }

        Long userId = SecurityUtils.getUserId();

        // 2. 查询注册信息
        TxwxUserRegisterPO register = userRegisterService.selectByUserId(userId);
        if (register == null) {
            return AjaxResult.error("用户注册信息不存在");
        }

        // 3. 验证原邮箱验证码
        boolean verified = verifyCodeService.verifyCode(request.getOldEmail(), request.getOldVerifyCode());
        if (!verified) {
            return AjaxResult.error("原邮箱验证码错误");
        }

        // 4. 验证新邮箱验证码
        verified = verifyCodeService.verifyCode(request.getNewEmail(), request.getNewVerifyCode());
        if (!verified) {
            return AjaxResult.error("新邮箱验证码错误");
        }

        // 5. 更新注册信息
        register.setBindEmail(request.getNewEmail());
        register.setUpdateBy(username);
        userRegisterService.updateById(register);

        log.info("邮箱修改成功: userId={}, newEmail={}", userId, request.getNewEmail());

        return AjaxResult.success("邮箱修改成功");
    }

    /**
     * 绑定备用联系方式
     *
     * @param request 绑定请求
     * @return 结果
     */
    @PutMapping("/profile/bind/backup")
    @Operation(summary = "绑定备用联系方式")
    public AjaxResult bindBackup(@Valid @RequestBody BindBackupRequest request) {
        // 1. 获取当前用户ID
        String username = SecurityUtils.getUsername();
        if (StringUtils.isEmpty(username)) {
            return AjaxResult.error("未登录");
        }

        Long userId = SecurityUtils.getUserId();

        // 2. 查询注册信息
        TxwxUserRegisterPO register = userRegisterService.selectByUserId(userId);
        if (register == null) {
            return AjaxResult.error("用户注册信息不存在");
        }

        // 3. 验证码校验（绑定手机号或邮箱都需要验证码）
        String account = StringUtils.isNotEmpty(request.getBindPhone()) ? request.getBindPhone() : request.getBindEmail();
        boolean verified = verifyCodeService.verifyCode(account, request.getVerifyCode());
        if (!verified) {
            return AjaxResult.error("验证码错误");
        }

        // 4. 更新注册信息
        if (StringUtils.isNotEmpty(request.getBindPhone())) {
            register.setBindPhone(request.getBindPhone());
        }
        if (StringUtils.isNotEmpty(request.getBindEmail())) {
            register.setBindEmail(request.getBindEmail());
        }
        register.setUpdateBy(username);
        userRegisterService.updateById(register);

        log.info("备用联系方式绑定成功: userId={}", userId);

        return AjaxResult.success("备用联系方式绑定成功");
    }

    // ==================== 请求类 ====================

    /**
     * 修改手机号请求
     */
    @Data
    public static class ModifyPhoneRequest {
        @NotBlank(message = "原手机号不能为空")
        private String oldPhone;

        @NotBlank(message = "原手机号验证码不能为空")
        private String oldVerifyCode;

        @NotBlank(message = "新手机号不能为空")
        private String newPhone;

        @NotBlank(message = "新手机号验证码不能为空")
        private String newVerifyCode;
    }

    /**
     * 修改邮箱请求
     */
    @Data
    public static class ModifyEmailRequest {
        @NotBlank(message = "原邮箱不能为空")
        private String oldEmail;

        @NotBlank(message = "原邮箱验证码不能为空")
        private String oldVerifyCode;

        @NotBlank(message = "新邮箱不能为空")
        private String newEmail;

        @NotBlank(message = "新邮箱验证码不能为空")
        private String newVerifyCode;
    }

    /**
     * 绑定备用联系方式请求
     */
    @Data
    public static class BindBackupRequest {
        private String bindPhone;
        private String bindEmail;
        @NotBlank(message = "验证码不能为空")
        private String verifyCode;
    }
}
