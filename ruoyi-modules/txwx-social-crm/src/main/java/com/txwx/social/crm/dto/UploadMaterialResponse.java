/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.dto;

import lombok.Data;

/**
 * 上传永久素材响应
 */
@Data
public class UploadMaterialResponse {
    /**
     * 新增的永久素材media_id
     */
    private String media_id;

    /**
     * 图片URL
     */
    private String url;

    /**
     * 错误码
     */
    private int errcode;

    /**
     * 错误信息
     */
    private String errmsg;
}
