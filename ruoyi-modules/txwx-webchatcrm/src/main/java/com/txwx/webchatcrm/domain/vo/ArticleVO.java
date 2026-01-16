package com.txwx.webchatcrm.domain.vo;

import lombok.Data;

/**
 * 文章VO
 */
@Data
public class ArticleVO {

    /**
     * 文章ID
     */
    private Long id;

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
    private String thumbMediaId;

    /**
     * 是否打开评论，0不打开，1打开
     */
    private Integer needOpenComment;

    /**
     * 是否粉丝才可评论，0所有人可评论，1粉丝才可评论
     */
    private Integer onlyFansCanComment;

    /**
     * 文章类型
     */
    private String articleType;

    /**
     * 要更新的文章在图文消息中的位置（多图文消息时，此字段才有意义），第一篇为0
     */
    private Integer index;
}
