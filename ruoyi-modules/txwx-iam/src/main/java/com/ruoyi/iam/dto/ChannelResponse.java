package com.ruoyi.iam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 通道信息响应
 *
 * @author txwx
 */
@Data
@Schema(description = "通道信息响应对象")
public class ChannelResponse
{
    @Schema(description = "通道 ID")
    private Long id;

    @Schema(description = "通道类型：phone / email")
    private String channelType;

    @Schema(description = "脱敏后的通道账号")
    private String channelAccount;

    @Schema(description = "是否主通道：1=是 0=否")
    private String isPrimary;

    @Schema(description = "绑定时间")
    private String bindTime;

    @Schema(description = "状态：0=正常")
    private String status;
}
