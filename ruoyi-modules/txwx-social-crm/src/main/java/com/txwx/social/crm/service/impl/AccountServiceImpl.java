package com.txwx.social.crm.service.impl;

import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.bean.BeanUtils;
import com.ruoyi.common.core.web.domain.BaseEntity;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.api.domain.dto.AccountDTO;
import com.txwx.social.crm.domain.po.TxwxAccountPO;
import com.txwx.social.crm.domain.po.TxwxProductChannelPO;
import com.txwx.social.crm.mapper.TxwxAccountMapper;
import com.txwx.social.crm.service.IAccountService;
import com.txwx.social.crm.service.IProductChannelService;
import com.txwx.social.crm.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.txwx.social.crm.util.EntityConvertor.fillBaseInfo;

/**
 * 账号服务实现类
 *
 * @author txwx
 * @date 2026-04-03
 */
@Service
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private TxwxAccountMapper accountMapper;
    @Autowired
    private IProductChannelService productChannelService;

    @Override
    public List<TxwxAccountPO> selectAccountList(TxwxAccountPO account) {
        return accountMapper.selectAccountList(account);
    }

    @Override
    public TxwxAccountPO selectAccountById(Long id) {
        return accountMapper.selectAccountById(id);
    }

    @Override
    public int insertAccount(TxwxAccountPO account) {
        return accountMapper.insertAccount(account);
    }

    @Transactional
    @Override
    public int insertAccount(AccountDTO accountDTO) {
        TxwxAccountPO accountPO = new TxwxAccountPO();
        BeanUtils.copyProperties(accountDTO, accountPO);
        fillBaseInfo(accountPO);
        // 1. 新增accoutPO
        insertAccount(accountPO);
        // 2. 绑定产品渠道关系
        TxwxProductChannelPO txwxProductChannelPO = new TxwxProductChannelPO();
        txwxProductChannelPO.setProductId(accountDTO.getProductId());
        txwxProductChannelPO.setChannelId(accountDTO.getChannelId());
        fillBaseInfo(txwxProductChannelPO);
        productChannelService.insertProductChannel(txwxProductChannelPO);
        return 1;
    }

    @Override
    public int updateAccount(TxwxAccountPO account) {
        return accountMapper.updateAccount(account);
    }

    @Override
    public int deleteAccountByIds(List<Long> ids) {
        return accountMapper.deleteAccountByIds(ids);
    }

    @Override
    public List<TxwxAccountPO> selectAccountByProductId(Long productId) {
        return accountMapper.selectAccountByProductId(productId);
    }

    @Override
    public Map<Long, List<TxwxAccountPO>> selectAccountByProductId(List<Long> productIds) {
        if (CollectionUtils.isEmpty(productIds)) {
            return new HashMap<>();
        }
        List<TxwxAccountPO> accountPOList = accountMapper.selectAccountByProductIds(productIds);
        return accountPOList.stream().collect(Collectors.groupingBy(TxwxAccountPO::getProductId));
    }

    @Override
    public List<TxwxAccountPO> selectAccountByChannelId(Long channelId) {
        return accountMapper.selectAccountByChannelId(channelId);
    }

    @Override
    public Map<Long, List<TxwxAccountPO>> selectAccountByChannelIds(List<Long> cids) {
        if (CollectionUtils.isEmpty(cids)) {
            return new HashMap<>();
        }
        List<TxwxAccountPO> accountPOList = accountMapper.selectAccountByChannelIds(cids);
        return accountPOList.stream().collect(Collectors.groupingBy(TxwxAccountPO::getChannelId));
    }

    @Override
    public int deleteByProductIds(List<Long> productIds) {
        List<TxwxAccountPO> accountPOList = accountMapper.selectAccountByProductIds(productIds);
        List<Long> ids = accountPOList.stream().map(TxwxAccountPO::getId).toList();
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        return accountMapper.deleteAccountByIds(ids);
    }

    @Override
    public List<TxwxAccountPO> selectAccountByQuery(TxwxAccountPO query) {
        return accountMapper.selectAccountList(query);
    }

}
