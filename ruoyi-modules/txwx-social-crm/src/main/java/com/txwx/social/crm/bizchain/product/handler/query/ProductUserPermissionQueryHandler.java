package com.txwx.social.crm.bizchain.product.handler.query;

import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.api.common.exception.BizException;
import com.txwx.social.api.common.exception.enums.ErrorCode;
import com.txwx.social.api.domain.dto.SimpleProductDTO;
import com.txwx.social.crm.bizchain.product.context.ProductChainContext;
import com.txwx.social.crm.common.chain.AbstractChainHandler;
import com.txwx.social.crm.domain.po.TxwxUserPermissionPO;
import com.txwx.social.crm.mapper.TxwxUserPermissionMapper;
import com.txwx.social.crm.util.StreamUtil;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Component
public class ProductUserPermissionQueryHandler extends AbstractChainHandler<ProductChainContext> {
    @Autowired
    private TxwxUserPermissionMapper permissionMapper;

    @Override
    protected void doHandle(ProductChainContext context) {

        List<SimpleProductDTO> productList = context.getProductList();
        if (productList == null || productList.isEmpty()) {
            context.skipCurrentHandler("产品列表为空");
            return;
        }

        Long userId = SecurityUtils.getUserId();
        List<Long> productIds = productList.stream().map(SimpleProductDTO::getId).toList();
        if (CollectionUtils.isEmpty(productIds)) {
            return;
        }
        List<Long> queryProductIds = Lists.newArrayList();
        List<TxwxUserPermissionPO> permissions = permissionMapper.selectPermissionByUserId(userId);
        if (CollectionUtils.isEmpty(permissions)) {
            context.interruptWithError("该用户对该产品没有权限");
            return;
        }
        permissions.forEach(permission -> {
            // TODO 这里先简单地写成常量
            if (permission.getRelationType() == 1) {
                String relIds = permission.getRelationIds();
                if (!StringUtils.hasText(relIds)) {
                    List<Long> longList = Arrays.stream(relIds.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(Long::valueOf)
                            .toList();
                    queryProductIds.addAll(longList);
                }
            }
        });

        if (!StreamUtil.isListEqualIgnoreOrder(productIds, queryProductIds)) {
            throw new BizException(ErrorCode.OPERATION_NOT_ALLOWED);
        }
    }
}
