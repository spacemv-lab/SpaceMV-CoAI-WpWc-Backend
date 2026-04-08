package com.txwx.social.dashboard.remote;

import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.social.api.client.AccountApiClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * 账号远程调用服务（代理到 API 模块）
 *
 * @author txwx
 * @date 2026-04-04
 */
@Component
@FeignClient(name = "txwx-social-crm", path = "/account")
public interface AccountRemoteService extends AccountApiClient {
}
