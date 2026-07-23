/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.service.content;

import com.txwx.social.crm.domain.po.DataSourcePO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface IDataSourceService {

    Map<String, Object> pageList(Map<String, Object> params);

    DataSourcePO getById(String id);

    void create(DataSourcePO dataSource);

    void update(String id, DataSourcePO dataSource);

    void delete(String id);

    Map<String, Object> sync(String id, String triggerType);

    Map<String, Object> importCsv(String id, MultipartFile file);

    List<SyncLogItem> getSyncLogs(String sourceId);

    Map<String, Object> syncCategory(int categoryId);

    Map<String, Object> syncAllCategories();

    void syncAllCategoriesAsync();

    record SyncLogItem(Integer id, String sourceId, String status, String triggerType,
                       Integer dataPoints, String errorMsg,
                       String startedAt, String finishedAt, String createdBy) {}
}
