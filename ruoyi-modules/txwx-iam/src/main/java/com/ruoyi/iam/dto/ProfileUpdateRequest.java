/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 资料更新请求
 *
 * @author txwx
 */
@Data
@Schema(description = "用户资料更新请求对象")
public class ProfileUpdateRequest
{
    @Schema(description = "显示名")
    private String displayName;

    @Schema(description = "头像 URL")
    private String avatarUrl;
}
