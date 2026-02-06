package com.txwx.webchat.service.impl;

import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.txwx.webchat.config.WebChatConfig;
import com.txwx.webchat.domain.ArticleReadDaily;
import com.txwx.webchat.domain.ArticleSummaryDaily;
import com.txwx.webchat.domain.ArticleShareDaily;
import com.txwx.webchat.util.WebChatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 历史数据抓取工具类（临时使用）
 * 用于抓取2025年11月1日到2026年2月5日的历史数据
 */
@Component
public class WebChatHistoryDataCapture {

    private static Logger logger = LoggerFactory.getLogger(WebChatHistoryDataCapture.class);

    @Autowired
    private WebChatConfig webChatConfig;

    @Autowired
    private ClickhouseService clickhouseService;

    /**
     * @description: 获取并保存历史发表内容每日阅读数据（2025-11-01 到 2026-12-04）
     */
    public void captureArticleReadDailyHistory(String accessToken) {
        logger.info("<##############################历史发表内容每日阅读数据抓取开始##############################>");
        logger.info("传入的凭证->" + accessToken);

        LocalDate startDate = LocalDate.of(2025, 11, 1);
        LocalDate endDate = LocalDate.of(2026, 2, 5);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        LocalDate currentDate = startDate;
        int totalCount = 0;

        while (!currentDate.isAfter(endDate)) {
            String formattedDate = currentDate.format(formatter);
            logger.info("正在抓取日期: " + formattedDate);

            // 抓取发表内容每日阅读数据
            List<ArticleReadDaily> articleReadDailyList = null;
            try {
                articleReadDailyList = WebChatUtil.getArticleReadDaily(accessToken, formattedDate, formattedDate);
            } catch (Exception ex) {
                logger.error("抓取发表内容每日阅读数据失败，日期: " + formattedDate + ", 错误: " + ex.getMessage());
            }

            // 将获取的数据插入数据库
            if (articleReadDailyList != null && articleReadDailyList.size() > 0) {
                logger.info("<------获取的发表内容每日阅读数据条数------> " + articleReadDailyList.size());
                List<Object[]> batchArgs = new ArrayList<>();
                for (ArticleReadDaily article : articleReadDailyList) {
                    batchArgs.add(article.toObject());
                }

                String insertSql = webChatConfig.getInsertarticlereaddailysql();
                if (insertSql != null && !insertSql.isEmpty()) {
                    try {
                        clickhouseService.batchInsert(insertSql, batchArgs);
                        totalCount += batchArgs.size();
                        logger.info("成功插入发表内容每日阅读数据到ClickHouse，数量: " + batchArgs.size());
                    } catch (Exception ex) {
                        logger.error("插入发表内容每日阅读数据到ClickHouse失败: " + ex.getMessage());
                    }
                } else {
                    logger.warn("未配置insertarticlereaddailysql，无法插入数据到ClickHouse");
                }
            } else {
                logger.info("日期 " + formattedDate + " 无数据");
            }

            currentDate = currentDate.plusDays(1);
        }

        logger.info("历史发表内容每日阅读数据抓取完成，总计插入数据: " + totalCount + " 条");
        logger.info("<##############################历史发表内容每日阅读数据抓取结束##############################>");
    }

