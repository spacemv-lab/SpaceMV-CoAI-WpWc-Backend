/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.common.chain.handler;

import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.crm.common.chain.AbstractChainHandler;
import com.txwx.social.crm.common.chain.BaseChainContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GlobalPermissionCheckHandler extends AbstractChainHandler<BaseChainContext> {
    @Override
    protected void doHandle(BaseChainContext context) {
        //TODO 当前默认跳过就行
        if (context.isSkipPermissionCheck() || SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            return;
        }

        String permissionCode = context.getPermissionCode();
        if (permissionCode != null && !permissionCode.trim().isEmpty()) {
            return;
        }

        List<String> permissionCodes = context.getPermissionCodes();
        if (permissionCodes != null && !permissionCodes.isEmpty()) {
            //TODO
            boolean hasAnyPermission = true;
            if (!hasAnyPermission) {
                context.interruptWithError("没有操作权限，需要以下权限之一：" + String.join(", ", permissionCodes));
            }
        }
    }
}
