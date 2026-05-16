package com.txwx.social.crm.bizchain.product.handler.insert;

import com.txwx.social.api.domain.dto.ProductChannelDTO;
import com.txwx.social.crm.bizchain.product.context.ProductChainContext;
import com.txwx.social.crm.common.chain.AbstractChainHandler;
import com.txwx.social.crm.domain.po.TxwxProductChannelPO;
import com.txwx.social.crm.mapper.TxwxProductChannelMapper;
import com.txwx.social.crm.util.EntityConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class ProductChannelInsertHandler extends AbstractChainHandler<ProductChainContext> {
    @Autowired
    private TxwxProductChannelMapper productChannelMapper;

    @Override
    protected void doHandle(ProductChainContext context) {
        //TODO 目前只支持单产品单渠道
        Long productId = context.getProductId();

        if (productId == null || productId == 0L) {
            context.skipCurrentHandler("没有需要添加的产品ID");
            return;
        }

        List<ProductChannelDTO> productChannelDTOList = context.getExt("productChannels", List.class);

        if (CollectionUtils.isEmpty(productChannelDTOList)) {
            context.skipCurrentHandler("没有需要添加的渠道关系");
            return;
        }

        TxwxProductChannelPO productChannelPO = EntityConvertor.convert2PO(productChannelDTOList.get(0), true);
        productChannelPO.setProductId(productId);
        productChannelMapper.insertProductChannel(productChannelPO);
    }
}
