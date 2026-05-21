/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.dashboard.domain;

import lombok.Data;

@Data
public class WebChatDate {

    /**
     * @description: 开始时间
     */
    private String begin_date;
    
    /**
     * @description: 结束时间
     */
    private String end_date;
}
