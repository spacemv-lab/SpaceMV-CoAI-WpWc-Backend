package com.txwx.social.crm.domain.content;

import com.ruoyi.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 文章平台发布记录（多平台发布设计v3）。
 * 每条记录 = 一篇文章在某平台上的发布状态。
 * 合并 content_publish_job + content_publish_result 功能。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "文章平台发布记录")
public class ArticlePublish extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "文章ID")
    private Long articleId;

    @Schema(description = "平台编码: SITE_WENDAO / WECHAT_OFFICIAL_ACCOUNT / ZHISHU_XINGQIU ...")
    private String platform;

    @Schema(description = "该平台标题（NULL=使用主标题）")
    private String platformTitle;

    @Schema(description = "该平台标签")
    private String platformTags;

    @Schema(description = "发布时使用的样式预设名")
    private String stylePreset;

    @Schema(description = "发布状态: PENDING / PUBLISHING / SUCCESS / FAILED / DRAFT_CREATED / DELETED")
    private String status;

    @Schema(description = "发出去时的 contentJson 快照（仅用于历史预览）")
    private String contentJsonSnapshot;

    @Schema(description = "平台上的文章 ID")
    private String platformArticleId;

    @Schema(description = "平台上的链接")
    private String platformUrl;

    @Schema(description = "错误码")
    private String errorCode;

    @Schema(description = "错误描述")
    private String errorMessage;

    @Schema(description = "重试次数")
    private Integer retryCount;

    @Schema(description = "有效标记: 1=有效 0=已删除")
    private String isActive;

    @Schema(description = "成功发布时间")
    private Date publishedAt;

    @Schema(description = "删除标志")
    private String delFlag;

    // ========== 以下为非持久化查询辅助字段 ==========

    @Schema(description = "文章标题（关联查询）")
    private String articleTitle;

    @Schema(description = "文章状态（关联查询）")
    private String articleStatus;
}
