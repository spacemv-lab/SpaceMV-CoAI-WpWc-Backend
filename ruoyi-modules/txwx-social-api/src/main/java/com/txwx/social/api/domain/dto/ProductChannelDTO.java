/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.api.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode
@Schema(description = "产品渠道关联表")
public class ProductChannelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "产品ID")
    private Long productId;

    @Schema(description = "渠道ID")
    private Long channelId;

    @Schema(description = "删除标志(0代表存在 1代表删除)")
    private String delFlag;
}
