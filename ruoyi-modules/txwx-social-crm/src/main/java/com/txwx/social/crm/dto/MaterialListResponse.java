/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.dto;

import lombok.Data;

import java.util.List;

/**
 * 永久素材列表响应
 */
@Data
public class MaterialListResponse {
    /**
     * 素材总数
     */
    private Integer total_count;

    /**
     * 本次调用获取的素材数量
     */
    private Integer item_count;

    /**
     * 素材列表
     */
    private List<MaterialItem> item;
}
