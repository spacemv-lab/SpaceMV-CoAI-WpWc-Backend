package com.txwx.social.dashboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.txwx.social.dashboard.domain.entity.DwsBizsummaryChannelDaily;
import com.txwx.social.dashboard.domain.entity.FlowSource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DwsBizsummaryChannelDailyMapper extends BaseMapper<DwsBizsummaryChannelDaily> {

    int insertBatch(@Param("list") List<DwsBizsummaryChannelDaily> list);

    List<FlowSource> selectSource(@Param("accountId") Long accountId);

    List<Map<String, Object>> selectDataBoardReadSource(@Param("accountId") Long accountId);

}
