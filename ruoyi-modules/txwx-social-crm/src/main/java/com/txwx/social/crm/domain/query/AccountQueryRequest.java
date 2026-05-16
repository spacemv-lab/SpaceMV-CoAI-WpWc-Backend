package com.txwx.social.crm.domain.query;

import com.txwx.social.api.domain.dto.AccountDTO;
import com.txwx.social.crm.common.config.QueryConfig;
import lombok.Data;

@Data
public class AccountQueryRequest {

    private AccountDTO query;

    private QueryConfig queryConfig;
}
