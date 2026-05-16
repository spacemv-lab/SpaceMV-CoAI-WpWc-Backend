package com.txwx.social.crm.bizchain.product.handler.delete;

import com.txwx.social.crm.bizchain.product.context.ProductChainContext;
import com.txwx.social.crm.common.chain.AbstractChainHandler;
import com.txwx.social.crm.mapper.TxwxProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductBaseDeleteHandler extends AbstractChainHandler<ProductChainContext> {
    @Autowired
    private TxwxProductMapper productMapper;

    @Override
    protected void doHandle(ProductChainContext context) {
        Long productId = context.getProductId();
        if (productId == null) {
            context.interruptWithError("产品ID不能为空");
            return;
        }
        productMapper.deleteProductByIds(List.of(productId));
    }
}
