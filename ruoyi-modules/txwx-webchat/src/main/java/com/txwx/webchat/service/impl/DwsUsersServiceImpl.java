package com.txwx.webchat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.txwx.webchat.domain.entity.DwsUsers;
import com.txwx.webchat.mapper.DwsUsersMapper;
import com.txwx.webchat.service.IDwsUsersService;
import org.springframework.stereotype.Service;

@Service
public class DwsUsersServiceImpl extends ServiceImpl<DwsUsersMapper, DwsUsers> implements IDwsUsersService {
}
