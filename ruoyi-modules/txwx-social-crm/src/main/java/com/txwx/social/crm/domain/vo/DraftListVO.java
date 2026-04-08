package com.txwx.social.crm.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

/**
 * 草稿列表VO
 */
@Data
@Schema(description = "草稿列表VO")
public class DraftListVO {

    @Schema(description = "草稿素材的总数")
    private Integer totalCount;

    @Schema(description = "本次调用获取的素材的数量")
    private Integer itemCount;

    @Schema(description = "图文消息条目列表")
    private List<DraftItem> items;

    @Data
    @Schema(description = "草稿项")
    public static class DraftItem {

        @Schema(description = "图文消息的id")
        private String mediaId;

        @Schema(description = "图文消息更新时间")
        private Long updateTime;

        @Schema(description = "图文内容列表")
        private List<NewsItem> newsItems;
    }

    @Data
    @Schema(description = "图文消息项")
    public static class NewsItem {

        @Schema(description = "文章类型，分别有图文消息（news）、图片消息（newspic），不填默认为图文消息（news）")
        private String articleType;

        @Schema(description = "标题")
        private String title;

        @Schema(description = "作者")
        private String author;

        @Schema(description = "图文消息的摘要，仅有单图文消息才有摘要，多图文此处为空")
        private String digest;

        @Schema(description = "图文消息的具体内容，支持HTML标签")
        private String content;

        @Schema(description = "图文消息的原文地址，即点击\"阅读原文\"后的URL")
        private String contentSourceUrl;

        @Schema(description = "是否显示封面，0为false，即不显示，1为true，即显示")
        private String showCoverPic;

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
    }
}
