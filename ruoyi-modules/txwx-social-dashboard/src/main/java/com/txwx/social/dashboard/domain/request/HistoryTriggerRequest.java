/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.dashboard.domain.request;

import lombok.Data;

import java.util.List;

@Data
public class HistoryTriggerRequest {

    /**
     * YYYY-MM-dd
     */
    private String startdate;

    /**
     * YYYY-MM-dd
     */
    private String enddate;

    private List<Long> accountIds;
}
