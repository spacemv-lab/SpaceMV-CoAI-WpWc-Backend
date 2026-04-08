package com.txwx.social.dashboard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.txwx.social.dashboard.domain.entity.mysql.UserPlatform;
import com.txwx.social.dashboard.mapper.UserPlatformMapper;
import com.txwx.social.dashboard.service.IUserPlatformService;
import org.springframework.stereotype.Service;

@Service
public class UserPlatformServiceImpl extends ServiceImpl<UserPlatformMapper, UserPlatform> implements IUserPlatformService {
}
