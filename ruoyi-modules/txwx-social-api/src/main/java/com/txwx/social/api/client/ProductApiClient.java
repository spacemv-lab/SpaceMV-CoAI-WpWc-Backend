package com.txwx.social.api.client;

import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.social.api.domain.dto.ProductDTO;
import com.txwx.social.api.domain.dto.SimpleProductDTO;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产品服务远程调用客户端接口
 *
 * @author txwx
 * @date 2026-04-04
 */
@FeignClient(
    name = "txwx-social-crm",
    contextId = "productApiClient"
)
public interface ProductApiClient {

    /**
     * 查询产品列表
     */
    @PostMapping("/list/simple")
    AjaxResult getSimpleProductList(@RequestBody(required = false) SimpleProductDTO query);

    @PostMapping("/list")
    AjaxResult getProductList(@RequestBody(required = false) SimpleProductDTO query);

    /**
     * 获取产品详细信息
     */
    @GetMapping("/{id}")
    AjaxResult getProductById(@PathVariable("id") Long id);

    /**
     * 新增产品
     */
    @PostMapping
    AjaxResult addProduct(@RequestBody ProductDTO product);

    /**
     * 修改产品
     */
    @PutMapping
    AjaxResult updateProduct(@RequestBody ProductDTO product);

    /**
     * 删除产品
     */
    @DeleteMapping("/{ids}")
    AjaxResult deleteProductByIds(@PathVariable("ids") String ids);
}
