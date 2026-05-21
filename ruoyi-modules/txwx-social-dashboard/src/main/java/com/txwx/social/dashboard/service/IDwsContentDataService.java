/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.dashboard.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.txwx.social.dashboard.domain.condition.ContentDataSearchCondition;
import com.txwx.social.dashboard.domain.entity.DwsContentData;

import java.util.List;

public interface IDwsContentDataService extends IService<DwsContentData> {
    List<DwsContentData> select(ContentDataSearchCondition condition);

    Page<DwsContentData> selectDataListByPage(ContentDataSearchCondition condition, Integer pageNum, Integer pageSize);
}
