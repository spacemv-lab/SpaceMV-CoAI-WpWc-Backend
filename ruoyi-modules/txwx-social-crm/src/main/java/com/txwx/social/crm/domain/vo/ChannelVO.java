/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 渠道视图对象
 *
 * @author txwx
 * @date 2026-04-03
 */
@Data
@Schema(description = "渠道视图对象")
public class ChannelVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "渠道名称")
    private String channelName;

    @Schema(description = "渠道类型（wechat, douyin, xiaohongshu）")
    private String channelType;

    @Schema(description = "渠道描述")
    private String channelDesc;
}
