/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.dto;

import lombok.Data;

@Data
public class RegisterQuery {

    private String userName;

    private String phonenumber;

    private String email;

    private String status;
}
