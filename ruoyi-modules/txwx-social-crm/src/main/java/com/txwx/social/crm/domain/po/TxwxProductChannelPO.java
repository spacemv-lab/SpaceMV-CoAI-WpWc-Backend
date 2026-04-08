package com.txwx.social.crm.domain.po;

import com.ruoyi.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 产品渠道关联表
 *
 * @author txwx
 * @date 2026-04-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "产品渠道关联表")
public class TxwxProductChannelPO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "产品ID")
    private Long productId;

    @Schema(description = "渠道ID")
    private Long channelId;

    @Schema(description = "删除标志(0代表存在 1代表删除)")
    private String delFlag;
}
