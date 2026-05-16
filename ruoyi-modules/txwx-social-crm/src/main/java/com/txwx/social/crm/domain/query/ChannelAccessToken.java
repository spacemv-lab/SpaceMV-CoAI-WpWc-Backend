/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.domain.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "渠道临时凭证")
public class ChannelAccessToken {
    /**
     * @description: 获取到的凭证
     */
    @Schema(description = "access_token")
    private String access_token;

    /**
     * @description: 凭证有效时间，单位：秒。目前是7200秒之内的值。
     */
    @Schema(description = "有效时间，单位秒")
    private int expires_in;

    @Schema(description = "渠道名称")
    private String channel;
}
