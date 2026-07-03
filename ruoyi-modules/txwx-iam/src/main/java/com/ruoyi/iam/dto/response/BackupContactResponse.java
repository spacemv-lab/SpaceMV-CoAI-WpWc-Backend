/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 备用联系方式响应 DTO
 *
 * @author txwx
 */
@Data
@Schema(description = "备用联系方式响应对象")
public class BackupContactResponse
{
    @Schema(description = "脱敏后的备用手机号")
    private String bakPhone;

    @Schema(description = "原始备用手机号（未脱敏）")
    private String rawBakPhone;

    @Schema(description = "脱敏后的备用邮箱")
    private String bakEmail;

    @Schema(description = "原始备用邮箱（未脱敏）")
    private String rawBakEmail;

    @Schema(description = "是否有备用手机号")
    private boolean hasBakPhone;

    @Schema(description = "是否有备用邮箱")
    private boolean hasBakEmail;
}
