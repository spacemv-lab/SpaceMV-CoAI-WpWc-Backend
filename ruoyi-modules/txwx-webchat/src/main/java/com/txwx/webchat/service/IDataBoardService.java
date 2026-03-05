package com.txwx.webchat.service;

import com.txwx.webchat.domain.vo.UserTotalVo;
import com.txwx.webchat.domain.vo.UserTrendChartVo;
import java.util.List;
import java.util.Map;

public interface IDataBoardService {
    List<Map<String, Object>> netUserTrend(Integer filterDimension);

    List<Map<String, Object>> accumulatedUserTrend(Integer filterDimension);

    List<UserTotalVo> selectUserTotal();

    List<List<Map<String, Object>>> readFlowTrend(Integer filterDimension);

    List<UserTotalVo> optimumReaderShareNum();

    List<Map<String, Object>> subscribeUserAfterRead();
}
