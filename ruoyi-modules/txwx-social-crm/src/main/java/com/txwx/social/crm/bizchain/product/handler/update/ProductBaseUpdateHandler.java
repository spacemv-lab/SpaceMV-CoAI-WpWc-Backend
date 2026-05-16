package com.txwx.social.crm.bizchain.product.handler.update;

import com.txwx.social.api.domain.dto.SimpleChannelDTO;
import com.txwx.social.api.domain.dto.SimpleProductDTO;
import com.txwx.social.crm.bizchain.product.context.ProductChainContext;
import com.txwx.social.crm.common.chain.AbstractChainHandler;
import com.txwx.social.crm.mapper.TxwxProductMapper;
import com.txwx.social.crm.util.EntityConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductBaseUpdateHandler extends AbstractChainHandler<ProductChainContext> {
    @Autowired
    private TxwxProductMapper productMapper;

    @Override
    protected void doHandle(ProductChainContext context) {
        SimpleProductDTO product = context.getSimpleProductInfo();
        if (product == null || product.getId() == null) {
            context.interruptWithError("产品ID不能为空");
            return;
        }

        // 检查产品是否存在
        if (productMapper.selectProductById(product.getId()) == null) {
            context.interruptWithError("产品不存在: " + product.getId());
            return;
        }

        productMapper.updateProduct(EntityConvertor.convert2PO(product, true));
    }
}
