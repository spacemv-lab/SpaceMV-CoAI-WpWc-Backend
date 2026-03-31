package com.txwx.webchat.domain.condition;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContentDataSearchCondition extends BaseSearchCondition{

    @Schema(description = "标题")
    private String title;

    @Schema(description = "产品ID")
    private Long productId;

    @Schema(description = "平台ID")
    private Long platformId;
}
