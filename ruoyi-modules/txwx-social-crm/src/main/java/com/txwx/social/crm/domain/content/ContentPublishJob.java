package com.txwx.social.crm.domain.content;

import com.ruoyi.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "内容中心发布任务")
public class ContentPublishJob extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "发布任务ID")
    private Long id;

    @Schema(description = "文章ID")
    private Long articleId;

    @Schema(description = "发布目标编码")
    private String targetCode;

    @Schema(description = "发布任务状态")
    private String status;

    @Schema(description = "计划发布时间")
    private Date scheduledAt;

    @Schema(description = "开始时间")
    private Date startedAt;

    @Schema(description = "结束时间")
    private Date finishedAt;

    @Schema(description = "操作人ID")
    private Long operatorId;

    @Schema(description = "操作人名称")
    private String operatorName;

    @Schema(description = "失败原因")
    private String errorMessage;

    @Schema(description = "文章标题，发布记录页展示字段")
    private String articleTitle;

    @Schema(description = "文章访问标识，发布记录页展示字段")
    private String articleSlug;

    @Schema(description = "文章当前状态，来自content_article.status")
    private String articleStatus;

    @Schema(description = "文章删除标志，来自content_article.del_flag")
    private String articleDelFlag;

    @Schema(description = "站内URL，发布记录页展示字段")
    private String siteUrl;

    @Schema(description = "外部平台URL，发布记录页展示字段")
    private String externalUrl;

    @Schema(description = "发布结果ID，发布记录页展示字段")
    private Long resultId;

    @Schema(description = "查询开始时间")
    private Date beginTime;

    @Schema(description = "查询结束时间")
    private Date endTime;

    @Schema(description = "页码")
    private Integer pageNum;

    @Schema(description = "每页条数")
    private Integer pageSize;

    @Schema(description = "发布任务删除标志")
    private String delFlag;
}
