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
 * 文章DTO(用于调用微信API)
 */
@Data
public class ArticleDTO {

    /**
     * media_id(更新草稿时需要)
     */
    private String media_id;

    /**
     * 图文素材集合
     */
    private List<ArticleItemDTO> articles;
}
