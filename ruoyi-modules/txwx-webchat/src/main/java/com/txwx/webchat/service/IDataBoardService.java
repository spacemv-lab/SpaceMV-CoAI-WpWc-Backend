package com.txwx.webchat.service;

import com.txwx.webchat.domain.vo.UserTotalVo;
import com.txwx.webchat.domain.vo.UserTrendChartVo;
import java.util.List;
import java.util.Map;

public interface IDataBoardService {
    List<Map<String, Object>> netUserTrend(Integer filterDimension, Long productId,  Long platformId);

    List<Map<String, Object>> accumulatedUserTrend(Integer filterDimension, Long productId,  Long platformId);

    List<UserTotalVo> selectUserTotal(Long productId, Long platformId);

    List<List<Map<String, Object>>> readFlowTrend(Integer filterDimension, Long productId,  Long platformId);

    List<UserTotalVo> optimumReaderShareNum(Long productId, Long platformId);

    List<Map<String, Object>> subscribeUserAfterRead(Long productId, Long platformId);
}
