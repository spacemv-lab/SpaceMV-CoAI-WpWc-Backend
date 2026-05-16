package com.txwx.social.dashboard.domain.condition;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FlowSearchCondition extends BaseSearchCondition{

    @Schema(description = "渠道")
    private List<String> channel;
}
