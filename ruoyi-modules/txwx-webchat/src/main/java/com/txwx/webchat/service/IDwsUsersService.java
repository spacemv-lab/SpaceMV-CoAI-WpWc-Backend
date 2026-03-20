package com.txwx.webchat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.txwx.webchat.domain.common.PageRequest;
import com.txwx.webchat.domain.common.PageResult;
import com.txwx.webchat.domain.condition.UserDataSearchCondition;
import com.txwx.webchat.domain.entity.DwsUsers;

public interface IDwsUsersService extends IService<DwsUsers> {
    PageResult<DwsUsers> select(PageRequest pageRequest, UserDataSearchCondition condition);
}
