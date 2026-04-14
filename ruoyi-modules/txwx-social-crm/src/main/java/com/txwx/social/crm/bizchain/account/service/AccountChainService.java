package com.txwx.social.crm.bizchain.account.service;

import com.ruoyi.common.core.web.page.PageDomain;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.txwx.social.api.domain.dto.AccountDTO;
import com.txwx.social.crm.bizchain.account.manager.AccountChainManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountChainService {
    private final AccountChainManager accountChainManager;

    public TableDataInfo selectAccountList(AccountDTO query, PageDomain pageDomain) {
        return accountChainManager.queryAccountList(query, pageDomain);
    }

    public Long insertAccount(AccountDTO account) {
        return accountChainManager.insertAccount(account);
    }

    public List<Long> batchInsertAccounts(List<AccountDTO> accounts) {
        return accountChainManager.batchInsertAccounts(accounts);
    }

    public void updateAccount(AccountDTO account) {
        accountChainManager.updateAccount(account);
    }

    public void deleteAccount(Long accountId) {
        accountChainManager.deleteAccount(accountId);
    }

    public void batchDeleteAccounts(List<Long> accountIds) {
        accountChainManager.batchDeleteAccounts(accountIds);
    }
}
