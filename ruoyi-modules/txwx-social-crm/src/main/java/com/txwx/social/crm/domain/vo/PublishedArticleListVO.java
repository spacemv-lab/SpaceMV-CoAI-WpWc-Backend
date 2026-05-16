/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

/**
 * 已发布消息列表VO
 */
@Data
@Schema(description = "已发布消息列表VO")
public class PublishedArticleListVO {

    @Schema(description = "成功发布素材的总数")
    private Integer totalCount;

    @Schema(description = "本次调用获取的素材的数量")
    private Integer itemCount;

    @Schema(description = "图文消息条目列表")
    private List<PublishedArticleItem> items;

    @Data
    @Schema(description = "已发布文章项")
    public static class PublishedArticleItem {

        @Schema(description = "成功的图文消息id")
        private String articleId;

        @Schema(description = "图文消息更新时间")
        private Long updateTime;

        @Schema(description = "图文内容列表")
        private List<NewsItem> newsItems;
    }

    @Data
    @Schema(description = "图文消息项")
    public static class NewsItem {

        @Schema(description = "标题")
        private String title;

        @Schema(description = "作者")
        private String author;

        @Schema(description = "摘要")
        private String digest;

        @Schema(description = "图文消息的具体内容")
        private String content;

        @Schema(description = "图文消息的原文地址")
        private String contentSourceUrl;

        @Schema(description = "图文消息的封面图片素材id")
        private String thumbMediaId;

        @Schema(description = "图文消息的封面图片URL")
        private String thumbUrl;

        @Schema(description = "是否打开评论，0不打开(默认)，1打开")
        private Integer needOpenComment;

        @Schema(description = "是否粉丝才可评论，0所有人可评论(默认)，1粉丝才可评论")
        private Integer onlyFansCanComment;

        @Schema(description = "草稿的临时链接")
        private String url;

        @Schema(description = "该图文是否被删除")
        private Boolean isDeleted;
    }
}
