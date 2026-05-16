/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.bizchain.account.handler.delete;

import com.txwx.social.crm.bizchain.account.context.AccountChainContext;
import com.txwx.social.crm.common.chain.AbstractChainHandler;
import com.txwx.social.crm.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class AccountDeleteHandler extends AbstractChainHandler<AccountChainContext> {
    @Autowired
    private IAccountService accountService;

    @Override
    protected void doHandle(AccountChainContext context) {
        List<Long> accountIds = context.getAccountIds();
        if (CollectionUtils.isEmpty(accountIds)) {
            context.interruptNormally("账号信息为空");
            return;
        }
        accountService.deleteAccountByIds(accountIds);
    }
}
