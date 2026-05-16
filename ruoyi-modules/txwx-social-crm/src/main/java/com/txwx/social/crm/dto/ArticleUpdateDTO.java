/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.dto;

import lombok.Data;

import java.util.List;

@Data
public class ArticleUpdateDTO {
    /**
     * media_id(更新草稿时需要)
     */
    private String media_id;

    /**
     * 要更新的文章在图文消息中的位置（多图文消息时，此字段才有意义），第一篇为0
     */
    private Integer index;

    /**
     * 图文素材集合
     */
    private ArticleItemDTO articles;
}
