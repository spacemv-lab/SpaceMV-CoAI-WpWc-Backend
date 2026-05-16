/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户协作权限分配视图对象
 *
 * @author txwx
 * @date 2026-04-03
 */
@Data
@Schema(description = "用户协作权限分配视图对象")
public class UserPermissionVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "关系类型：0-系统级 1-产品级 2-渠道级")
    private Integer relationType;

    @Schema(description = "关系ID列表（JSON 数组）")
    private List<Long> relationIds;
}
