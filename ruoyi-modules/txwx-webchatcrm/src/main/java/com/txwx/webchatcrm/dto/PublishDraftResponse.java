package com.txwx.webchatcrm.dto;

import lombok.Data;

/**
 * 发布草稿响应
 */
@Data
public class PublishDraftResponse {
    /**
     * 错误码
     */
    private Integer errcode;

    /**
     * 错误信息
     */
    private String errmsg;

    /**
     * 发布任务的id
     */
    private String publish_id;

    /**
     * 消息的数据ID
     */
    private String msg_data_id;
}
