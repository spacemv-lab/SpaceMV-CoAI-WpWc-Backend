/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.service.content.fetcher;

import com.txwx.social.crm.domain.po.DataSourcePO;

import java.util.List;
import java.util.Map;

public interface DataFetcher {

    boolean supports(String type);

    List<Map<String, Object>> fetch(DataSourcePO source) throws Exception;
}
