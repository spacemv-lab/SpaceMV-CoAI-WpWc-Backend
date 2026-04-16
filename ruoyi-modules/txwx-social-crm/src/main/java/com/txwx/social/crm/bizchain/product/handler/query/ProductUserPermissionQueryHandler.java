package com.txwx.social.crm.bizchain.product.handler.query;

import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.api.common.exception.BizException;
import com.txwx.social.api.common.exception.enums.ErrorCode;
import com.txwx.social.api.domain.dto.SimpleProductDTO;
import com.txwx.social.crm.bizchain.product.context.ProductChainContext;
import com.txwx.social.crm.common.chain.AbstractChainHandler;
import com.txwx.social.crm.domain.po.TxwxUserPermissionPO;
import com.txwx.social.crm.enums.PermissionTypeEnum;
import com.txwx.social.crm.mapper.TxwxUserPermissionMapper;
import com.txwx.social.crm.util.EntityConvertor;
import com.txwx.social.crm.util.StreamUtil;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.compress.utils.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Component
public class ProductUserPermissionQueryHandler extends AbstractChainHandler<ProductChainContext> {
    @Autowired
    private TxwxUserPermissionMapper permissionMapper;

    @Override
    protected void doHandle(ProductChainContext context) {
        Long userId = SecurityUtils.getUserId();

        List<TxwxUserPermissionPO> permissions = permissionMapper.selectPermissionByUserId(userId);
        if (CollectionUtils.isEmpty(permissions)) {
            return;
        }

        List<String> queryProductStrIds = permissions.stream()
                .filter(po -> po.getRelationType().equals(PermissionTypeEnum.PRODUCT_LEVEL.getCode()))
                .map(TxwxUserPermissionPO::getRelationIds)
                .toList();

        List<Long> authProductIds = Lists.newArrayList();

        queryProductStrIds.forEach(strId -> {
            List<Long> curList = EntityConvertor.convertToLongList(strId);
            authProductIds.addAll(curList);
        });

        context.setProductIds(authProductIds);
    }
}
