package com.txwx.social.dashboard.remote;

import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.social.api.client.ProductApiClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * 产品远程调用服务（代理到 API 模块）
 *
 * @author txwx
 * @date 2026-04-04
 */
@Component
@FeignClient(name = "txwx-social-crm", path = "/product")
public interface ProductRemoteService extends ProductApiClient {
}
