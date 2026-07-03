/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.dashboard.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.txwx.social.dashboard.domain.condition.FlowSearchCondition;
import com.txwx.social.dashboard.domain.entity.DwsBizsummaryChannelDaily;
import com.txwx.social.dashboard.domain.entity.FlowSource;

import java.util.List;
import java.util.Map;

public interface IDwsBizsummaryChannelDailyService extends IService<DwsBizsummaryChannelDaily> {

    TableDataInfo selectByCondition(FlowSearchCondition condition);

    List<FlowSource> selectSource(Long accountId);

}
