package com.txwx.social.dashboard.domain.condition;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserDataSearchCondition extends BaseSearchCondition{

    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "平台id")
    private Long platformId;
}
