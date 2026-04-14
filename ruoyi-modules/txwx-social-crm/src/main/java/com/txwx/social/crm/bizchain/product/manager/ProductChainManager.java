package com.txwx.social.crm.bizchain.product.manager;

import com.ruoyi.common.core.web.page.PageDomain;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.txwx.social.api.common.exception.BizException;
import com.txwx.social.api.common.exception.enums.ErrorCode;
import com.txwx.social.api.domain.dto.ProductChannelDTO;
import com.txwx.social.api.domain.dto.SimpleProductDTO;
import com.txwx.social.api.domain.dto.UserPermissionDTO;
import com.txwx.social.crm.bizchain.product.context.ProductChainContext;
import com.txwx.social.crm.bizchain.product.handler.delete.ProductBaseDeleteHandler;
import com.txwx.social.crm.bizchain.product.handler.delete.ProductRelatedDataDeleteHandler;
import com.txwx.social.crm.bizchain.product.handler.insert.ProductBaseInsertHandler;
import com.txwx.social.crm.bizchain.product.handler.insert.ProductChannelInsertHandler;
import com.txwx.social.crm.bizchain.product.handler.insert.UserPermissionInsertHandler;
import com.txwx.social.crm.bizchain.product.handler.query.ChannelAccountQueryHandler;
import com.txwx.social.crm.bizchain.product.handler.query.ProductBaseQueryHandler;
import com.txwx.social.crm.bizchain.product.handler.query.ProductChannelQueryHandler;
import com.txwx.social.crm.bizchain.product.handler.query.ProductUserPermissionQueryHandler;
import com.txwx.social.crm.bizchain.product.handler.update.ProductBaseUpdateHandler;
import com.txwx.social.crm.bizchain.product.handler.update.ProductChannelUpdateHandler;
import com.txwx.social.crm.bizchain.product.handler.update.ProductUserPermissionUpdateHandler;
import com.txwx.social.crm.common.chain.BaseChainContext;
import com.txwx.social.crm.common.chain.ChainBuilder;
import com.txwx.social.crm.common.chain.ChainInterruptType;
import com.txwx.social.crm.common.chain.handler.GlobalPermissionCheckHandler;
import com.txwx.social.crm.common.chain.handler.GlobalSentinelRateLimitHandler;
import com.txwx.social.crm.common.chain.handler.GlobalTransactionHandler;
import com.txwx.social.crm.common.config.QueryConfig;
import com.txwx.social.crm.common.validation.ValidationGroups;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductChainManager {
    // 全局处理器
    private final GlobalPermissionCheckHandler permissionCheckHandler;
    private final GlobalSentinelRateLimitHandler sentinelRateLimitHandler;
    private final GlobalTransactionHandler transactionHandler;

    // 产品处理器
    private final ProductBaseQueryHandler baseQueryHandler;
    private final ProductUserPermissionQueryHandler userPermissionQueryHandler;
    private final ProductChannelQueryHandler channelQueryHandler;
    private final ChannelAccountQueryHandler channelAccountQueryHandler;
    private final ProductBaseInsertHandler baseInsertHandler;
    private final UserPermissionInsertHandler userPermissionInsertHandler;
    private final ProductChannelInsertHandler channelInsertHandler;
    private final ProductBaseUpdateHandler baseUpdateHandler;
    private final ProductUserPermissionUpdateHandler userPermissionUpdateHandler;
    private final ProductChannelUpdateHandler channelUpdateHandler;
    private final ProductBaseDeleteHandler baseDeleteHandler;
    private final ProductRelatedDataDeleteHandler relatedDataDeleteHandler;

    // ==================== 预设业务链条 ====================
    private void executeQueryChain(ProductChainContext context) {
        new ChainBuilder<ProductChainContext>()
                .addHandler(sentinelRateLimitHandler)
                .addHandler(userPermissionQueryHandler)
                .addHandler(baseQueryHandler)
                .addHandler(channelQueryHandler)
                .addHandler(channelAccountQueryHandler)
                .execute(context);
    }

    private void executeInsertChain(ProductChainContext context) {
        new ChainBuilder<ProductChainContext>()
                .addHandler(permissionCheckHandler)
                .addHandler(sentinelRateLimitHandler)
                .addHandler(transactionHandler)
                .addHandler(baseInsertHandler)
                .addHandler(userPermissionInsertHandler)
                .addHandler(channelInsertHandler)
                .execute(context);
    }

    private void executeUpdateChain(ProductChainContext context) {
        new ChainBuilder<ProductChainContext>()
                .addHandler(permissionCheckHandler)
                .addHandler(sentinelRateLimitHandler)
                .addHandler(transactionHandler)
                .addHandler(baseUpdateHandler)
                .addHandler(userPermissionUpdateHandler)
                .addHandler(channelUpdateHandler)
                .execute(context);
    }

    private void executeDeleteChain(ProductChainContext context) {
        new ChainBuilder<ProductChainContext>()
                .addHandler(permissionCheckHandler)
                .addHandler(sentinelRateLimitHandler)
                .addHandler(transactionHandler)
                .addHandler(relatedDataDeleteHandler)
                .addHandler(baseDeleteHandler)
                .execute(context);
    }

    // ==================== 统一校验方法 ====================
    private <T> void validateObject(T object, Class<?>... groups) {
        if (object == null) {
            throw new BizException(ErrorCode.PARAMS_ERROR);
        }

    }


    // ==================== 增强结果检查（处理所有中断类型） ====================
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
                // 正常执行完成
                break;
        }
    }

    // ==================== 对外业务方法 ====================
    public TableDataInfo queryProductList(SimpleProductDTO query, PageDomain pageDomain, QueryConfig queryConfig) {
        if (query == null) query = new SimpleProductDTO();
        if (queryConfig == null) queryConfig = new QueryConfig();
        if (pageDomain == null) pageDomain = new PageDomain();

        ProductChainContext context = new ProductChainContext();
        context.setQueryParams(query);
        context.setPageDomain(pageDomain);
        context.setSentinelResourceName("product:query");
        context.setQueryConfig(queryConfig);

        executeQueryChain(context);
        checkResult(context);
        return context.getPageResult();
    }

    public Long insertProduct(SimpleProductDTO product,
                              List<UserPermissionDTO> userPermissions,
                              List<ProductChannelDTO> productChannels) {
        validateObject(product, ValidationGroups.Add.class);

        ProductChainContext context = new ProductChainContext();
        context.setProductList(List.of(product));
        context.putExt("userPermissions", userPermissions);
        context.putExt("productChannels", productChannels);
        context.setPermissionCode("system:product:add");
        context.setSentinelResourceName("product:insert");

        executeInsertChain(context);
        checkResult(context);
        return context.getProductId();
    }

    public void updateProduct(SimpleProductDTO product,
                              List<UserPermissionDTO> userPermissions,
                              List<ProductChannelDTO> productChannels) {
        validateObject(product, ValidationGroups.Update.class);

        ProductChainContext context = new ProductChainContext();
        context.setProductInfo(product);
        context.setProductId(product.getId());
        context.putExt("userPermissions", userPermissions);
        context.putExt("productChannels", productChannels);
        context.setPermissionCode("system:product:edit");
        context.setSentinelResourceName("product:update");

        executeUpdateChain(context);
        checkResult(context);
    }

    public void deleteProduct(Long productId) {

        ProductChainContext context = new ProductChainContext();
        context.setProductId(productId);
        context.setPermissionCode("system:product:remove");
        context.setSentinelResourceName("product:delete");

        executeDeleteChain(context);
        checkResult(context);
    }

    public Long insertProductOnly(SimpleProductDTO product) {
        validateObject(product, ValidationGroups.Add.class);

        ProductChainContext context = new ProductChainContext();
        context.setProductInfo(product);
        context.setPermissionCode("system:product:add");
        context.setSentinelResourceName("product:insert");

        new ChainBuilder<ProductChainContext>()
                .addHandler(permissionCheckHandler)
                .addHandler(sentinelRateLimitHandler)
                .addHandler(transactionHandler)
                .addHandler(baseInsertHandler)
                .execute(context);

        checkResult(context);
        return context.getProductId();
    }
}
