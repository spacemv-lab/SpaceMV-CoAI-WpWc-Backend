package com.txwx.social.dashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.txwx.social.dashboard.domain.condition.ContentDataSearchCondition;
import com.txwx.social.dashboard.domain.entity.DwsContentData;
import com.txwx.social.dashboard.domain.mapper.DwsContentDataMapper;
import com.txwx.social.dashboard.service.IDwsContentDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DwsContentDataServiceImpl extends ServiceImpl<DwsContentDataMapper, DwsContentData> implements IDwsContentDataService {

    @Autowired
    private DwsContentDataMapper dwsContentDataMapper;

    @Override
    public List<DwsContentData> select(ContentDataSearchCondition condition) {
        LambdaQueryWrapper<DwsContentData> queryWrapper = buildqueryWrapper(condition);
        return dwsContentDataMapper.selectList(queryWrapper);
    }

    @Override
    public Page<DwsContentData> selectDataListByPage(ContentDataSearchCondition condition, Integer pageNum, Integer pageSize) {
        // 构建分页对象
        Page<DwsContentData> page = new Page<>(pageNum, pageSize);

        // 构建 LambdaQueryWrapper
        LambdaQueryWrapper<DwsContentData> queryWrapper = buildqueryWrapper(condition);

        // 执行分页查询
        return dwsContentDataMapper.selectPage(page, queryWrapper);
    }

    private static LambdaQueryWrapper<DwsContentData> buildqueryWrapper(ContentDataSearchCondition condition) {
        LambdaQueryWrapper<DwsContentData> queryWrapper = new LambdaQueryWrapper<>();
        if (condition != null) {
            if (condition.getTitle() != null) queryWrapper.like(DwsContentData::getTitle, condition.getTitle());
            if (condition.getStartTime() != null && condition.getEndTime() != null) queryWrapper.between(DwsContentData::getCreateTime, condition.getStartTime(), condition.getEndTime());
        }
        if (condition != null) {
            queryWrapper.eq(DwsContentData::getAccountId, condition.getAccountId());
        }
        queryWrapper.orderByDesc(DwsContentData::getCreateTime);
        return queryWrapper;
    }
}
