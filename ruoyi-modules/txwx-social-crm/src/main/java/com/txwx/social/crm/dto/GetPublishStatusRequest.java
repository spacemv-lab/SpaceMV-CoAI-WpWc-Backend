/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.dto;

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
