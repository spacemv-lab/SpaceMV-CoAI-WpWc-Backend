/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.system.api;

import com.ruoyi.common.core.web.domain.AjaxResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.api.factory.RemoteUserFallbackFactory;
import com.ruoyi.system.api.model.LoginUser;

/**
 * 用户服务
 * 
 * @author ruoyi
 */
@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemoteUserService
{
    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @param source 请求来源
     * @return 结果
     */
    @GetMapping("/user/info/{username}")
    public R<LoginUser> getUserInfo(@PathVariable("username") String username, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 注册用户信息
     *
     * @param sysUser 用户信息
     * @param source 请求来源
     * @return 结果
     */
    @PostMapping("/user/register")
    public R<Boolean> registerUserInfo(@RequestBody SysUser sysUser, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 记录用户登录IP地址和登录时间
     *
     * @param sysUser 用户信息
     * @param source 请求来源
     * @return 结果
     */
    @PutMapping("/user/recordlogin")
    public R<Boolean> recordUserLogin(@RequestBody SysUser sysUser, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @PutMapping("/user/remote/resetPwd")
    public R<Boolean> remoteResetPwd(@RequestBody SysUser user, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @GetMapping("/user/checkUnique")
    public R<Boolean> checkUnique(@RequestParam("accountName") String accountName, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * IAM 注册同步 — 将 IAM 用户同步写入 sys_user（无密码处理）
     * <p>
     * IAM 负责认证，system 只需管理后台兼容记录。
     * sys_user.userId 由 system 模块自增生成，通过返回值传递。
     * IAM 端需将返回值写入 iam_user_product 映射表维护关联关系。
     *
     * @param sysUser 用户信息（注意：userId 不设置，由 system 自增生成）
     * @param source 请求来源
     * @return R<Long> data 为 system 模块自增产生的 sys_user.userId
     */
    @PostMapping("/user/inner/syncIamUser")
    public R<Long> syncIamUser(@RequestBody SysUser sysUser, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * IAM 用户注销同步 — 删除 sys_user
     * <p>
     * IAM 模块用户注销冷静期到期后，通过此接口删除对应的 sys_user 记录。
     *
     * @param productUserId sys_user.userId（来自 iam_user_product 映射表）
     * @param source 请求来源
     * @return R<Boolean> 删除结果
     */
    @DeleteMapping("/user/inner/deleteIamUser/{productUserId}")
    public R<Boolean> deleteIamUser(@PathVariable("productUserId") Long productUserId,
                                    @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * IAM 通道绑定/解绑同步 — 更新 sys_user 手机号/邮箱
     * <p>
     * IAM 用户绑定/解绑 phone/email 通道时，同步更新 sys_user 对应字段。
     *
     * @param sysUser 包含 userId 和需要更新的字段（phonenumber / email）
     * @param source 请求来源
     * @return R<Boolean> 更新结果
     */
    @PutMapping("/user/inner/updateProfile")
    public R<Boolean> updateProfile(@RequestBody SysUser sysUser,
                                    @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * IAM 密码变更同步 — 同步更新 sys_user.password
     * <p>
     * IAM 用户修改密码后，将已 BCrypt 加密的密码同步写入 sys_user。
     * 密码已是加密后的密文，无需再次加密。
     *
     * @param sysUser 包含 userId 和 password（BCrypt 密文）
     * @param source 请求来源
     * @return R<Boolean> 更新结果
     */
    @PutMapping("/user/inner/syncIamUserPassword")
    public R<Boolean> syncIamUserPassword(@RequestBody SysUser sysUser,
                                          @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
