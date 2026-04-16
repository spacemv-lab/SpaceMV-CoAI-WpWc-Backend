package com.txwx.social.dashboard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.txwx.social.dashboard.domain.entity.OdsUsers;
import com.txwx.social.dashboard.domain.mapper.OdsUsersMapper;
import com.txwx.social.dashboard.service.IOdsUsersService;
import org.springframework.stereotype.Service;

@Service
public class OdsUsersServiceImpl extends ServiceImpl<OdsUsersMapper, OdsUsers> implements IOdsUsersService {
}
