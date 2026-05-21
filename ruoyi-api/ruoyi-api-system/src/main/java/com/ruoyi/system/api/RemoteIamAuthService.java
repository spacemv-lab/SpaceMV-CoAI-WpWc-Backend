package com.ruoyi.system.api;

import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * IAM 认证服务 Feign 接口
 *
 * @author txwx
 */
@FeignClient(name = ServiceNameConstants.IAM_SERVICE,
        contextId = "remoteIamAuthService",
        value = ServiceNameConstants.IAM_SERVICE)
public interface RemoteIamAuthService
{
    /**
     * 验证 token 有效性
     */
    @PostMapping("/auth/v1/inner/user/validate")
    R<Boolean> validateToken(@RequestHeader("Authorization") String token);

    /**
     * 按 ID 查询用户
     */
    @GetMapping("/auth/v1/inner/user/{id}")
    R<?> getUserById(@PathVariable("id") Long userId);
}
