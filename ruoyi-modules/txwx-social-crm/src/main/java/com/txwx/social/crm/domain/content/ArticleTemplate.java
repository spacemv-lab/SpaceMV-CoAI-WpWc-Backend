package com.txwx.social.crm.domain.content;

import com.ruoyi.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章模板。
 * 模板 = 内容骨架 + 样式预设名 + 默认平台。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "文章模板")
public class ArticleTemplate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "模板ID")
    private Long id;

    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "模板分类")
    private String category;

    @Schema(description = "默认目标平台编码")
    private String platform;

    @Schema(description = "默认样式预设名称")
    private String stylePreset;

    @Schema(description = "ProseMirror JSON 内容快照")
    private String snapshotJson;

    @Schema(description = "缩略图")
    private String thumbnailUrl;

    @Schema(description = "模板说明")
    private String description;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否默认模板：0否 1是")
    private String isDefault;

    @Schema(description = "删除标志")
    private String delFlag;
}
