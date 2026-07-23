/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.mapper.content;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.common.datasource.annotation.Postgres;
import com.txwx.social.crm.domain.po.SyncLogPO;

@Postgres
public interface SyncLogMapper extends BaseMapper<SyncLogPO> {
}
