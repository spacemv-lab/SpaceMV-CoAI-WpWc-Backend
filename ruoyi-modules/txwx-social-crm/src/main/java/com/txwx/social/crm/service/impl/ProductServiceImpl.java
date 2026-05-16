/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.service.impl;

import com.txwx.social.api.domain.dto.AccountDTO;
import com.txwx.social.api.domain.dto.ChannelDTO;
import com.txwx.social.crm.domain.po.TxwxAccountPO;
import com.txwx.social.crm.domain.po.TxwxChannelPO;
import com.txwx.social.crm.domain.po.TxwxProductChannelPO;
import com.txwx.social.crm.domain.po.TxwxProductPO;
import com.txwx.social.crm.mapper.TxwxProductMapper;
import com.txwx.social.crm.service.IAccountService;
import com.txwx.social.crm.service.IChannelService;
import com.txwx.social.crm.service.IProductChannelService;
import com.txwx.social.crm.service.IProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 产品服务实现类
 *
 * @author txwx
 * @date 2026-04-03
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    @Autowired
    private TxwxProductMapper productMapper;

    @Autowired
    private IChannelService channelService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IProductChannelService productChannelService;



    @Override
    public List<TxwxProductPO> selectProductList(TxwxProductPO product) {
        return productMapper.selectProductList(product);
    }

    @Override
    public TxwxProductPO selectProductById(Long id) {
        return productMapper.selectProductById(id);
    }

    @Override
    public int insertProduct(TxwxProductPO product) {
        return productMapper.insertProduct(product);
    }

    @Override
    public Long insertProductReturnID(TxwxProductPO product) {
        int affectedRows = productMapper.insertProductReturnID(product);

        if (affectedRows > 0) {
            // 2. 主键ID自动设置到product对象
            return product.getId();
        }

        throw new RuntimeException("插入失败");
    }

    @Override
    public int updateProduct(TxwxProductPO product) {
        return productMapper.updateProduct(product);
    }

    @Override
    public int deleteProductByIds(List<Long> ids) {
        return productMapper.deleteProductByIds(ids);
    }

    @Override
    public Map<Long, List<ChannelDTO>> getProduct2ChannelMap(List<Long> pids) {
        if (CollectionUtils.isEmpty(pids)) {
            return Map.of();
        }
        Map<Long, List<ChannelDTO>> resMap = new HashMap<>();
        Map<Long, List<TxwxProductChannelPO>> prod2ChannelIdMap = productChannelService.selectChannelIdsByProductIds(pids);
        Map<Long, List<TxwxChannelPO>> prod2ChannelPOMap = channelService.selectChannelByProductIds(prod2ChannelIdMap);
        Map<Long, List<TxwxAccountPO>> prod2AccountPOMap = accountService.selectAccountByProductId(pids);
        prod2ChannelPOMap.forEach((pid, channelPOs) -> {
            List<ChannelDTO> dtoList = Lists.newArrayList();
            resMap.put(pid, dtoList);
            channelPOs.forEach(channel -> {
                ChannelDTO channelDTO = new ChannelDTO();
                channelDTO.setId(channel.getId());
                channelDTO.setChannelName(channel.getChannelName());
                channelDTO.setChannelType(channel.getChannelType());
                channelDTO.setChannelDesc(channel.getChannelDesc());
                List<TxwxAccountPO> accountPOList = Optional.ofNullable(prod2AccountPOMap.get(pid))
                        .orElse(Collections.emptyList())
                        .stream()
                        .filter(account -> account.getChannelId().equals(channel.getId()))
                        .toList();
                List<AccountDTO> accountDTOList = accountPOList.stream().map(this::convert2DTO).toList();
                channelDTO.setAccountDTOList(accountDTOList);
                dtoList.add(channelDTO);
            });
        });
        return resMap;
    }

    private AccountDTO convert2DTO(TxwxAccountPO txwxAccountPO) {
        AccountDTO accountDTO = new AccountDTO();
        if (txwxAccountPO == null) {
            return accountDTO;
        }
        BeanUtils.copyProperties(txwxAccountPO, accountDTO);
        return accountDTO;
    }
}
