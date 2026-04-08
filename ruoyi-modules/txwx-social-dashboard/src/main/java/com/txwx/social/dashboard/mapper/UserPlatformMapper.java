package com.txwx.social.dashboard.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.txwx.social.dashboard.domain.entity.mysql.UserPlatform;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DS("slave")
public interface UserPlatformMapper extends BaseMapper<UserPlatform> {
}
