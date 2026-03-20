package com.txwx.webchat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserTotalVo {

    @Schema(description = "描述")
    private String desc;

    @Schema(description = "数量")
    private Long value;
}
