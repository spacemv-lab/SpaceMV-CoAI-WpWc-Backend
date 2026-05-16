/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.bizchain.account.handler.update;

import com.txwx.social.api.domain.dto.AccountDTO;
import com.txwx.social.crm.bizchain.account.context.AccountChainContext;
import com.txwx.social.crm.common.chain.AbstractChainHandler;
import com.txwx.social.crm.service.IAccountService;
import com.txwx.social.crm.util.EntityConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class AccountUpdateHandler extends AbstractChainHandler<AccountChainContext> {
    @Autowired
    private IAccountService accountService;

    @Override
    protected void doHandle(AccountChainContext context) {
        List<AccountDTO> accountDTOList = context.getAccountList();
        if (CollectionUtils.isEmpty(accountDTOList)) {
            context.interruptWithError("更新的账号信息为空");
            return;
        }
        //TODO 仅支持单账号
        AccountDTO account = accountDTOList.get(0);
        // 检查账号是否存在
        if (accountService.selectAccountById(account.getId()) == null) {
            context.interruptWithError("账号不存在: " + account.getId());
            return;
        }

        int rows = accountService.updateAccount(EntityConvertor.convert2PO(account, true));
        if (rows == 0) {
            context.interruptWithError("账号更新失败，可能已被删除");
        }
    }
}
