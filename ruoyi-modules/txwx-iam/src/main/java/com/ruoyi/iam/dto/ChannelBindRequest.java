package com.ruoyi.iam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 通道绑定请求
 *
 * @author txwx
 */
@Data
@Schema(description = "通道绑定请求对象")
public class ChannelBindRequest
{
    @Schema(description = "通道类型：phone / email")
    private String channelType;

    @Schema(description = "通道账号")
    private String channelAccount;

    @Schema(description = "验证码")
    private String verifyCode;
}
