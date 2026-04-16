package com.txwx.social.crm.bizchain.product.context;

import com.txwx.social.api.domain.dto.*;
import com.txwx.social.crm.common.chain.BaseChainContext;
import com.txwx.social.crm.common.config.QueryConfig;
import com.txwx.social.crm.domain.po.TxwxProductPO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductChainContext extends BaseChainContext {
    // 输入参数
    private List<Long> productIds;
    private SimpleProductDTO queryParams;

    //修改输入
    private SimpleProductDTO simpleProductInfo;

    // 输出结果
    private Long productId;
    private List<ProductDTO> productList;
    private ProductDTO productInfo;

    private Map<Long, List<Long>> productChannelMap;
    private Map<Long, List<AccountDTO>> channelAccountMap;

    public QueryConfig queryConfig;

}
