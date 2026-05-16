/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.bizchain.product.handler.insert;

import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.api.domain.dto.SimpleProductDTO;
import com.txwx.social.crm.bizchain.product.context.ProductChainContext;
import com.txwx.social.crm.common.chain.AbstractChainHandler;
import com.txwx.social.crm.domain.po.TxwxProductPO;
import com.txwx.social.crm.mapper.TxwxProductMapper;
import com.txwx.social.crm.service.IProductService;
import com.txwx.social.crm.util.EntityConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductBaseInsertHandler extends AbstractChainHandler<ProductChainContext> {
    @Autowired
    private TxwxProductMapper productMapper;
    @Autowired
    private IProductService productService;

    @Override
    protected void doHandle(ProductChainContext context) {
        SimpleProductDTO product= context.getSimpleProductInfo();
        if (product == null) {
            context.interruptWithError("产品信息不能为空");
            return;
        }

        TxwxProductPO po = EntityConvertor.convert2PO(product, true);
        po.setUserId(SecurityUtils.getUserId());
        Long productId = productService.insertProductReturnID(po);
        context.setProductId(productId);
    }
}