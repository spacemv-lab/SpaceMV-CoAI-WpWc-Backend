package com.txwx.webchatcrm.domain.vo;

import lombok.Data;
import java.util.List;

/**
 * 已发布消息列表VO
 */
@Data
public class PublishedArticleListVO {

    /**
     * 成功发布素材的总数
     */
    private Integer totalCount;

    /**
     * 本次调用获取的素材的数量
     */
    private Integer itemCount;

    /**
     * 图文消息条目列表
     */
    private List<PublishedArticleItem> items;

    @Data
    public static class PublishedArticleItem {

        /**
         * 成功发布的图文消息id
         */
        private String articleId;

        /**
         * 图文消息更新时间
         */
        private Long updateTime;

        /**
         * 图文内容列表
         */
        private List<NewsItem> newsItems;
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
        private String contentSourceUrl;

        /**
         * 图文消息的封面图片素材id
         */
        private String thumbMediaId;

        /**
         * 图文消息的封面图片URL
         */
        private String thumbUrl;

        /**
         * 是否打开评论，0不打开(默认)，1打开
         */
        private Integer needOpenComment;

        /**
         * 是否粉丝才可评论，0所有人可评论(默认)，1粉丝才可评论
         */
        private Integer onlyFansCanComment;

        /**
         * 草稿的临时链接
         */
        private String url;

        /**
         * 该图文是否被删除
         */
        private Boolean isDeleted;
    }
}
