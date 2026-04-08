package com.txwx.social.crm.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 文章VO
 */
@Data
@Schema(description = "文章VO")
public class ArticleVO {

    @Schema(description = "文章ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "摘要")
    private String digest;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "封面图片素材id")
    private String thumbMediaId;

    @Schema(description = "是否打开评论，0不打开，1打开")
    private Integer needOpenComment;

    @Schema(description = "是否粉丝才可评论，0所有人可评论，1粉丝才可评论")
    private Integer onlyFansCanComment;

    @Schema(description = "文章类型")
    private String articleType;

    @Schema(description = "要更新的文章在图文消息中的位置（多图文消息时，此字段才有意义），第一篇为0")
    private Integer index;

    @Schema(description = "账号ID")
    @NotNull(message = "账号ID不能为空")
    private Long accountId;

}
