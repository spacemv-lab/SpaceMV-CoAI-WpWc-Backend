/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.dashboard.schedule;

import com.google.gson.Gson;
import com.ruoyi.common.core.domain.R;
import com.txwx.social.api.client.AccountApiClient;
import com.txwx.social.api.domain.dto.AccountDTO;
import com.txwx.social.dashboard.config.WebChatConfig;
import com.txwx.social.dashboard.service.IWebChatCaptureService;
import com.txwx.social.dashboard.service.SyncDataService;
import com.txwx.social.dashboard.service.ArticleDataAggregator;
import com.txwx.social.dashboard.util.NoticeHelper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Component
@EnableScheduling
@Slf4j
public class WebChatDataCaptureTasks {
    private static Logger logger = LoggerFactory.getLogger(WebChatDataCaptureTasks.class);

    @Autowired
    private WebChatConfig webChatConfig;
    @Autowired
    private IWebChatCaptureService webChatCaptureService;
    @Autowired
    private AccountApiClient accountApiClient;

    @Autowired
    private ArticleDataAggregator articleDataAggregator;

    @Autowired
    private SyncDataService syncDataService;

    @Autowired
    private NoticeHelper noticeHelper;

    @Scheduled(cron = "0 30 8 * * ?")
    public void getYesterdayDatas() {
        boolean success = true;
        StringBuilder detailMsg = new StringBuilder();
        R<List<AccountDTO>> accountListRes = accountApiClient.getAccountList(new AccountDTO());
        noticeHelper.sendSystemSyncNotification("数据同步", "getYesterdayDatas正在任务", 2, "同步任务进行中");
        try {
            if (accountListRes != null) {
                List<AccountDTO> accountDTOList = accountListRes.getData();
                if (CollectionUtils.isEmpty(accountDTOList)) {
                    detailMsg.append("账号列表为空，跳过同步");
                } else {
                    int syncCount = 0;
                    for (AccountDTO accountDTO : accountDTOList) {
                        if (accountDTO != null && accountDTO.getId() != null) {
                            // py原有的同步逻辑
                            //syncData(accountDTO.getId());
                            // 2026.04.16替换成qyl的同步逻辑，有点复杂暂时难以读懂就用的他的
                            LocalDate yesterday = LocalDate.now().minusDays(1);
                            String yesterdayFormatted = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE);
                            String accessToken = webChatCaptureService.getAccessToken(accountDTO.getId());
                            syncDataService.syncPlatformData(accessToken, accountDTO.getId(), yesterdayFormatted, yesterdayFormatted);
                            syncCount++;
                        } else {
                            logger.error("错误的账号信息{}", new Gson().toJson(accountDTO));
                            detailMsg.append("账号信息异常: ").append(new Gson().toJson(accountDTO)).append("; ");
                        }
                    }
                    detailMsg.append("同步账号数: ").append(syncCount);
                }
            } else {
                success = false;
                detailMsg.append("获取账号列表失败");
            }
        } catch (Exception e) {
            success = false;
            detailMsg.append("同步异常: ").append(e.getMessage());
            logger.error("WebChatDataCaptureTasks.getYesterdayDatas error, {}", e.getMessage(), e);
        } finally {
            noticeHelper.sendSystemSyncNotification("数据同步", "getYesterdayDatas完成任务", 0, detailMsg.toString());
        }
    }

    private void syncData(Long accountId) throws Exception {
        logger.info("<===================微信公众号数据每日抓取开始=======" + timestamp() + "==============>");
        // 获取 用户增减数据 【ods_users】
        // 聚合            【dws_users】

        LocalDate yesterday = LocalDate.now().minusDays(1);
        //syncDataServiceImpl.syncUserOneDay(accessToken, platformId, productId, today);
        // TODO 20260415 暂时未做accessToken中控过期机制，假设当前2小时内是可以抓完全部数据的
        String accessToken = webChatCaptureService.getAccessToken(accountId);
        webChatCaptureService.webChatUserCapture(accessToken, accountId);
        logger.info("\n");
        // 获取 图文群发 每日数据 【article_perday】
        // todo 20260226 微信后续会停用
        webChatCaptureService.webChatArticleUptackCapture(accessToken, accountId);

        logger.info("\n");
        //  获取 图文阅读 概括数据 【user_read】
        // todo 20260226 微信后续会停用
        webChatCaptureService.webChatUserReadCapture(accessToken, accountId);

        logger.info("\n");
        // 获取 每日阅读数据 【ods_article_read_daily】
        webChatCaptureService.captureArticleReadDaily(accessToken, accountId);

        //webChatCaptureService.captureArticleSummaryDaily(accessToken, accountId);

        logger.info("\n");
        webChatCaptureService.captureArticleShareDaily(accessToken, accountId);

        logger.info("\n");
        articleDataAggregator.aggregateDataToDws(accountId);

        logger.info("\n");
        logger.info("<===================微信公众号数据每日抓取结束=======" + timestamp() + "==============>");


        logger.info("\n");
        // 获取 详细文章数据 【ods_article_detail_daily】
        // TODO 接口即将停止维护
        webChatCaptureService.captureArticleTotalDetailDaily(accountId);
        String formatted = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE);
        //syncDataServiceImpl.syncArticleTotalDetailHistoryRange(accessToken, productId, platformId, formatted, formatted);

        logger.info("\n");
        logger.info("<===================微信公众号数据每日抓取结束=======" + timestamp() + "==============>");

    }

    /**
     * @description: 每天凌晨3点获取并保存已发布消息列表
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void capturePublishedArticles() {
        boolean success = true;
        StringBuilder detailMsg = new StringBuilder();
        noticeHelper.sendSystemSyncNotification("数据同步", "capturePublishedArticles正在任务", 2, "同步任务进行中");
        R<List<AccountDTO>> accountListRes = accountApiClient.getAccountList(new AccountDTO());
        try {
            if (accountListRes != null) {
                List<AccountDTO> accountDTOList = accountListRes.getData();
                if (CollectionUtils.isEmpty(accountDTOList)) {
                    detailMsg.append("账号列表为空，跳过同步");
                } else {
                    int syncCount = 0;
                    for (AccountDTO accountDTO : accountDTOList) {
                        if (accountDTO != null) {
                            syncPublishedData(accountDTO.getId());
                            syncCount++;
                        }
                    }
                    detailMsg.append("同步账号数: ").append(syncCount);
                }
            } else {
                success = false;
                detailMsg.append("获取账号列表失败");
            }
        } catch (Exception e) {
            success = false;
            detailMsg.append("同步异常: ").append(e.getMessage());
            logger.error("WebChatDataCaptureTasks.capturePublishedArticles error, {}", e.getMessage(), e);
        } finally {
            noticeHelper.sendSystemSyncNotification("文章同步", "capturePublishedArticles完成任务", 0, detailMsg.toString());
        }
    }

    private void syncPublishedData(Long accountId) {
        logger.info("<===================微信公众号已发布消息列表抓取开始=======" + timestamp() + "==============>");
        logger.info("\n");
        webChatCaptureService.capturePublishedArticles(accountId);
        logger.info("\n");
        logger.info("<===================微信公众号已发布消息列表抓取结束=======" + timestamp() + "==============>");
    }

    private String timestamp(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = now.format(formatter);
        return timestamp;
    }

    public static void main(String[] args) {

    }
}
