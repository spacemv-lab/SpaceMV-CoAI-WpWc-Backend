package com.txwx.social.crm.bizchain.product.handler.query;

import com.txwx.social.api.common.exception.BizException;
import com.txwx.social.api.common.exception.enums.ErrorCode;
import com.txwx.social.api.domain.dto.AccountDTO;
import com.txwx.social.api.domain.dto.ProductChannelDTO;
import com.txwx.social.crm.bizchain.product.context.ProductChainContext;
import com.txwx.social.crm.common.chain.AbstractChainHandler;
import com.txwx.social.crm.domain.po.TxwxAccountPO;
import com.txwx.social.crm.domain.po.TxwxProductChannelPO;
import com.txwx.social.crm.mapper.TxwxAccountMapper;
import com.txwx.social.crm.util.EntityConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ChannelAccountQueryHandler extends AbstractChainHandler<ProductChainContext> {
    @Autowired
    private TxwxAccountMapper accountMapper;

    @Override
    protected void doHandle(ProductChainContext context) {
        if (!context.getQueryConfig().isQueryAccount()) {
            context.skipCurrentHandler("配置信息不需要查询账号");
        }

        List<Long> productIds = context.getProductIds();
        if (CollectionUtils.isEmpty(productIds)) {
            context.interruptWithError(ErrorCode.DATA_NOT_FOUND.getMsg(), new BizException(ErrorCode.DATA_NOT_FOUND));
        }
        Map<Long, List<ProductChannelDTO>> productChannelMap = context.getProductChannelMap();
        if (productChannelMap == null || productChannelMap.isEmpty()) {
            context.skipCurrentHandler("渠道列表为空");
            return;
        }

        List<Long> channelIds = productChannelMap.values().stream()
                .flatMap(List::stream)
                .map(ProductChannelDTO::getChannelId)
                .distinct()
                .toList();

        List<TxwxAccountPO> accounts = accountMapper.selectAccountByChannelIds(channelIds);
        List<AccountDTO> accountDTOList = EntityConvertor.convertPO2DTO(accounts);
        Map<Long, List<AccountDTO>> accountMap = accountDTOList.stream()
                .collect(Collectors.groupingBy(AccountDTO::getChannelId));
        context.setChannelAccountMap(accountMap);
    }
}
