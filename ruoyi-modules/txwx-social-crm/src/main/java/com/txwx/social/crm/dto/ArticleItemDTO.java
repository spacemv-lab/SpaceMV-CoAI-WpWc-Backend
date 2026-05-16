/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.dto;

import lombok.Data;

/**
 * 文章项DTO
 */
@Data
public class ArticleItemDTO {

    /**
     * 文章类型,固定填news
     */
    private String article_type;

    /**
     * 标题
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * 摘要
     */
    private String digest;

    /**
     * 内容
     */
    private String content;

    /**
     * 封面图片素材id
     */
    private String thumb_media_id;

    /**
     * 是否打开评论，0不打开(默认)，1打开
     */
    private Integer need_open_comment;

    /**
     * 是否粉丝才可评论，0所有人可评论(默认)，1粉丝才可评论
     */
    private Integer only_fans_can_comment;
}
