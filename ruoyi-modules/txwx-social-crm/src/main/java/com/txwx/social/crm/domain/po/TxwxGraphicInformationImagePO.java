/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.domain.po;

import com.ruoyi.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 微信公众号图文消息图片实体类
 *
 * @author txwx
 * @date 2025-12-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "图文消息图片实体")
public class TxwxGraphicInformationImagePO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "媒体ID")
    private String mediaId;

    @Schema(description = "图片名称")
    private String name;

    @Schema(description = "图片URL")
    private String url;

    @Schema(description = "删除标志(0代表存在 1代表删除)")
    private String delFlag;

    @Schema(description = "账号ID")
    private Long accountId;
}
