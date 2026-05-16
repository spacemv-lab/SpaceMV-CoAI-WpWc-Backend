package com.txwx.social.crm.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 账号视图对象
 *
 * @author txwx
 * @date 2026-04-03
 */
@Data
@Schema(description = "账号视图对象")
public class AccountVO {

    @Schema(description = "主键ID")
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
}
