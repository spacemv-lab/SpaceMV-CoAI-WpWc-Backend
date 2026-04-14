package com.txwx.social.crm.bizchain.product.handler.query;

import com.txwx.social.api.domain.dto.ProductChannelDTO;
import com.txwx.social.api.domain.dto.SimpleProductDTO;
import com.txwx.social.crm.bizchain.product.context.ProductChainContext;
import com.txwx.social.crm.common.chain.AbstractChainHandler;
import com.txwx.social.crm.domain.po.TxwxProductChannelPO;
import com.txwx.social.crm.mapper.TxwxChannelMapper;
import com.txwx.social.crm.mapper.TxwxProductChannelMapper;
import com.txwx.social.crm.service.IProductChannelService;
import com.txwx.social.crm.util.EntityConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
public class ProductChannelQueryHandler extends AbstractChainHandler<ProductChainContext> {
    @Autowired
    private TxwxProductChannelMapper productChannelMapper;

    @Autowired
    private IProductChannelService productChannelService;

    @Autowired
    private TxwxChannelMapper channelMapper;

    @Override
    protected void doHandle(ProductChainContext context) {

        if (!context.getQueryConfig().isQueryChannels()) {
            context.skipCurrentHandler("查询深度不包含渠道");
            return;
        }

        List<SimpleProductDTO> productList = context.getProductList();
        if (productList == null || productList.isEmpty()) {
            context.skipCurrentHandler("产品列表为空");
            return;
        }

        List<Long> productIds = productList.stream().map(SimpleProductDTO::getId).toList();
        Map<Long, List<TxwxProductChannelPO>> prod2ChannelMap = productChannelService.selectChannelIdsByProductIds(productIds);
        Map<Long, List<ProductChannelDTO>> channelMap = EntityConvertor.convert2DTOMap(prod2ChannelMap);
        context.setProductChannelMap(channelMap);
    }
}