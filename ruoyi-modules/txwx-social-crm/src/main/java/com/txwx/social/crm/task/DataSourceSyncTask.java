/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.task;

import cn.hutool.cron.pattern.CronPattern;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.txwx.social.crm.domain.po.DataSourcePO;
import com.txwx.social.crm.mapper.content.DataSourceMapper;
import com.txwx.social.crm.service.content.IDataSourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSourceSyncTask {

    private final DataSourceMapper dataSourceMapper;
    private final IDataSourceService dataSourceService;

    @Scheduled(fixedRate = 60_000)
    public void checkScheduledSync() {
        List<DataSourcePO> scheduledSources = dataSourceMapper.selectList(
                new LambdaQueryWrapper<DataSourcePO>()
                        .eq(DataSourcePO::getEnabled, true)
                        .isNotNull(DataSourcePO::getSchedule)
                        .ne(DataSourcePO::getSchedule, "")
        );

        for (DataSourcePO source : scheduledSources) {
            if (shouldRunNow(source)) {
                log.info("Scheduled sync triggered: {} ({})", source.getName(), source.getId());
                dataSourceService.sync(source.getId(), "scheduled");
            }
        }
    }

    private boolean shouldRunNow(DataSourcePO source) {
        String cron = source.getSchedule();
        if (cron == null || cron.isBlank()) return false;
        if (!cron.matches("^[0-9*,/\\-?LW#]+(\\s+[0-9*,/\\-?LW#]+){4,6}$")) return false;

        try {
            CronPattern pattern = CronPattern.of(cron);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            java.util.Calendar nextMatch = pattern.nextMatchAfter(cal);
            return nextMatch.getTimeInMillis() <= System.currentTimeMillis() + 60_000;
        } catch (Exception e) {
            return false;
        }
    }
}
