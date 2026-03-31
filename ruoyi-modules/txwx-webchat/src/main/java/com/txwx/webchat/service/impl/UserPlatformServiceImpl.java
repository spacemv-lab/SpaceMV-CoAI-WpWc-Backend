package com.txwx.webchat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.txwx.webchat.domain.entity.mysql.UserPlatform;
import com.txwx.webchat.mapper.UserPlatformMapper;
import com.txwx.webchat.service.IUserPlatformService;
import org.springframework.stereotype.Service;

@Service
public class UserPlatformServiceImpl extends ServiceImpl<UserPlatformMapper, UserPlatform> implements IUserPlatformService {
}
