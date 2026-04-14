package com.txwx.social.crm.bizchain.product.handler.insert;

import com.txwx.social.api.domain.dto.UserPermissionDTO;
import com.txwx.social.crm.bizchain.product.context.ProductChainContext;
import com.txwx.social.crm.common.chain.AbstractChainHandler;
import com.txwx.social.crm.domain.po.TxwxUserPermissionPO;
import com.txwx.social.crm.mapper.TxwxUserPermissionMapper;
import com.txwx.social.crm.util.EntityConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class UserPermissionInsertHandler extends AbstractChainHandler<ProductChainContext> {
    @Autowired
    private TxwxUserPermissionMapper permissionMapper;

    @Override
    protected void doHandle(ProductChainContext context) {
        Long productId = context.getProductId();

        if (productId == null || productId == 0L) {
            context.skipCurrentHandler("没有需要添加的产品ID");
            return;
        }
        //TODO 目前仅支持单产品
        List<UserPermissionDTO> userPermissionDTOS = context.getExt("userPermissions", List.class);
        if (CollectionUtils.isEmpty(userPermissionDTOS)) {
            context.skipCurrentHandler("没有需要添加的权限的用户");
            return;
        }
        TxwxUserPermissionPO userPermissionPO = EntityConvertor.convert2PO(userPermissionDTOS.get(0), true);
        userPermissionPO.setRelationIds(EntityConvertor.convertToString(List.of(productId)));
        permissionMapper.insertPermission(userPermissionPO);
    }
}
