package com.txwx.webchat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.txwx.webchat.domain.common.PageRequest;
import com.txwx.webchat.domain.common.PageResult;
import com.txwx.webchat.domain.condition.FlowSearchCondition;
import com.txwx.webchat.domain.dto.ProductPlatformDto;
import com.txwx.webchat.domain.entity.DwsBizsummaryChannelDaily;
import com.txwx.webchat.domain.vo.FlowSourceVo;

import java.util.List;

public interface IDwsBizsummaryChannelDailyService extends IService<DwsBizsummaryChannelDaily> {
    PageResult<DwsBizsummaryChannelDaily> select(PageRequest pageRequest, FlowSearchCondition condition);

    List<FlowSourceVo> selectSource(ProductPlatformDto productPlatformDto);
}
