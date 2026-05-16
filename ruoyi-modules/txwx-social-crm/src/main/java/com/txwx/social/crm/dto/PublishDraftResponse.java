/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.dto;

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
