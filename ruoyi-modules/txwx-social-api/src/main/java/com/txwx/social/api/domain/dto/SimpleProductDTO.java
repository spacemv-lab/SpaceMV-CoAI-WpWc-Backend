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

/**
 * 产品信息传输对象
 *
 * @author txwx
 * @date 2026-04-04
 */
@Data
@Schema(description = "产品信息传输对象")
public class SimpleProductDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "产品ID")
    private Long id;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "产品编码")
    private String productCode;

    @Schema(description = "产品描述")
    private String productDesc;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "修改时间")
    private Date updateTime;
}
