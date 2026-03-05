package com.txwx.webchat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.txwx.webchat.domain.entity.OdsUsers;
import com.txwx.webchat.mapper.OdsUsersMapper;
import com.txwx.webchat.service.IOdsUsersService;
import org.springframework.stereotype.Service;

@Service
public class OdsUsersServiceImpl extends ServiceImpl<OdsUsersMapper, OdsUsers> implements IOdsUsersService {
}
