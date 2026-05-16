/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.bizchain.account.context;


import com.txwx.social.api.domain.dto.AccountDTO;
import com.txwx.social.crm.common.chain.BaseChainContext;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccountChainContext extends BaseChainContext {
    // 输入参数
    private List<Long> accountIds;
    private List<AccountDTO> accounts;
    private AccountDTO queryParams;

    // 输出结果
    private List<AccountDTO> accountList;
}
