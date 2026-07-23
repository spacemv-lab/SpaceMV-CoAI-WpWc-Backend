/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.task;

import com.txwx.social.crm.service.content.IDataSourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class FredCategorySyncTask {

    private final IDataSourceService dataSourceService;

    @Scheduled(cron = "0 0 6 * * ?")
    public void dailySync() {
        log.info("=== FRED daily category sync started ===");
        Map<String, Object> result = dataSourceService.syncAllCategories();
        log.info("=== FRED daily category sync finished: {} ===", result);
    }
}
