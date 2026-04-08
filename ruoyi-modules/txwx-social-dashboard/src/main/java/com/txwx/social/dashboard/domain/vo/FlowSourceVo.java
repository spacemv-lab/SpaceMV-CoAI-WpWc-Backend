package com.txwx.social.dashboard.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FlowSourceVo {

    @Schema(description = "渠道")
    @ExcelProperty("渠道")
    private String channel;

    @Schema(description = "阅读数")
    @ExcelProperty("阅读数")
    private Long readUser;

    @Schema(description = "占比")
    @ExcelProperty("占比")
    private String proportion;
}
