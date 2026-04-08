package com.txwx.social.crm.domain.po;

import com.ruoyi.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 产品表
 *
 * @author txwx
 * @date 2026-04-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "产品表")
public class TxwxProductPO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "产品编码")
    private String productCode;

    @Schema(description = "产品描述")
    private String productDesc;

    @Schema(description = "删除标志(0代表存在 1代表删除)")
    private int delFlag;
}
