package com.ruoyi.iam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录态验证码发送请求对象
 * <p>
 * 适用于用户在已登录状态下主动发送验证码（如解绑、修改密码等场景）。
 * 与 {@link VerifyCodeRequest} 的区别在于多了 {@code useFrontAccount} 字段，
 * 默认优先查询 DB 已绑定的通道账号发送，避免前端传错。
 *
 * @author txwx
 */
@Data
@Schema(description = "登录态验证码发送请求对象")
public class SendAuthVerifyCodeRequest
{
    @Schema(description = "通道类型：phone / email", example = "phone")
    private String channelType;

    @Schema(description = "通道账号（手机号/邮箱），当 useFrontAccount=false 且 DB 有记录时被覆盖", example = "15123101295")
    private String channelAccount;

    @Schema(description = "是否强制使用前端传入的 channelAccount（默认 false，优先查 DB）", example = "false")
    private boolean useFrontAccount = false;
}
