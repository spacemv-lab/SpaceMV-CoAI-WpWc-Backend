package com.txwx.social.crm.domain.content;

import com.ruoyi.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "内容中心发布结果")
public class ContentPublishResult extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "发布结果ID")
    private Long id;

    @Schema(description = "发布任务ID")
    private Long jobId;

    @Schema(description = "文章ID")
    private Long articleId;

    @Schema(description = "发布目标编码")
    private String targetCode;

    @Schema(description = "外部平台ID")
    private String externalId;

    @Schema(description = "外部平台URL")
    private String externalUrl;

    @Schema(description = "站内URL")
    private String siteUrl;

    @Schema(description = "发布请求快照JSON")
    private String payloadJson;

    @Schema(description = "发布结果JSON")
    private String resultJson;

    @Schema(description = "查询开始时间")
    private Date beginTime;

    @Schema(description = "查询结束时间")
    private Date endTime;

    @Schema(description = "页码")
    private Integer pageNum;

    @Schema(description = "每页条数")
    private Integer pageSize;

    @Schema(description = "删除标志")
    private String delFlag;
}
