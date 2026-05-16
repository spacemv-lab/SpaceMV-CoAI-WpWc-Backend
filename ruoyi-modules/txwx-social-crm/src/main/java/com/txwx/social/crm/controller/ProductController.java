package com.txwx.social.crm.controller;

import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.api.client.ProductApiClient;
import com.txwx.social.api.domain.dto.*;
import com.txwx.social.crm.bizchain.product.service.ProductChainService;
import com.txwx.social.crm.common.config.QueryConfig;
import com.txwx.social.crm.domain.query.ProductAddOrUpdateRequest;
import com.txwx.social.crm.domain.query.ProductQueryRequest;
import com.txwx.social.crm.domain.po.TxwxProductPO;
import com.txwx.social.crm.enums.PermissionTypeEnum;
import com.txwx.social.crm.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ruoyi.common.core.web.page.TableSupport.getPageDomain;

/**
 * 产品控制器（实现 ProductApiClient 接口，提供远程调用能力）
 *
 * @author txwx
 * @date 2026-04-04
 */
@RestController
@RequestMapping("/product")
@Tag(name = "03--【CRM】--产品管理")
@RequiredArgsConstructor
@Validated
public class ProductController extends BaseController implements ProductApiClient {

    @Autowired
    private IProductService productService;

    private final ProductChainService productChainService;


    /* ========== 以下是 ProductApiClient 接口的实现 ========== */

    @Override
    @PostMapping("/list/simple")
    @Operation(summary = "查询简单产品列表（client调用）")
    public AjaxResult getSimpleProductList(@Parameter(description = "查询条件") @RequestBody(required = false) SimpleProductDTO query) {
        TxwxProductPO queryPO = null;
        if (query != null) {
            queryPO = new TxwxProductPO();
            BeanUtils.copyProperties(query, queryPO);
            queryPO.setUserId(SecurityUtils.getUserId());
        }

        List<TxwxProductPO> list = productService.selectProductList(queryPO);
        List<SimpleProductDTO> dtoList = list.stream()
                .map(this::convertPoToDto)
                .collect(Collectors.toList());
        return AjaxResult.success(dtoList);
    }

    @Override
    @PostMapping("/list")
    @Operation(summary = "查询产品列表（client调用）")
    public AjaxResult getProductList(@Parameter(description = "查询条件") @RequestBody(required = false) SimpleProductDTO query) {
        AjaxResult result = getSimpleProductList(query);
        if (!result.isSuccess()) {
            return result;
        }
        List<SimpleProductDTO> simpleProductDTOList = (List<SimpleProductDTO>) result.get(AjaxResult.DATA_TAG);
        List<Long> pids = simpleProductDTOList.stream().mapToLong(SimpleProductDTO::getId).boxed().toList();
        List<ProductDTO> fullList = simpleProductDTOList.stream().map(simple -> {
            ProductDTO dto = new ProductDTO();
            BeanUtils.copyProperties(simple, dto);
            return dto;
        }).toList();
        Map<Long, List<ChannelDTO>> p2cMap = productService.getProduct2ChannelMap(pids);
        fullList.forEach(productDTO -> {
            List<ChannelDTO> channelDTOList = p2cMap.getOrDefault(productDTO.getBaseInfo().getId(), Lists.newArrayList());
            productDTO.setChannelDTOList(channelDTOList);
        });
        return AjaxResult.success(fullList);
    }

    /**
     * 获取产品列表
     * @param request 产品查询条件
     * @return 分页结果
     */
    //TODO 角色权限分配
    //@PreAuthorize("@ss.hasPermi('system:product:list')")
    @PostMapping("/management/list")
    @Operation(summary = "查询产品列表")
    public TableDataInfo list(@RequestBody ProductQueryRequest request) {
        // 1. 调用责任链Service
        return productChainService.selectProductList(
                request.getQuery(),
                getPageDomain(),
                request.getQueryConfig()
        );
    }

