/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.txwx.social.crm.domain.po.TxwxUserLogoutPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账号注销申请表Mapper
 *
 * @author txwx
 * @date 2026-04-20
 */
@Mapper
public interface TxwxUserLogoutMapper extends BaseMapper<TxwxUserLogoutPO> {
}
