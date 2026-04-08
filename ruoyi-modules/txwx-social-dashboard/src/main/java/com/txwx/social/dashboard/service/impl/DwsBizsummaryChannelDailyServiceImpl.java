package com.txwx.social.dashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.txwx.social.dashboard.domain.common.PageRequest;
import com.txwx.social.dashboard.domain.common.PageResult;
import com.txwx.social.dashboard.domain.condition.FlowSearchCondition;
import com.txwx.social.dashboard.domain.dto.ProductPlatformDto;
import com.txwx.social.dashboard.domain.entity.DwsBizsummaryChannelDaily;
import com.txwx.social.dashboard.domain.vo.FlowSourceVo;
import com.txwx.social.dashboard.mapper.DwsBizsummaryChannelDailyMapper;
import com.txwx.social.dashboard.service.IDwsBizsummaryChannelDailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DwsBizsummaryChannelDailyServiceImpl extends ServiceImpl<DwsBizsummaryChannelDailyMapper, DwsBizsummaryChannelDaily> implements IDwsBizsummaryChannelDailyService {

    @Autowired
    private DwsBizsummaryChannelDailyMapper dwsBizsummaryChannelDailyMapper;

    @Override
    public PageResult<DwsBizsummaryChannelDaily> select(PageRequest pageRequest, FlowSearchCondition condition) {
        PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
        LambdaQueryWrapper<DwsBizsummaryChannelDaily> queryWrapper = new LambdaQueryWrapper<>();
        if (condition != null) {
            if (condition.getChannel() != null && !condition.getChannel().isEmpty()) {
                queryWrapper.in(DwsBizsummaryChannelDaily::getChannel, condition.getChannel());
            }
            if (condition.getStartTime() != null && condition.getEndTime() != null) queryWrapper.between(DwsBizsummaryChannelDaily::getRefDate, condition.getStartTime(), condition.getEndTime());
        }
        queryWrapper.eq(condition.getProductId() != null , DwsBizsummaryChannelDaily::getProductId,  condition.getProductId());
        queryWrapper.eq(condition.getPlatformId() != null , DwsBizsummaryChannelDaily::getPlatformId,  condition.getPlatformId());
        queryWrapper.orderByDesc(DwsBizsummaryChannelDaily::getRefDate);
        List<DwsBizsummaryChannelDaily> res = dwsBizsummaryChannelDailyMapper.selectList(queryWrapper);
        PageInfo<DwsBizsummaryChannelDaily> pageInfo = new PageInfo<>(res);
        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getList());
    }

    @Override
    public List<FlowSourceVo> selectSource(ProductPlatformDto productPlatformDto) {
        return dwsBizsummaryChannelDailyMapper.selectSource(productPlatformDto);
    }
}
