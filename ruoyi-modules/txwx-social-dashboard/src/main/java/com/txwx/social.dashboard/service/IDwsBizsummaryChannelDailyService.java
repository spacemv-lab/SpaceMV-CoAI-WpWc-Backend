package com.txwx.social.dashboard.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.txwx.social.dashboard.domain.condition.FlowSearchCondition;
import com.txwx.social.dashboard.domain.entity.DwsBizsummaryChannelDaily;
import com.txwx.social.dashboard.domain.entity.FlowSource;

import java.util.List;

public interface IDwsBizsummaryChannelDailyService extends IService<DwsBizsummaryChannelDaily> {

    List<DwsBizsummaryChannelDaily> selectByCondition(FlowSearchCondition condition);

    List<FlowSource> selectSource(Long accountId);

    /**
     * 分页查询
     * @param condition 查询条件
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<DwsBizsummaryChannelDaily> selectByPage(FlowSearchCondition condition, int pageNum, int pageSize);

}
