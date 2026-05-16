/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.common.chain.handler;

import com.txwx.social.crm.common.chain.AbstractChainHandler;
import com.txwx.social.crm.common.chain.BaseChainContext;
import com.txwx.social.crm.common.chain.ChainInterruptType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Component
public class GlobalTransactionHandler extends AbstractChainHandler<BaseChainContext> {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handle(BaseChainContext context) {
        super.handle(context);

        // 如果是错误中断，手动标记事务回滚
        if (context.getInterruptType() == ChainInterruptType.ERROR) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        // 正常中断和跳过中断不回滚事务
    }

    @Override
    protected void doHandle(BaseChainContext context) {
        // 事务由handle方法管理
    }
}
