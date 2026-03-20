package com.txwx.webchat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.txwx.webchat.domain.entity.DwsContentData;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DwsContentDataMapper extends BaseMapper<DwsContentData> {

    void truncateDwsContentData();

    void aggregateArticleDetailsDataToDws();
}
