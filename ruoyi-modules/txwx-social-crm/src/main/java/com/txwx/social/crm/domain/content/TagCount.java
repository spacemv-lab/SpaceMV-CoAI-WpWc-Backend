package com.txwx.social.crm.domain.content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "文章标签计数")
public class TagCount {

    @Schema(description = "标签名称")
    private String tagName;

    @Schema(description = "文章数量")
    private Integer articleCount;
}
