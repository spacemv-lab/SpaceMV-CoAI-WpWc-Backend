/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.dashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.txwx.social.dashboard.domain.condition.BaseSearchCondition;
import com.txwx.social.dashboard.domain.entity.DwsUsers;
import com.txwx.social.dashboard.mapper.DwsUsersMapper;
import com.txwx.social.dashboard.service.IDwsUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DwsUsersServiceImpl extends ServiceImpl<DwsUsersMapper, DwsUsers> implements IDwsUsersService {

    @Autowired
    private DwsUsersMapper dwsUsersMapper;

    @Override
    public List<DwsUsers> select(BaseSearchCondition condition) {
        LambdaQueryWrapper<DwsUsers> queryWrapper = buildQueryWrapper(condition);

        return dwsUsersMapper.selectList(queryWrapper);
    }

    private LambdaQueryWrapper<DwsUsers> buildQueryWrapper(BaseSearchCondition condition) {
        LambdaQueryWrapper<DwsUsers> queryWrapper = new LambdaQueryWrapper<>();
        if (condition != null) {
            queryWrapper.eq(DwsUsers::getAccountId, condition.getAccountId());
            if (condition.getStartTime() != null && condition.getEndTime() != null) {
                queryWrapper.between(DwsUsers::getRefDate, condition.getStartTime(), condition.getEndTime());
            }
        }

        queryWrapper.orderByDesc(DwsUsers::getRefDate);
        return queryWrapper;
    }

    @Override
    public Page<DwsUsers> selectPage(Integer pageNum, Integer pageSize, BaseSearchCondition condition) {
        Page<DwsUsers> page = new Page<>(pageNum, pageSize);

        // 构建 LambdaQueryWrapper
        LambdaQueryWrapper<DwsUsers> queryWrapper = buildQueryWrapper(condition);

        // 执行分页查询
        return dwsUsersMapper.selectPage(page, queryWrapper);
    }
}
