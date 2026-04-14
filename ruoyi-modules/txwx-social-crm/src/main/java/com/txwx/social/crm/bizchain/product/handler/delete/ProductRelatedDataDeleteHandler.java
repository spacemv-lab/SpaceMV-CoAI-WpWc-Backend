package com.txwx.social.crm.bizchain.product.handler.delete;

import com.txwx.social.crm.bizchain.product.context.ProductChainContext;
import com.txwx.social.crm.common.chain.AbstractChainHandler;
import com.txwx.social.crm.service.IAccountService;
import com.txwx.social.crm.service.IProductChannelService;
import com.txwx.social.crm.service.ITxwxUserPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class ProductRelatedDataDeleteHandler extends AbstractChainHandler<ProductChainContext> {
    @Autowired
    private ITxwxUserPermissionService userPermissionService;
    @Autowired
    private IProductChannelService productChannelService;
    @Autowired
    private IAccountService accountService;

    @Override
    protected void doHandle(ProductChainContext context) {
        if (CollectionUtils.isEmpty(context.getProductIds())) {
            context.skipCurrentHandler("产品id为空");
            return;
        }
        //TODO 目前只支持单产品
        Long productId = context.getProductIds().get(0);

        userPermissionService.deleteUserPermissionByProductId(productId);

        productChannelService.deleteProductChannelByProductIds(List.of(productId));
        accountService.deleteByProductIds(List.of(productId));
    }


}
