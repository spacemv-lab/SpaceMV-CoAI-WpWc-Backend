package com.txwx.webchat.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.txwx.webchat.domain.entity.mysql.UserPlatform;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DS("slave")
public interface UserPlatformMapper extends BaseMapper<UserPlatform> {
}
