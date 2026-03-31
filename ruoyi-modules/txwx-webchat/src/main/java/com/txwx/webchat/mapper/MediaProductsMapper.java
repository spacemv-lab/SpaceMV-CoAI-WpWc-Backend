package com.txwx.webchat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.txwx.webchat.domain.entity.mysql.MediaProduct;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MediaProductsMapper extends BaseMapper<MediaProduct> {
}
