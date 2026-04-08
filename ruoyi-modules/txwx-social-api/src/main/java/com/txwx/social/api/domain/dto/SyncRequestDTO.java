package com.txwx.social.api.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 同步请求DTO
 *
 * @author txwx
 * @date 2026-04-06
 */
@Data
@Schema(description = "同步请求传输对象")
public class SyncRequestDTO {

    @Schema(description = "任务ID")
    private Long taskId;

    @Schema(description = "渠道类型：如wechat")
    private String channelType;

    @Schema(description = "产品ID")
    private Long productId;

    @Schema(description = "渠道ID")
    private Long channelId;

    @Schema(description = "账号ID")
    private Long accountId;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;
}
