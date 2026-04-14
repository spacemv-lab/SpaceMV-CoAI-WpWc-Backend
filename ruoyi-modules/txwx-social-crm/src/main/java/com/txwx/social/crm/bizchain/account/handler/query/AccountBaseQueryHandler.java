package com.txwx.social.crm.bizchain.account.handler.query;

import com.github.pagehelper.Page;
import com.ruoyi.common.core.constant.HttpStatus;
import com.ruoyi.common.core.utils.PageUtils;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.txwx.social.api.domain.dto.AccountDTO;
import com.txwx.social.crm.bizchain.account.context.AccountChainContext;
import com.txwx.social.crm.common.chain.AbstractChainHandler;
import com.txwx.social.crm.domain.po.TxwxAccountPO;
import com.txwx.social.crm.domain.po.TxwxProductPO;
import com.txwx.social.crm.service.IAccountService;
import com.txwx.social.crm.util.EntityConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountBaseQueryHandler extends AbstractChainHandler<AccountChainContext> {
    @Autowired
    private IAccountService accountService;

    @Override
    protected void doHandle(AccountChainContext context) {
        PageUtils.startPage(context.getPageDomain());
        AccountDTO query = context.getQueryParams();
        List<TxwxAccountPO> accountList = accountService.selectAccountList(EntityConvertor.convert2PO(query, false));
        Page<TxwxAccountPO> page = (Page<TxwxAccountPO>) accountList;
        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setCode(HttpStatus.SUCCESS);
        dataInfo.setMsg("查询成功");
        dataInfo.setRows(page.getResult()); // 当前页数据
        dataInfo.setTotal(page.getTotal());
        context.setAccountList(page.getResult().stream().map(EntityConvertor::convert2DTO).toList());
        context.setPageResult(dataInfo);
    }
}
