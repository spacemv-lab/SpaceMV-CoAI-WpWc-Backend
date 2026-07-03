/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.system.api.factory;

import com.ruoyi.common.core.web.domain.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.api.RemoteUserService;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.api.model.LoginUser;

/**
 * 用户服务降级处理
 * 
 * @author ruoyi
 */
@Component
public class RemoteUserFallbackFactory implements FallbackFactory<RemoteUserService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteUserFallbackFactory.class);

    @Override
    public RemoteUserService create(Throwable throwable)
    {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new RemoteUserService()
        {
            @Override
            public R<LoginUser> getUserInfo(String username, String source)
            {
                return R.fail("获取用户失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> registerUserInfo(SysUser sysUser, String source)
            {
                return R.fail("注册用户失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> recordUserLogin(SysUser sysUser, String source)
            {
                return R.fail("记录用户登录信息失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> remoteResetPwd(SysUser user, String source) {
                return R.fail("修改用户密码失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> checkUnique(String accountName, String source)
            {
                return R.fail("校验登录账号唯一性失败：" + throwable.getMessage());
            }

            @Override
            public R<Long> syncIamUser(SysUser sysUser, String source)
            {
                log.warn("IAM 用户同步降级: userName={}", sysUser.getUserName());
                return R.fail("IAM 用户同步失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> deleteIamUser(Long productUserId, String source)
            {
                log.warn("IAM 用户删除同步降级: productUserId={}", productUserId);
                return R.fail("IAM 用户删除同步失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> updateProfile(SysUser sysUser, String source)
            {
                log.warn("IAM 用户资料同步降级: userId={}", sysUser.getUserId());
                return R.fail("IAM 用户资料同步失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> syncIamUserPassword(SysUser sysUser, String source)
            {
                log.warn("IAM 密码同步降级: userId={}", sysUser.getUserId());
                return R.fail("IAM 密码同步失败:" + throwable.getMessage());
            }
        };
    }
}
