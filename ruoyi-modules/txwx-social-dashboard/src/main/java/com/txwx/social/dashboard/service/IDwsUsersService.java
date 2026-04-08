package com.txwx.social.dashboard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.txwx.social.dashboard.domain.common.PageRequest;
import com.txwx.social.dashboard.domain.common.PageResult;
import com.txwx.social.dashboard.domain.condition.UserDataSearchCondition;
import com.txwx.social.dashboard.domain.entity.DwsUsers;

public interface IDwsUsersService extends IService<DwsUsers> {
    PageResult<DwsUsers> select(PageRequest pageRequest, UserDataSearchCondition condition);
}
