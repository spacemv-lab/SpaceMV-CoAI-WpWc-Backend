/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.dashboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.txwx.social.dashboard.domain.entity.DwsBizsummaryChannelDaily;
import com.txwx.social.dashboard.domain.entity.FlowSource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DwsBizsummaryChannelDailyMapper extends BaseMapper<DwsBizsummaryChannelDaily> {


    List<FlowSource> selectSource(@Param("accountId") Long accountId);


}
