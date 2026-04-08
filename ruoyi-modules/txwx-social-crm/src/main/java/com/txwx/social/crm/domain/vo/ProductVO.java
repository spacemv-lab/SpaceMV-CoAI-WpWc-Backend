package com.txwx.social.crm.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 产品视图对象
 *
 * @author txwx
 * @date 2026-04-03
 */
@Data
@Schema(description = "产品视图对象")
public class ProductVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "产品编码")
    private String productCode;

    @Schema(description = "产品描述")
    private String productDesc;
}
