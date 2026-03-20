package com.txwx.webchat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.txwx.webchat.domain.entity.DwsBizsummaryChannelDaily;
import com.txwx.webchat.domain.vo.FlowSourceVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DwsBizsummaryChannelDailyMapper extends BaseMapper<DwsBizsummaryChannelDaily> {

    int insertBatch(@Param("list") List<DwsBizsummaryChannelDaily> list);

    List<FlowSourceVo> selectSource();

    Map<String, Long> selectTotalReadShare();

    List<Map<String, Object>> selectDataBoardReadSource();
}
