/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.dto;

import lombok.Data;

/**
 * 永久素材项
 */
@Data
public class MaterialItem {
    /**
     * 消息
     */
    private String media_id;

    /**
     * 图片的名字
     */
    private String name;

    /**
     * 更新日期
     */
    private String update_time;

    /**
     * 图片的URL
     */
    private String url;
}
