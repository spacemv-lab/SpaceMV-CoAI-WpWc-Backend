/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.bizchain.product.handler.update;

import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.api.domain.dto.UserPermissionDTO;
import com.txwx.social.crm.bizchain.product.context.ProductChainContext;
import com.txwx.social.crm.common.chain.AbstractChainHandler;
import com.txwx.social.crm.domain.po.TxwxUserPermissionPO;
import com.txwx.social.crm.enums.PermissionTypeEnum;
import com.txwx.social.crm.service.ITxwxUserPermissionService;
import com.txwx.social.crm.util.EntityConvertor;
import com.txwx.social.crm.util.StreamUtil;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Component
public class ProductUserPermissionUpdateHandler extends AbstractChainHandler<ProductChainContext> {
    @Autowired
    private ITxwxUserPermissionService userPermissionService;

    @Override
    protected void doHandle(ProductChainContext context) {
        List<UserPermissionDTO> permissions = context.getExt("userPermissions", List.class);

        if (permissions == null || permissions.isEmpty()) {
            context.skipCurrentHandler("没有需要更新的用户权限");
            return;
        }

        List<TxwxUserPermissionPO> userPermissionPOList =
                userPermissionService.selectPermissionByUserIdAndRelType(SecurityUtils.getUserId(), PermissionTypeEnum.PRODUCT_LEVEL.getCode());

        List<String> queryPermissions = userPermissionPOList.stream().map(TxwxUserPermissionPO::getRelationIds).toList();

        List<Long> queryPIds = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(queryPermissions)) {
            queryPermissions.forEach(relIds -> queryPIds.addAll(EntityConvertor.convertToLongList(relIds)));
        }

        //TODO 目前仅支持单产品
        TxwxUserPermissionPO userPermissionPO = EntityConvertor.convert2PO(permissions.get(0), true);
        if (Objects.equals(userPermissionPO.getRelationType(), PermissionTypeEnum.PRODUCT_LEVEL.getCode())) {
            List<Long> relIds = EntityConvertor.convertToLongList(userPermissionPO.getRelationIds());
            userPermissionPO.setRelationIds(EntityConvertor.convertToString(StreamUtil.unionDistinct(queryPIds, relIds)));
        }
        userPermissionService.insertPermission(userPermissionPO);
    }
}
