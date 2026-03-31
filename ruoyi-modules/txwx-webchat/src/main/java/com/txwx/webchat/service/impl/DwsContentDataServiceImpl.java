package com.txwx.webchat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.txwx.webchat.domain.ArticleDetailDaily;
import com.txwx.webchat.domain.common.PageRequest;
import com.txwx.webchat.domain.common.PageResult;
import com.txwx.webchat.domain.condition.ContentDataSearchCondition;
import com.txwx.webchat.domain.entity.DwsContentData;
import com.txwx.webchat.mapper.DwsContentDataMapper;
import com.txwx.webchat.service.IDwsContentDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DwsContentDataServiceImpl extends ServiceImpl<DwsContentDataMapper, DwsContentData> implements IDwsContentDataService {

    @Autowired
    private DwsContentDataMapper dwsContentDataMapper;

    @Override
    public PageResult<DwsContentData> select(PageRequest pageRequest, ContentDataSearchCondition condition) {
        PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
        LambdaQueryWrapper<DwsContentData> queryWrapper = new LambdaQueryWrapper<>();
        if (condition != null) {
            if (condition.getTitle() != null) queryWrapper.like(DwsContentData::getTitle, condition.getTitle());
            if (condition.getStartTime() != null && condition.getEndTime() != null) queryWrapper.between(DwsContentData::getCreateTime, condition.getStartTime(), condition.getEndTime());
        }
        queryWrapper.eq(DwsContentData::getProductId, condition.getProductId());
        queryWrapper.eq(DwsContentData::getPlatformId, condition.getPlatformId());
        queryWrapper.orderByDesc(DwsContentData::getCreateTime);
        List<DwsContentData> res = dwsContentDataMapper.selectList(queryWrapper);
        PageInfo<DwsContentData> pageInfo = new PageInfo<>(res);
        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getList());
    }
}
