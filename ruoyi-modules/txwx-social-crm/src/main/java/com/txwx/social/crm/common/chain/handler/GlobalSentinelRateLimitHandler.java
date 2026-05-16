/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.common.chain.handler;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.txwx.social.crm.common.chain.AbstractChainHandler;
import com.txwx.social.crm.common.chain.BaseChainContext;
import org.springframework.stereotype.Component;

@Component
public class GlobalSentinelRateLimitHandler extends AbstractChainHandler<BaseChainContext> {
    @Override
    protected void doHandle(BaseChainContext context) {
        if (context.isSkipSentinelRateLimit()) return;

        String resourceName = context.getSentinelResourceName();
        if (resourceName == null || resourceName.trim().isEmpty()) return;

        Entry entry = null;
        try {
            entry = SphU.entry(resourceName);
        } catch (BlockException e) {
            context.interruptWithError("请求过于频繁，请稍后再试");
        } finally {
            if (entry != null) entry.exit();
        }
    }
}
