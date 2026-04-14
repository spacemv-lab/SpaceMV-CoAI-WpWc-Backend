package com.txwx.social.crm.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 账号表
 *
 * @author txwx
 * @date 2026-04-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "账号表")
@TableName("txwx_account")
public class TxwxAccountPO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "渠道ID")
    private Long channelId;

    @Schema(description = "产品ID")
    private Long productId;

    @Schema(description = "账号名称")
    private String accountName;

    @Schema(description = "账号号码")
    private String accountNo;

    @Schema(description = "微信appid")
    private String appId;

    @Schema(description = "微信secret")
    private String secret;

    @Schema(description = "令牌")
    private String token;

    @Schema(description = "消息加密码")
    private String encodingAesKey;

    @Schema(description = "状态：0-停用 1-启用")
    private String status;

    @Schema(description = "最后同步时间")
    private String lastSyncTime;

    @Schema(description = "删除标志(0代表存在 1代表删除)")
    private String delFlag;
}
