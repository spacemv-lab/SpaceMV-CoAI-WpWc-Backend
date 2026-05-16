/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.dto;

import lombok.Data;

/**
 * 获取草稿详情请求
 */
@Data
public class GetDraftDetailRequest {

    /**
     * 要获取的草稿的media_id
     */
    private String media_id;
}
