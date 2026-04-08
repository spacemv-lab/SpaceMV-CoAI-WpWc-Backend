package com.txwx.social.crm.service.impl;

import com.txwx.social.crm.domain.po.TxwxAccountPO;
import com.txwx.social.crm.mapper.TxwxAccountMapper;
import com.txwx.social.crm.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
}
