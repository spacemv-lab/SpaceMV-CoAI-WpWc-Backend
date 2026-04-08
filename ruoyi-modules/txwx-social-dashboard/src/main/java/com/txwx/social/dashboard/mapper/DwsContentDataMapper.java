package com.txwx.social.dashboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.txwx.social.dashboard.domain.entity.DwsContentData;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DwsContentDataMapper extends BaseMapper<DwsContentData> {

    void truncateDwsContentData();

    void aggregateArticleDetailsDataToDws();
}
