package com.ruoyi.iam.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 注销申请详情响应 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "注销申请详情VO")
public class LogoutDetailVO {

    @Schema(description = "注销ID（即用户ID）")
    private Long logoutId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "申请注销时间")
    private Date applyTime;

    @Schema(description = "冷却结束时间（预计注销时间）")
    private Date coolEndTime;

    @Schema(description = "状态：0=冷却中 1=已注销")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;
}
