package com.txwx.social.dashboard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.txwx.social.dashboard.domain.common.PageRequest;
import com.txwx.social.dashboard.domain.common.PageResult;
import com.txwx.social.dashboard.domain.condition.FlowSearchCondition;
import com.txwx.social.dashboard.domain.dto.ProductPlatformDto;
import com.txwx.social.dashboard.domain.entity.DwsBizsummaryChannelDaily;
import com.txwx.social.dashboard.domain.vo.FlowSourceVo;

import java.util.List;

public interface IDwsBizsummaryChannelDailyService extends IService<DwsBizsummaryChannelDaily> {
    PageResult<DwsBizsummaryChannelDaily> select(PageRequest pageRequest, FlowSearchCondition condition);

    List<FlowSourceVo> selectSource(ProductPlatformDto productPlatformDto);
}
