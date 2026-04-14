package com.txwx.social.crm.bizchain.account.handler.insert;

import com.txwx.social.api.domain.dto.AccountDTO;
import com.txwx.social.crm.bizchain.account.context.AccountChainContext;
import com.txwx.social.crm.common.chain.AbstractChainHandler;
import com.txwx.social.crm.domain.po.TxwxAccountPO;
import com.txwx.social.crm.service.IAccountService;
import com.txwx.social.crm.util.EntityConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class AccountInsertHandler extends AbstractChainHandler<AccountChainContext> {
    @Autowired
    private IAccountService accountService;

    @Override
    protected void doHandle(AccountChainContext context) {
        List<AccountDTO> accounts = context.getAccounts();
        if (CollectionUtils.isEmpty(accounts)) {
            context.interruptNormally("没有需要增加的账号信息");
            return;
        }

        batchInsertAccounts(context, accounts);

    }

    private void batchInsertAccounts(AccountChainContext context, List<AccountDTO> accounts) {
        //TODO 当前仅支持单账号
        AccountDTO accountDTO = accounts.get(0);
        TxwxAccountPO query = new TxwxAccountPO();
        query.setAppId(accountDTO.getAppId());
        //TODO 目前仅支持单渠道
        query.setChannelId(1L);
        List<TxwxAccountPO> accountPOList = accountService.selectAccountByQuery(query);
        if (!CollectionUtils.isEmpty(accountPOList)) {
            context.interruptWithError("当前账号已被他人绑定，请绑定其他账号");
            return;
        }

        accountService.insertAccount(EntityConvertor.convert2PO(accountDTO, true));
    }
}
