/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.dashboard.service;


import com.txwx.social.dashboard.domain.vo.UserTotalVo;

import java.util.List;
import java.util.Map;

public interface IDataBoardService {
    List<Map<String, Object>> netUserTrend(Integer filterDimension, Long accountId);

    List<Map<String, Object>> accumulatedUserTrend(Integer filterDimension, Long accountId);

    List<UserTotalVo> selectUserTotal(Long accountId);

    List<List<Map<String, Object>>> readFlowTrend(Integer filterDimension, Long accountId);

    List<UserTotalVo> optimumReaderShareNum(Long accountId);

    List<Map<String, Object>> subscribeUserAfterRead(Long accountId);
}
