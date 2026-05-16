/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.bizchain.product.service;

import com.ruoyi.common.core.web.page.PageDomain;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.txwx.social.api.domain.dto.ProductChannelDTO;
import com.txwx.social.api.domain.dto.ProductDTO;
import com.txwx.social.api.domain.dto.SimpleProductDTO;
import com.txwx.social.api.domain.dto.UserPermissionDTO;
import com.txwx.social.crm.bizchain.product.manager.ProductChainManager;
import com.txwx.social.crm.common.config.QueryConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductChainService {
    private final ProductChainManager productChainManager;

    public TableDataInfo selectProductList(SimpleProductDTO query, PageDomain pageDomain, QueryConfig queryConfig) {
        return productChainManager.queryProductList(query, pageDomain, queryConfig);
    }

    public Long insertProduct(SimpleProductDTO product,
                              List<UserPermissionDTO> userPermissions,
                              List<ProductChannelDTO> productChannels) {
        return productChainManager.insertProduct(product, userPermissions, productChannels);
    }

    public void updateProduct(SimpleProductDTO product,
                              List<UserPermissionDTO> userPermissions,
                              List<ProductChannelDTO> productChannels) {
        productChainManager.updateProduct(product, userPermissions, productChannels);
    }

    public void deleteProduct(Long productId) {
        productChainManager.deleteProduct(productId);
    }

    public Long insertProductOnly(SimpleProductDTO product) {
        return productChainManager.insertProductOnly(product);
    }

    public ProductDTO queryProductById(Long productId, QueryConfig queryConfig) {
        SimpleProductDTO simpleProductDTO = new SimpleProductDTO();
        simpleProductDTO.setId(productId);
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNum(1);
        pageDomain.setPageSize(1);
        TableDataInfo dataInfo = productChainManager.queryProductList(simpleProductDTO, pageDomain, queryConfig);
        if (dataInfo == null || CollectionUtils.isEmpty(dataInfo.getRows())) {
            return null;
        }
        return (ProductDTO) dataInfo.getRows().get(0);
    }
}
