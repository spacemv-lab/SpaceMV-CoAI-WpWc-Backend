package com.txwx.social.api.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 渠道信息传输对象
 *
 * @author txwx
 * @date 2026-04-04
 */
@Data
@Schema(description = "渠道信息传输对象")
public class SimpleChannelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "渠道ID")
    private Long id;

    @Schema(description = "产品ID")
    private Long productId;

    @Schema(description = "渠道名称")
    private String channelName;

    @Schema(description = "渠道类型（wechat, douyin, xiaohongshu）")
    private String channelType;

    @Schema(description = "渠道描述")
    private String channelDesc;
}
