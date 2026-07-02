package com.txwx.social.crm.domain.content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Schema(description = "站内发布响应")
public class ContentPublishSiteResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "文章ID")
    private Long articleId;

    @Schema(description = "文章发布后状态")
    private String articleStatus;

    @Schema(description = "规范URL")
    private String canonicalUrl;

    @Schema(description = "发布时间")
    private Date publishedAt;

    @Schema(description = "发布任务ID")
    private Long jobId;

    @Schema(description = "发布结果ID")
    private Long resultId;

    @Schema(description = "发布目标编码")
    private String targetCode;

    @Schema(description = "站内URL")
    private String siteUrl;

    @Schema(description = "外部URL")
    private String externalUrl;
}
