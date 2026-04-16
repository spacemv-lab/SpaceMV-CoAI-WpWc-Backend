package com.txwx.social.dashboard.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.txwx.social.dashboard.domain.condition.BaseSearchCondition;
import com.txwx.social.dashboard.domain.entity.DwsUsers;

import java.util.List;

public interface IDwsUsersService extends IService<DwsUsers> {
    List<DwsUsers> select(BaseSearchCondition condition);

    Page<DwsUsers> selectPage(Integer pageNum, Integer pageSize, BaseSearchCondition condition);
}
