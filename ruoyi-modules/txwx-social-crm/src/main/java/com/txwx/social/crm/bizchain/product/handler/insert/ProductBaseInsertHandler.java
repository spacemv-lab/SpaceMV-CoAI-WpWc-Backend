package com.txwx.social.crm.bizchain.product.handler.insert;

import com.txwx.social.api.domain.dto.SimpleProductDTO;
import com.txwx.social.crm.bizchain.product.context.ProductChainContext;
import com.txwx.social.crm.common.chain.AbstractChainHandler;
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

        Long productId = productService.insertProductReturnID(EntityConvertor.convert2PO(product, true));
        context.setProductId(productId);
    }
}