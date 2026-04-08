package com.txwx.social.crm.service.impl;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.txwx.social.crm.domain.po.TxwxChannelPO;
import com.txwx.social.crm.domain.po.TxwxProductChannelPO;
import com.txwx.social.crm.mapper.TxwxChannelMapper;
import com.txwx.social.crm.mapper.TxwxProductChannelMapper;
import com.txwx.social.crm.service.IChannelService;
import com.txwx.social.crm.service.IProductChannelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.ibatis.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 渠道服务实现类
 *
 * @author txwx
 * @date 2026-04-03
 */
@Service
@Slf4j
public class ChannelServiceImpl implements IChannelService {

    @Autowired
    private TxwxChannelMapper channelMapper;

    @Autowired
    private IProductChannelService productChannelService;

    @Override
    public List<TxwxChannelPO> selectChannelList(TxwxChannelPO channel) {
        return channelMapper.selectChannelList(channel);
    }

    @Override
    public TxwxChannelPO selectChannelById(Long id) {
        return channelMapper.selectChannelById(id);
    }

    @Override
    public List<TxwxChannelPO> selectChannelByIds(List<Long> ids) {
        return channelMapper.selectChannelByIds(ids);
    }

    @Override
    public Map<Long, List<TxwxChannelPO>> selectChannelByProductIds(List<Long> productIds) {
        if (CollectionUtils.isEmpty(productIds)) {
            return Map.of();
        }
        Map<Long, List<TxwxProductChannelPO>> prod2ChannelMap = productChannelService.selectChannelIdsByProductIds(productIds);
        return selectChannelByProductIds(prod2ChannelMap);
    }

    @Override
    public Map<Long, List<TxwxChannelPO>> selectChannelByProductIds(Map<Long, List<TxwxProductChannelPO>> p2cIdMap) {
        if (MapUtils.isEmpty(p2cIdMap)) {
            return Map.of();
        }

        System.out.println(new Gson().toJson(p2cIdMap));
        Map<Long, List<TxwxChannelPO>> resMap = new HashMap<>();
        p2cIdMap.forEach((pid, relPoList) -> {
            List<Long> cids = relPoList.stream().mapToLong(TxwxProductChannelPO::getChannelId).boxed().toList();
            List<TxwxChannelPO> poList = selectChannelByIds(cids);
            resMap.put(pid, poList);
        });
        return resMap;
    }


    @Transactional
    @Override
    public int insertChannel(TxwxChannelPO channel, TxwxProductChannelPO productChannelPO) {
        int count = channelMapper.insertChannel(channel);
        count += productChannelService.insertProductChannel(productChannelPO);
        return count;
    }

    @Override
    public int updateChannel(TxwxChannelPO channel) {
        return channelMapper.updateChannel(channel);
    }

    @Override
    @Transactional
    public int deleteChannelByIds(List<Long> ids) {
        int count = channelMapper.deleteChannelByIds(ids);
        count += productChannelService.deleteProductChannelByChannelIds(ids);
        return count;
    }

}
