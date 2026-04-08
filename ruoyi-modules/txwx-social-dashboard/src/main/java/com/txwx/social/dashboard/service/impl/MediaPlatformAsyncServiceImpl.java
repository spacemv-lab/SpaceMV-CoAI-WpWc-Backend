package com.txwx.social.dashboard.service.impl;

import com.txwx.social.dashboard.domain.entity.mysql.MediaPlatform;
import com.txwx.social.dashboard.domain.enums.SyncStatusEnum;
import com.txwx.social.dashboard.service.IWebChatCaptureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class MediaPlatformAsyncServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(MediaPlatformAsyncServiceImpl.class);

    @Autowired
    private MediaPlatformStatusServiceImpl mediaPlatformStatusServiceImpl;
    @Autowired
    private SyncDataServiceImpl syncDataServiceImpl;
    @Autowired
    private IWebChatCaptureService webChatCaptureService;

    @Async("dataSyncExecutor")
    public void syncPlatformData(String accessToken, MediaPlatform mediaPlatform) throws Exception {
        try {
            long startTime = System.currentTimeMillis();
            // 同步逻辑
            logger.info("开始同步数据..." + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            System.out.println("开始同步数据..." + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            syncDataServiceImpl.syncUserHistoryRange(accessToken, mediaPlatform.getId(), mediaPlatform.getProductId(), "2026-03-25", today);
            syncDataServiceImpl.syncArticleSummaryHistoryRange(accessToken, mediaPlatform.getId(), mediaPlatform.getProductId(), "2026-03-25", today);
            syncDataServiceImpl.syncArticleTotalDetailHistoryRange(accessToken, mediaPlatform.getProductId(), mediaPlatform.getId(), "2026-03-28", today);
            webChatCaptureService.capturePublishedArticles(accessToken, mediaPlatform.getId(), mediaPlatform.getProductId());

            logger.info("开始同步数据..." + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            System.out.println("停止同步数据..." + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            // 计算同步所用的时间
            long endTime = System.currentTimeMillis();
            long durationMs = endTime - startTime;
            long seconds = durationMs / 1000;
            long minutes = seconds / 60;
            long remainSeconds = seconds % 60;

            System.out.println("本次同步耗时: " + minutes + " 分 " + remainSeconds + " 秒");
            logger.info("停止同步数据, platformId={}, time={}, cost={} ms",
                    mediaPlatform.getId(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    durationMs);

            mediaPlatform.setSyncStatus(SyncStatusEnum.SYNCED_SUCCESS.getCode());
            mediaPlatformStatusServiceImpl.updateSyncStatus(mediaPlatform.getId(), SyncStatusEnum.SYNCED_SUCCESS.getCode(), null);
        } catch (Exception e) {
            mediaPlatform.setSyncStatus(SyncStatusEnum.SYNCED_FAIL.getCode());
            mediaPlatformStatusServiceImpl.updateSyncStatus(mediaPlatform.getId(), SyncStatusEnum.SYNCED_FAIL.getCode(), null);
            throw e;
        }
    }
}
