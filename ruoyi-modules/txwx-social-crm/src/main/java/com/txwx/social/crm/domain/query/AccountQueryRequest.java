/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.domain.query;

import com.txwx.social.api.domain.dto.AccountDTO;
import com.txwx.social.crm.common.config.QueryConfig;
import lombok.Data;

@Data
public class AccountQueryRequest {

    private AccountDTO query;

    private QueryConfig queryConfig;
}
