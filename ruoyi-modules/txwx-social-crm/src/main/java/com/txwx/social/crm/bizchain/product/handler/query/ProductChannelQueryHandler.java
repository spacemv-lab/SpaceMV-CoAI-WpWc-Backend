/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.bizchain.product.handler.query;

import com.txwx.social.api.domain.dto.ChannelDTO;
import com.txwx.social.api.domain.dto.ProductChannelDTO;
import com.txwx.social.api.domain.dto.ProductDTO;
import com.txwx.social.api.domain.dto.SimpleProductDTO;
import com.txwx.social.crm.bizchain.product.context.ProductChainContext;
import com.txwx.social.crm.common.chain.AbstractChainHandler;
import com.txwx.social.crm.domain.po.TxwxChannelPO;
import com.txwx.social.crm.domain.po.TxwxProductChannelPO;
import com.txwx.social.crm.mapper.TxwxChannelMapper;
import com.txwx.social.crm.mapper.TxwxProductChannelMapper;
import com.txwx.social.crm.service.IProductChannelService;
import com.txwx.social.crm.util.EntityConvertor;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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

        List<ProductDTO> productList = context.getProductList();
        if (productList == null || productList.isEmpty()) {
            context.skipCurrentHandler("产品列表为空");
            return;
        }

        List<Long> productIds = Lists.newArrayList();
        productList.forEach(productDTO -> {
            productIds.add(productDTO.getBaseInfo().getId());
        });
        List<TxwxProductChannelPO> channelPOS = productChannelService.selectChannelByProductIds(productIds);

        List<Long> channelIds = channelPOS.stream().map(TxwxProductChannelPO::getChannelId).toList();
        List<TxwxChannelPO> channelPOList = channelMapper.selectChannelByIds(channelIds);

        Map<Long, TxwxChannelPO> channelPOMap = channelPOList.stream()
                .collect(Collectors.toMap(TxwxChannelPO::getId, po -> po));
        Map<Long, List<Long>> product2Channel = channelPOS.stream()
                .collect(Collectors.groupingBy(
                        TxwxProductChannelPO::getProductId,
                        Collectors.mapping(
                                TxwxProductChannelPO::getChannelId,
                                Collectors.toList()
                        )
                ));
        productList.forEach(productDTO -> {
            List<Long> cids = product2Channel.getOrDefault(productDTO.getBaseInfo().getId(), Lists.newArrayList());
            if (!CollectionUtils.isEmpty(cids)) {
                productDTO.setChannelDTOList(cids.stream().map(cid -> {
                    return EntityConvertor.convert2DTO(channelPOMap.get(cid));
                }).toList());
            }
        });
        context.setProductChannelMap(product2Channel);
    }
}