package com.txwx.social.crm.service.impl;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.txwx.social.crm.domain.po.TxwxProductChannelPO;
import com.txwx.social.crm.mapper.TxwxProductChannelMapper;
import com.txwx.social.crm.service.IProductChannelService;
import org.apache.commons.collections4.MapUtils;
import org.apache.ibatis.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 产品渠道关联服务实现类
 *
 * @author txwx
 * @date 2026-04-03
 */
@Service
public class ProductChannelServiceImpl implements IProductChannelService {

    @Autowired
    private TxwxProductChannelMapper productChannelMapper;

    @Override
    public List<TxwxProductChannelPO> selectProductChannelList(TxwxProductChannelPO productChannel) {
        return productChannelMapper.selectProductChannelList(productChannel);
    }

    @Override
    public TxwxProductChannelPO selectProductChannelById(Long id) {
        return productChannelMapper.selectProductChannelById(id);
    }

    @Override
    public int insertProductChannel(TxwxProductChannelPO productChannel) {
        return productChannelMapper.insertProductChannel(productChannel);
    }

    @Override
    public int updateProductChannel(TxwxProductChannelPO productChannel) {
        return productChannelMapper.updateProductChannel(productChannel);
    }

    @Override
    public int deleteProductChannelByChannelIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        return productChannelMapper.deleteProductChannelBycIds(ids);
    }

    @Override
    public int deleteProductChannelByProductIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        return productChannelMapper.deleteProductChannelBypIds(ids);
    }

    @Override
    public List<Long> selectChannelIdsByProductId(Long productId) {
        return productChannelMapper.selectChannelIdsByProductId(productId);
    }

    @Override
    public Map<Long, List<TxwxProductChannelPO>> selectChannelIdsByProductIds(List<Long> productIds) {
        if (CollectionUtils.isEmpty(productIds)) {
            return new HashMap<>();
        }
        List<TxwxProductChannelPO> productChannelPOS = productChannelMapper.selectChannelIdsByProductIds(productIds);
        return productChannelPOS.stream()
                .collect(Collectors.groupingBy(TxwxProductChannelPO::getProductId));
    }

    @Override
    public List<TxwxProductChannelPO> selectChannelByProductIds(List<Long> productIds) {
        return productChannelMapper.selectChannelIdsByProductIds(productIds);
    }

    @Override
    public List<Long> selectProductIdsByChannelId(Long channelId) {
        return productChannelMapper.selectProductIdsByChannelId(channelId);
    }
}
