package com.txwx.social.crm.domain.po;

import com.ruoyi.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 微信公众号文章实体类
 *
 * @author txwx
 * @date 2025-01-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "微信公众号文章实体")
public class TxwxArticlePO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "微信返回的media_id")
    private String mediaId;

    @Schema(description = "微信返回的article_id(已发布文章)")
    private String articleId;

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

    @Schema(description = "状态：0待提交 1待审核 2审核不通过 3审核通过待发布 4已发布")
    private String status;

    @Schema(description = "提交人")
    private String submitter;

    @Schema(description = "提交人的ID")
    private Long submitterId;

    @Schema(description = "审核人")
    private String reviewer;

    @Schema(description = "发布人")
    private String publisher;

    @Schema(description = "发布任务的id")
    private String publishId;

    @Schema(description = "消息的数据ID")
    private String msgDataId;

    @Schema(description = "删除标志(0代表存在 1代表删除)")
    private String delFlag;

    @Schema(description = "账号ID")
    private Long accountId;
}
