/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.dashboard.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("ods_users")
public class OdsUsers {

    @Schema(description = "数据日期")
    private LocalDate refDate;

    private Integer newSource;

    private Integer newUser;

    private Integer cancelUser;

    private Long accountId;
}
