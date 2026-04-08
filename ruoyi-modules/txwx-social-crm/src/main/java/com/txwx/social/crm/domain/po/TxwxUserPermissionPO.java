package com.txwx.social.crm.domain.po;

import com.ruoyi.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户协作权限分配表
 *
 * @author txwx
 * @date 2026-04-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户协作权限分配表")
public class TxwxUserPermissionPO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "关系类型：0-系统级 1-产品级 2-渠道级")
    private Integer relationType;

    @Schema(description = "关系ID列表（JSON 数组）")
    private String relationIds;

    @Schema(description = "删除标志(0代表存在 1代表删除)")
    private String delFlag;
}
