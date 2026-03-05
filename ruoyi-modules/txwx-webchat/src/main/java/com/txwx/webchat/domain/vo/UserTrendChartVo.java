package com.txwx.webchat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserTrendChartVo {

    @Schema(description = "日期")
    private String refDate;

    @Schema(description = "数量")
    private Integer quantity;
}
