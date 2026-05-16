package com.txwx.social.crm.bizchain.account.manager;

import com.ruoyi.common.core.web.page.PageDomain;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.txwx.social.api.common.exception.BizException;
import com.txwx.social.api.common.exception.enums.ErrorCode;
import com.txwx.social.api.domain.dto.AccountDTO;
import com.txwx.social.crm.bizchain.account.context.AccountChainContext;
import com.txwx.social.crm.bizchain.account.handler.delete.AccountDeleteHandler;
import com.txwx.social.crm.bizchain.account.handler.insert.AccountInsertHandler;
import com.txwx.social.crm.bizchain.account.handler.query.AccountBaseQueryHandler;
import com.txwx.social.crm.bizchain.account.handler.update.AccountUpdateHandler;
import com.txwx.social.crm.common.chain.BaseChainContext;
import com.txwx.social.crm.common.chain.ChainBuilder;
import com.txwx.social.crm.common.chain.ChainInterruptType;
import com.txwx.social.crm.common.chain.handler.GlobalPermissionCheckHandler;
import com.txwx.social.crm.common.chain.handler.GlobalSentinelRateLimitHandler;
import com.txwx.social.crm.common.chain.handler.GlobalTransactionHandler;
import com.txwx.social.crm.common.validation.ValidationGroups;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountChainManager {
    // 全局处理器
    private final GlobalPermissionCheckHandler permissionCheckHandler;
    private final GlobalSentinelRateLimitHandler sentinelRateLimitHandler;
    private final GlobalTransactionHandler transactionHandler;

    // 账号处理器
    private final AccountBaseQueryHandler baseQueryHandler;
    private final AccountInsertHandler insertHandler;
    private final AccountUpdateHandler updateHandler;
    private final AccountDeleteHandler deleteHandler;


    // ==================== 预设业务链条 ====================
    private void executeQueryChain(AccountChainContext context) {
        new ChainBuilder<AccountChainContext>()
                .addHandler(sentinelRateLimitHandler)
                .addHandler(baseQueryHandler)
                .execute(context);
    }

    private void executeInsertChain(AccountChainContext context) {
        new ChainBuilder<AccountChainContext>()
                .addHandler(permissionCheckHandler)
                .addHandler(sentinelRateLimitHandler)
                .addHandler(transactionHandler)
                .addHandler(insertHandler)
                .execute(context);
    }

    private void executeUpdateChain(AccountChainContext context) {
        new ChainBuilder<AccountChainContext>()
                .addHandler(permissionCheckHandler)
                .addHandler(sentinelRateLimitHandler)
                .addHandler(transactionHandler)
                .addHandler(updateHandler)
                .execute(context);
    }

    private void executeDeleteChain(AccountChainContext context) {
        new ChainBuilder<AccountChainContext>()
                .addHandler(permissionCheckHandler)
                .addHandler(sentinelRateLimitHandler)
                .addHandler(transactionHandler)
                .addHandler(deleteHandler)
                .execute(context);
    }

    // ==================== 统一校验方法 ====================
    private <T> void validateObject(T object, Class<?>... groups) {
        if (object == null) {
            throw new BizException(ErrorCode.PARAMS_ERROR);
        }

    }

    // ==================== 增强结果检查 ====================
    private void checkResult(BaseChainContext context) {
        ChainInterruptType interruptType = context.getInterruptType();

        switch (interruptType) {
            case ERROR:
                if (context.getInterruptException() != null) {
                    log.error("链条执行失败: {}", context.getInterruptMessage(), context.getInterruptException());
                    throw new BizException(context.getInterruptMessage());
                } else {
                    log.error("链条执行失败: {}", context.getInterruptMessage());
                    throw new BizException(context.getInterruptMessage());
                }
            case NORMAL:
                log.info("链条正常中断: {}", context.getInterruptMessage());
                break;
            case SKIP_REMAINING:
                log.info("跳过剩余处理器: {}", context.getInterruptMessage());
                break;
            case NONE:
            case SKIP_CURRENT:
                break;
        }
    }

    // ==================== 对外业务方法 ====================
    public TableDataInfo queryAccountList(AccountDTO query, PageDomain pageDomain) {
        if (query == null) query = new AccountDTO();
        if (pageDomain == null) pageDomain = new PageDomain();

        AccountChainContext context = new AccountChainContext();
        context.setQueryParams(query);
        context.setPageDomain(pageDomain);
        context.setSentinelResourceName("account:query");

        executeQueryChain(context);
        checkResult(context);
        return context.getPageResult();
    }

    public void insertAccount(AccountDTO account) {
        validateObject(account, ValidationGroups.Add.class);

        AccountChainContext context = new AccountChainContext();
        context.setAccounts(List.of(account));
        context.setPermissionCode("system:account:add");
        context.setSentinelResourceName("account:insert");

        executeInsertChain(context);
        checkResult(context);
    }

    public List<Long> batchInsertAccounts(List<AccountDTO> accounts) {
        accounts.forEach(account -> validateObject(account, ValidationGroups.Add.class));

        AccountChainContext context = new AccountChainContext();
        context.setAccounts(accounts);
        context.setPermissionCode("system:account:add");
        context.setSentinelResourceName("account:insert");

        executeInsertChain(context);
        checkResult(context);
        return context.getAccountIds();
    }

    public void updateAccount(AccountDTO account) {
        validateObject(account, ValidationGroups.Update.class);

        AccountChainContext context = new AccountChainContext();
        context.setAccountList(List.of(account));
        context.setPermissionCode("system:account:edit");
        context.setSentinelResourceName("account:update");

        executeUpdateChain(context);
        checkResult(context);
    }

    public void deleteAccount(Long accountId) {

        AccountChainContext context = new AccountChainContext();
        context.setAccountIds(List.of(accountId));
        context.setPermissionCode("system:account:remove");
        context.setSentinelResourceName("account:delete");

        executeDeleteChain(context);
        checkResult(context);
    }

    public void batchDeleteAccounts(List<Long> accountIds) {

        AccountChainContext context = new AccountChainContext();
        context.setAccountIds(accountIds);
        context.setPermissionCode("system:account:remove");
        context.setSentinelResourceName("account:delete");

        executeDeleteChain(context);
        checkResult(context);
    }
}
