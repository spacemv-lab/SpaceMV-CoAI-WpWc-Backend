package com.txwx.social.crm.bizchain.product.handler.update;


import com.txwx.social.crm.bizchain.product.context.ProductChainContext;
import com.txwx.social.crm.common.chain.AbstractChainHandler;
import com.txwx.social.crm.domain.po.TxwxProductChannelPO;
import com.txwx.social.crm.mapper.TxwxProductChannelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductChannelUpdateHandler extends AbstractChainHandler<ProductChainContext> {
    @Autowired
    private TxwxProductChannelMapper productChannelMapper;

    @Override
    protected void doHandle(ProductChainContext context) {

        List<TxwxProductChannelPO> channels = context.getExt("productChannels", List.class);

        if (channels == null || channels.isEmpty()) {
            context.skipCurrentHandler("没有需要更新的渠道关系");
            return;
        }

        Long productId = context.getProductId();
        if (productId == null) {
            context.interruptWithError("产品id为空");
            return;
        }
        int count = productChannelMapper.deleteProductChannelBypIds(List.of(productId));

        channels.forEach(channel -> channel.setProductId(productId));
        //TODO 先只支持单产品
        productChannelMapper.insertProductChannel(channels.get(0));
    }
}
