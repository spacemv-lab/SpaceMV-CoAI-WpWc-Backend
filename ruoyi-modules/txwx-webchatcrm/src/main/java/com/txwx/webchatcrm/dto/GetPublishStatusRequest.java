package com.txwx.webchatcrm.dto;

import lombok.Data;

/**
 * 查询发布状态请求
 */
@Data
public class GetPublishStatusRequest {

    /**
     * 发布任务id
     */
    private String publish_id;
}
