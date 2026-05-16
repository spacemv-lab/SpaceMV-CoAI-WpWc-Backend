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
 * 获取已发布消息列表响应
 */
@Data
public class GetPublishedListResponse {

    /**
     * 成功发布素材的总数
     */
    private Integer total_count;

    /**
     * 本次调用获取的素材的数量
     */
    private Integer item_count;

    /**
     * 图文消息条目列表
     */
    private List<PublishedItem> item;

    /**
     * 错误码
     */
    private Integer errcode;

    /**
     * 错误信息
     */
    private String errmsg;

    @Data
    public static class PublishedItem {

        /**
         * 成功发布的图文消息id
         */
        private String article_id;

        /**
         * 图文消息内容
         */
        private Content content;

        /**
         * 图文消息更新时间
         */
        private Long update_time;
    }

    @Data
    public static class Content {

        /**
         * 图文内容列表
         */
        private List<NewsItem> news_item;
    }

    @Data
    public static class NewsItem {

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
         * 图文消息的具体内容
         */
        private String content;

        /**
         * 图文消息的原文地址
         */
        private String content_source_url;

        /**
         * 图文消息的封面图片素材id
         */
        private String thumb_media_id;

        /**
         * 图文消息的封面图片URL
         */
        private String thumb_url;

        /**
         * 是否打开评论
         */
        private Integer need_open_comment;

        /**
         * 是否粉丝才可评论
         */
        private Integer only_fans_can_comment;

        /**
         * 草稿的临时链接
         */
        private String url;

        /**
         * 该图文是否被删除
         */
        private Boolean is_deleted;
    }
}
