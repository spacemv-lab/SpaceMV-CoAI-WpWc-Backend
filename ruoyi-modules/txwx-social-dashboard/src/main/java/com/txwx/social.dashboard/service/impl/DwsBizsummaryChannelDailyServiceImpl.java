package com.txwx.social.dashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.txwx.social.dashboard.domain.condition.FlowSearchCondition;
import com.txwx.social.dashboard.domain.entity.DwsBizsummaryChannelDaily;
import com.txwx.social.dashboard.mapper.DwsBizsummaryChannelDailyMapper;
import com.txwx.social.dashboard.domain.entity.FlowSource;
import com.txwx.social.dashboard.service.IDwsBizsummaryChannelDailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DwsBizsummaryChannelDailyServiceImpl extends ServiceImpl<DwsBizsummaryChannelDailyMapper, DwsBizsummaryChannelDaily> implements IDwsBizsummaryChannelDailyService {

    @Autowired
    private DwsBizsummaryChannelDailyMapper dwsBizsummaryChannelDailyMapper;

    public List<DwsBizsummaryChannelDaily> selectByCondition(FlowSearchCondition condition) {
        LambdaQueryWrapper<DwsBizsummaryChannelDaily> queryWrapper = buildqueryByCondition(condition);

        return dwsBizsummaryChannelDailyMapper.selectList(queryWrapper);
    }

    private static LambdaQueryWrapper<DwsBizsummaryChannelDaily> buildqueryByCondition(FlowSearchCondition condition) {
        LambdaQueryWrapper<DwsBizsummaryChannelDaily> queryWrapper = new LambdaQueryWrapper<>();
        if (condition != null) {
            if (condition.getChannel() != null && !condition.getChannel().isEmpty()) {
                queryWrapper.in(DwsBizsummaryChannelDaily::getChannel, condition.getChannel());
            }
            if (condition.getStartTime() != null && condition.getEndTime() != null) queryWrapper.between(DwsBizsummaryChannelDaily::getRefDate, condition.getStartTime(), condition.getEndTime());
            queryWrapper.eq(DwsBizsummaryChannelDaily::getAccountId,  condition.getAccountId());
        }

        queryWrapper.orderByDesc(DwsBizsummaryChannelDaily::getRefDate);
        return queryWrapper;
    }


    @Override
    public List<FlowSource> selectSource(Long accountId) {
        return dwsBizsummaryChannelDailyMapper.selectSource(accountId);
    }

    @Override
    public Page<DwsBizsummaryChannelDaily> selectByPage(FlowSearchCondition condition, int pageNum, int pageSize) {
        Page<DwsBizsummaryChannelDaily> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DwsBizsummaryChannelDaily> queryWrapper = buildqueryByCondition(condition);
        return dwsBizsummaryChannelDailyMapper.selectPage(page, queryWrapper);
    }

}