    /**
     * 新增产品
     * @param request 产品请求
     * @return 产品ID
     */
    //@PreAuthorize("@ss.hasPermi('system:product:add')")
    @PostMapping("/management")
    public AjaxResult add(@RequestBody ProductAddOrUpdateRequest request) {
        List<UserPermissionDTO> userPermissions = Lists.newArrayList();
        UserPermissionDTO userPermissionDTO = new UserPermissionDTO();
        userPermissions.add(userPermissionDTO);
        userPermissionDTO.setRelationType(PermissionTypeEnum.PRODUCT_LEVEL.getCode());
        List<ProductChannelDTO> productChannels = Lists.newArrayList();
        ProductChannelDTO productChannelDTO = new ProductChannelDTO();
        productChannels.add(productChannelDTO);
        productChannelDTO.setChannelId(1L);
        Long productId = productChainService.insertProduct(request.getProductDTO(), userPermissions, productChannels);
        return AjaxResult.success("产品新增成功", productId);
    }

    /**
     * 修改产品
     * @param request 产品主体
     * @return 结果
     */
    //@PreAuthorize("@ss.hasPermi('system:product:edit')")
    @PutMapping("/management")
    public AjaxResult edit(@RequestBody ProductAddOrUpdateRequest request) {
        productChainService.updateProduct(request.getProductDTO(), request.getUserPermissions(), request.getProductChannels());
        return AjaxResult.success("产品修改成功");
    }

    /**
     * 删除产品
     * @param productId 产品ID
     * @return 结果
     */
    //@PreAuthorize("@ss.hasPermi('system:product:remove')")
    @DeleteMapping("/management/{productId}")
    public AjaxResult remove(
            @PathVariable @NotNull(message = "产品ID不能为空") Long productId
    ) {
        productChainService.deleteProduct(productId);
        return AjaxResult.success("产品删除成功");
    }

    @PostMapping("/management/{id}")
    @Operation(summary = "查询产品详情")
    public AjaxResult getProductById(@Parameter(description = "产品ID") @PathVariable("id") Long id, @RequestBody QueryConfig queryConfig) {
        return AjaxResult.success(productChainService.queryProductById(id, queryConfig));
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "查询产品详情（client调用）")
    public AjaxResult getProductById(@Parameter(description = "产品ID") @PathVariable("id") Long id) {
        TxwxProductPO po = productService.selectProductById(id);
        if (po == null) {
            return AjaxResult.error("产品不存在");
        }
        return AjaxResult.success(convertPoToDto(po));
    }

    @Override
    @PostMapping
    @Operation(summary = "新增产品（client调用）")
    public AjaxResult addProduct(@Parameter(description = "产品信息") @RequestBody ProductDTO product) {
        TxwxProductPO po = new TxwxProductPO();
        BeanUtils.copyProperties(product, po);
        fillBaseInfo(po);
        return AjaxResult.success(productService.insertProductReturnID(po));
    }

    @Override
    @PutMapping
    @Operation(summary = "更新产品（client调用）")
    public AjaxResult updateProduct(@Parameter(description = "产品信息") @RequestBody ProductDTO product) {
        TxwxProductPO po = new TxwxProductPO();
        BeanUtils.copyProperties(product, po);
        fillBaseInfo(po);
        return toAjax(productService.updateProduct(po));
    }

    @Override
    @DeleteMapping("/{ids}")
    @Operation(summary = "批量删除产品（client调用）")
    public AjaxResult deleteProductByIds(@Parameter(description = "产品ID列表") @PathVariable("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(Long::valueOf)
                .toList();

        if (idList.isEmpty()) {
            return AjaxResult.error("请提供要删除的产品ID");
        }
        return toAjax(productService.deleteProductByIds(idList));
    }

    /* ========== 内部转换方法 ========== */

    /**
     * 将内部 PO 对象转换为 API DTO 对象
     */
    private SimpleProductDTO convertPoToDto(TxwxProductPO po) {
        if (po == null) {
            return null;
        }
        SimpleProductDTO dto = new SimpleProductDTO();
        BeanUtils.copyProperties(po, dto);
        return dto;
    }

    private void fillBaseInfo(TxwxProductPO po) {
        //TODO 测试用
        String operator = SecurityUtils.getUsername();
        Long userId = SecurityUtils.getUserId();
        po.setCreateBy(operator);
        po.setUpdateBy(operator);
        po.setUserId(userId);
        po.setCreateTime(new Date());
        po.setUpdateTime(new Date());
    }
}
