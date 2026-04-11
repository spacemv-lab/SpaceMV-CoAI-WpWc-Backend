package com.txwx.social.api.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 账号信息传输对象
 *
 * @author txwx
 * @date 2026-04-04
 */
@Data
@Schema(description = "账号信息传输对象")
public class AccountDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "账号ID")
    private Long id;

    @Schema(description = "渠道ID")
    private Long channelId;

    @Schema(description = "产品ID")
    private Long productId;

    @Schema(description = "渠道名称")
    private String channelName;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "账号名称")
    private String accountName;

    @Schema(description = "账号号码")
    private String accountNo;

    @Schema(description = "状态：0-停用 1-启用")
    private String status;

    @Schema(description = "应用ID")
    private String appId;

    @Schema(description = "应用密钥")
    private String secret;
}
