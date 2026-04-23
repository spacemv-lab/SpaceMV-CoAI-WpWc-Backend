package com.ruoyi.system.api;

import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.api.domain.TxUser;
import com.ruoyi.system.api.model.LoginUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 用户服务
 * 
 * @author ruoyi
 */
@FeignClient(contextId = "remoteTxwxUserService", value = ServiceNameConstants.TX_SOCIAL_CRM_SERVICE)
public interface RemoteTxwxUserService
{

    /**
     * 通过用户名查询用户信息
     *
     * @param accountName 用户名/手机号/邮箱
     * @return 结果
     */
    @GetMapping("/user/info/{accountName}")
    public R<LoginUser> getUserInfo(@PathVariable("accountName") String accountName);

    /**
     * 注册用户信息
     * @param sysUser 用户信息
     * @return 结果
     */
    @PostMapping("/user/register")
    public R<Boolean> registerUserInfo(@RequestBody TxUser sysUser);

}