    /**
     * @description: 获取并保存历史发表内容概况总数据（2025-11-01 到 2026-02-05）
     */
    public void captureArticleSummaryDailyHistory(String accessToken) {
        logger.info("<##############################历史发表内容概况总数据抓取开始##############################>");
        logger.info("传入的凭证->" + accessToken);

        LocalDate startDate = LocalDate.of(2025, 11, 1);
        LocalDate endDate = LocalDate.of(2026, 2, 5);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        LocalDate currentDate = startDate;
        int totalCount = 0;

        while (!currentDate.isAfter(endDate)) {
            String formattedDate = currentDate.format(formatter);
            logger.info("正在抓取日期: " + formattedDate);

            // 抓取发表内容概况总数据
            List<ArticleSummaryDaily> articleSummaryDailyList = null;
            try {
                articleSummaryDailyList = WebChatUtil.getArticleSummaryDaily(accessToken, formattedDate, formattedDate);
            } catch (Exception ex) {
                logger.error("抓取发表内容概况总数据失败，日期: " + formattedDate + ", 错误: " + ex.getMessage());
            }

            // 将获取的数据插入数据库
            if (articleSummaryDailyList != null && articleSummaryDailyList.size() > 0) {
                logger.info("<------获取的发表内容概况总数据条数------> " + articleSummaryDailyList.size());
                List<Object[]> batchArgs = new ArrayList<>();
                for (ArticleSummaryDaily article : articleSummaryDailyList) {
                    batchArgs.add(article.toObject());
                }

                String insertSql = webChatConfig.getInsertarticlesummarydailysql();
                if (insertSql != null && !insertSql.isEmpty()) {
                    try {
                        clickhouseService.batchInsert(insertSql, batchArgs);
                        totalCount += batchArgs.size();
                        logger.info("成功插入发表内容概况总数据到ClickHouse，数量: " + batchArgs.size());
                    } catch (Exception ex) {
                        logger.error("插入发表内容概况总数据到ClickHouse失败: " + ex.getMessage());
                    }
                } else {
                    logger.warn("未配置insertarticlesummarydailysql，无法插入数据到ClickHouse");
                }
            } else {
                logger.info("日期 " + formattedDate + " 无数据");
            }

            currentDate = currentDate.plusDays(1);
        }

        logger.info("历史发表内容概况总数据抓取完成，总计插入数据: " + totalCount + " 条");
        logger.info("<##############################历史发表内容概况总数据抓取结束##############################>");
    }

    /**
     * @description: 获取并保存历史发表内容每日分享数据（2025-11-01 到 2026-02-05）
     */
    public void captureArticleShareDailyHistory(String accessToken) {
        logger.info("<##############################历史发表内容每日分享数据抓取开始##############################>");
        logger.info("传入的凭证->" + accessToken);

        LocalDate startDate = LocalDate.of(2025, 11, 1);
        LocalDate endDate = LocalDate.of(2026, 2, 5);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        LocalDate currentDate = startDate;
        int totalCount = 0;

        while (!currentDate.isAfter(endDate)) {
            String formattedDate = currentDate.format(formatter);
            logger.info("正在抓取日期: " + formattedDate);

            // 抓取发表内容每日分享数据
            List<ArticleShareDaily> articleShareDailyList = null;
            try {
                articleShareDailyList = WebChatUtil.getArticleShareDaily(accessToken, formattedDate, formattedDate);
            } catch (Exception ex) {
                logger.error("抓取发表内容每日分享数据失败，日期: " + formattedDate + ", 错误: " + ex.getMessage());
            }

            // 将获取的数据插入数据库
            if (articleShareDailyList != null && articleShareDailyList.size() > 0) {
                logger.info("<------获取的发表内容每日分享数据条数------> " + articleShareDailyList.size());
                List<Object[]> batchArgs = new ArrayList<>();
                for (ArticleShareDaily article : articleShareDailyList) {
                    batchArgs.add(article.toObject());
                }

                String insertSql = webChatConfig.getInsertarticlesharedailysql();
                if (insertSql != null && !insertSql.isEmpty()) {
                    try {
                        clickhouseService.batchInsert(insertSql, batchArgs);
                        totalCount += batchArgs.size();
                        logger.info("成功插入发表内容每日分享数据到ClickHouse，数量: " + batchArgs.size());
                    } catch (Exception ex) {
                        logger.error("插入发表内容每日分享数据到ClickHouse失败: " + ex.getMessage());
                    }
                } else {
                    logger.warn("未配置insertarticlesharedailysql，无法插入数据到ClickHouse");
                }
            } else {
                logger.info("日期 " + formattedDate + " 无数据");
            }

            currentDate = currentDate.plusDays(1);
        }

        logger.info("历史发表内容每日分享数据抓取完成，总计插入数据: " + totalCount + " 条");
        logger.info("<##############################历史发表内容每日分享数据抓取结束##############################>");
    }
}

