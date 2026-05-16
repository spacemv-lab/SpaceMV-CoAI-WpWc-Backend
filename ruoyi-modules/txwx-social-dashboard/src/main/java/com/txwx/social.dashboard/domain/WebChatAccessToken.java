/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.dashboard.domain;

import lombok.Data;

@Data
public class WebChatAccessToken {
    /**
     * @description: 获取到的凭证
     */
    private String access_token;

    /**
     * @description: 凭证有效时间，单位：秒。目前是7200秒之内的值。
     */
    private int expires_in;
}
