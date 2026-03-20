package com.txwx.webchat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.txwx.webchat.domain.common.PageRequest;
import com.txwx.webchat.domain.common.PageResult;
import com.txwx.webchat.domain.condition.UserDataSearchCondition;
import com.txwx.webchat.domain.entity.DwsUsers;
import com.txwx.webchat.mapper.DwsUsersMapper;
import com.txwx.webchat.service.IDwsUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DwsUsersServiceImpl extends ServiceImpl<DwsUsersMapper, DwsUsers> implements IDwsUsersService {

    @Autowired
    private DwsUsersMapper dwsUsersMapper;

    @Override
    public PageResult<DwsUsers> select(PageRequest pageRequest, UserDataSearchCondition condition) {
        PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
        LambdaQueryWrapper<DwsUsers> queryWrapper = new LambdaQueryWrapper<>();
        if (condition != null) {
            if (condition.getStartTime() != null && condition.getEndTime() != null) {
                queryWrapper.between(DwsUsers::getRefDate, condition.getStartTime(), condition.getEndTime());
            }
        }
        queryWrapper.orderByDesc(DwsUsers::getRefDate);
        List<DwsUsers> res = dwsUsersMapper.selectList(queryWrapper);
        PageInfo<DwsUsers> pageInfo = new PageInfo<>(res);
        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getList());
    }
}
