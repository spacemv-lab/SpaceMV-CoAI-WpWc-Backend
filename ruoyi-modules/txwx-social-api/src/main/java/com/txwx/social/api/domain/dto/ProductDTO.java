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
import java.util.Map;

/**
 * 产品信息传输对象
 *
 * @author txwx
 * @date 2026-04-04
 */
@Data
@Schema(description = "产品信息传输对象")
public class ProductDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    @Schema(description = "产品基本信息")
    private SimpleProductDTO baseInfo;

    @Schema(description = "渠道信息")
    private List<ChannelDTO> channelDTOList;

    private Map<String, Object> extraInfo;

}
