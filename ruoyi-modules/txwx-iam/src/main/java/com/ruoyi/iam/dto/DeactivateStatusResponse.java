package com.ruoyi.iam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 注销状态响应
 *
 * @author txwx
 */
@Data
@Schema(description = "注销状态响应对象")
public class DeactivateStatusResponse
{
    @Schema(description = "状态：0=非冷静期 1=冷静期中 2=已注销")
    private String status;

    @Schema(description = "提交注销申请时间")
    private Date deleteApplyTime;

    @Schema(description = "预计注销时间")
    private Date deleteScheduledAt;

    @Schema(description = "剩余天数")
    private Long remainingDays;
}
