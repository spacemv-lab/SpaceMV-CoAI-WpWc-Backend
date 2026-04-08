package com.txwx.social.crm.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

/**
 * 发布状态VO
 */
@Data
@Schema(description = "发布状态VO")
public class PublishStatusVO {

    @Schema(description = "发布任务id")
    private String publishId;

    @Schema(description = "发布状态(0:成功,1:发布中,2:原创失败,3:常规失败,4:平台审核不通过,5:成功后用户删除所有文章,6:成功后系统封禁所有文章)")
    private Integer publishStatus;

    @Schema(description = "发布状态描述")
    private String publishStatusDesc;

    @Schema(description = "成功时的图文article_id")
    private String articleId;

    @Schema(description = "文章数量")
    private Integer count;

    @Schema(description = "文章详情列表")
    private List<ArticleItem> articleItems;

    @Schema(description = "失败文章编号")
    private List<Integer> failIdx;

    @Data
    @Schema(description = "文章详情项")
    public static class ArticleItem {

        @Schema(description = "文章对应的编号")
        private Integer idx;

        @Schema(description = "图文的永久链接")
        private String articleUrl;
    }
}
