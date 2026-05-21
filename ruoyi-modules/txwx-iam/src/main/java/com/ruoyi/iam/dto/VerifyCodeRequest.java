package com.ruoyi.iam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 验证码请求
 *
 * @author txwx
 */
@Data
@Schema(description = "验证码发送请求对象")
public class VerifyCodeRequest
{
    @Schema(description = "通道类型：phone / email")
    private String channelType;

    @Schema(description = "通道账号（手机号/邮箱）")
    private String channelAccount;
}
