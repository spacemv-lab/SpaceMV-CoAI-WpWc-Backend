/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.api.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 同步响应DTO
 *
 * @author txwx
 * @date 2026-04-06
 */
@Data
@Schema(description = "同步响应传输对象")
public class SyncResponseDTO {

    @Schema(description = "是否成功")
    private boolean success;

    /**
     * 同步条数
     */
    @Schema(description = "同步条数")
    private Integer syncCount;

    /**
     * 错误信息
     */
    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "执行时间")
    private Long executeTime;
}
