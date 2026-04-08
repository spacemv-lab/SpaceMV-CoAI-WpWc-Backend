package com.txwx.social.crm.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 草稿详情VO
 */
@Data
@Schema(description = "草稿详情VO")
public class ArticleDetailVO {

    @Schema(description = "文章类型")
    private String articleType;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "摘要")
    private String digest;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "图文消息的原文地址")
    private String contentSourceUrl;

    @Schema(description = "图文消息封面序号")
    private Integer showCoverPic;

    @Schema(description = "图文消息的封面图片素材id")
    private String thumbMediaId;

    @Schema(description = "图文消息封面链接")
    private String thumbUrl;

    @Schema(description = "草稿的临时链接")
    private String url;

    @Schema(description = "是否打开评论，0不打开(默认)，1打开")
    private Integer needOpenComment;

    @Schema(description = "是否粉丝才可评论，0所有人可评论(默认)，1粉丝才可评论")
    private Integer onlyFansCanComment;
}
