/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.api.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 产品信息传输对象
 *
 * @author txwx
 * @date 2026-04-04
 */
@Data
@Schema(description = "用户产品协作传输DTO")
public class UserPermissionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "关系类型：0-系统级 1-产品级 2-渠道级")
    private Integer relationType;

    @Schema(description = "关系ID列表（JSON 数组）")
    private String relationIds;
}
