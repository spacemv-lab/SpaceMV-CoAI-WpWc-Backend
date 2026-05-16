/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 渠道表
 *
 * @author txwx
 * @date 2026-04-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "渠道表")
@TableName("txwx_channel")
public class TxwxChannelPO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "渠道名称")
    private String channelName;

    @Schema(description = "渠道类型（wechat, douyin, xiaohongshu）")
    private String channelType;

    @Schema(description = "渠道描述")
    private String channelDesc;

    @Schema(description = "删除标志(0代表存在 1代表删除)")
    private String delFlag;
}
