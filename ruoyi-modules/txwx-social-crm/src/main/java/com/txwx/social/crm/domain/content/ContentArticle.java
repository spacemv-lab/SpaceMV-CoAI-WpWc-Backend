package com.txwx.social.crm.domain.content;

import com.ruoyi.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 内容中心文章主数据。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "内容中心文章")
public class ContentArticle extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "文章ID")
    private Long id;

    @NotBlank(message = "文章标题不能为空")
    @Schema(description = "文章标题")
    private String title;

    @NotBlank(message = "文章访问标识不能为空")
    @Schema(description = "文章访问标识")
    private String slug;

    @Schema(description = "文章摘要")
    private String summary;

    @Schema(description = "封面图URL")
    private String coverUrl;

    @Schema(description = "文章标签（逗号分隔，如\"宏观经济,市场分析\"）")
    private String tags;

    @Schema(description = "作者ID")
    private Long authorId;

    @Schema(description = "作者名称")
    private String authorName;

    @Schema(description = "HTML正文")
    private String contentHtml;

    @Schema(description = "Markdown正文")
    private String contentMarkdown;

    @Schema(description = "结构化正文JSON")
    private String contentJson;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "可见性")
    private String visibility;

    @Schema(description = "是否付费文章，第一阶段使用字符串0/1")
    private String isPaid;

    @Schema(description = "SEO标题")
    private String seoTitle;

    @Schema(description = "SEO描述")
    private String seoDescription;

    @Schema(description = "SEO关键词")
    private String seoKeywords;

    @Schema(description = "规范URL")
    private String canonicalUrl;

    @Schema(description = "发布时间")
    private Date publishedAt;

    @Schema(description = "发布时间开始")
    private Date beginPublishedAt;

    @Schema(description = "发布时间结束")
    private Date endPublishedAt;

    @Schema(description = "页码")
    private Integer pageNum;

    @Schema(description = "每页条数")
    private Integer pageSize;

    @Schema(description = "微信公众号草稿 media_id")
    private String wechatMediaId;

    @Schema(description = "微信公众号账号ID")
    private Long wechatAccountId;

    @Schema(description = "删除标志")
    private String delFlag;
}
