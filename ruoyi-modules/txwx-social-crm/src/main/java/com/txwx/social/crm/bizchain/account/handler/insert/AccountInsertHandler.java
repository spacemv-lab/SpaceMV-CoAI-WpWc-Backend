/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.bizchain.account.handler.insert;

import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.api.domain.dto.AccountDTO;
import com.txwx.social.crm.bizchain.account.context.AccountChainContext;
import com.txwx.social.crm.common.chain.AbstractChainHandler;
import com.txwx.social.crm.domain.po.TxwxAccountPO;
import com.txwx.social.crm.service.IAccountService;
import com.txwx.social.crm.util.EntityConvertor;
import com.txwx.social.crm.util.WebChatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
        String appid = accountDTO.getAppId();
        String secret = accountDTO.getSecret();
        TxwxAccountPO query = new TxwxAccountPO();
        //TODO 目前仅支持单渠道
        query.setChannelId(1L);
        query.setUserId(SecurityUtils.getUserId());
        List<TxwxAccountPO> accountPOList = accountService.selectAccountByQuery(query);
        if (!CollectionUtils.isEmpty(accountPOList)) {
            context.interruptWithError("当前仅支持绑定1个账号，请先解绑后再绑定新账号");
            return;
        }

        TxwxAccountPO query2 = new TxwxAccountPO();
        query2.setChannelId(1L);
        query2.setAppId(appid);
        List<TxwxAccountPO> sameAccount = accountService.selectAccountByQuery(query2);
        if (!CollectionUtils.isEmpty(sameAccount)) {
            context.interruptWithError("当前账号已被别人绑定");
            return;
        }

        try {
            String accessToken = WebChatUtil.getAccessToken(appid, secret);
            if (!StringUtils.hasText(accessToken)) {
                context.interruptWithError("输入的appid和secret连通性检查失败，请重新校验");
                return;
            }
        } catch (Exception e) {
            context.interruptWithError(e.getMessage());
            return;
        }

        TxwxAccountPO accountPO = EntityConvertor.convert2PO(accountDTO, true);
        accountPO.setUserId(SecurityUtils.getUserId());
        accountService.insertAccount(accountPO);
    }
}
