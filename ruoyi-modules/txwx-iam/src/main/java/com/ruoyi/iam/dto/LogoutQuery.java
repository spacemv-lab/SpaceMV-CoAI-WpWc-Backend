/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.dto;

import lombok.Data;

/**
 * 注销申请查询参数
 */
@Data
public class LogoutQuery {

    private Long userId;

    private String status;
}
