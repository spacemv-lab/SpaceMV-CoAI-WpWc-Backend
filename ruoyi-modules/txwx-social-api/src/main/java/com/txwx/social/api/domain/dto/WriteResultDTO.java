package com.txwx.social.api.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 写入结果DTO
 *
 * @author txwx
 * @date 2026-04-03
 */
@Data
@Builder
@Schema(description = "同步结果传输对象")
public class WriteResultDTO {

    /**
     * 是否成功
     */
    @Schema(description = "是否成功")
    private boolean success;

    /**
     * 写入条数
     */
    @Schema(description = "写入条数")
    private int count;

    /**
     * 错误信息
     */
    @Schema(description = "错误信息")
    private String errorMessage;

    /**
     * 账号ID
     */
    @Schema(description = "账号id")
    private Long accountId;
}
