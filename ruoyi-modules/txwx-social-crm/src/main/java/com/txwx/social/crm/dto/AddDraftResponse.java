/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.dto;

import lombok.Data;

/**
 * 新增草稿响应
 */
@Data
public class AddDraftResponse {
    /**
     * 上传后的获取标志(不超过128字符)
     */
    private String media_id;

    /**
     * 错误码
     */
    private Integer errcode;

    /**
     * 错误信息
     */
    private String errmsg;
}
