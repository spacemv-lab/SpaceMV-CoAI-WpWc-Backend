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
