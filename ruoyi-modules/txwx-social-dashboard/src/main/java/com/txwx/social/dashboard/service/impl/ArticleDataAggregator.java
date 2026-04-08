package com.txwx.social.dashboard.service.impl;

import com.ruoyi.common.clickhouse.service.ClickhouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: ODS层数据聚合到DWS层的工具类
 * 将ods_article、ods_article_read_daily、ods_article_share_daily聚合到dws_article_read
 */
@Component
public class ArticleDataAggregator {

    private static Logger logger = LoggerFactory.getLogger(ArticleDataAggregator.class);

    @Autowired
    private ClickhouseService clickhouseService;

    /**
     * @description: 聚合所有数据到DWS层（按msgid维度全量累加）
     */
    public void aggregateYesterdayDataToDws() {
        logger.info("<##############################聚合文章数据到DWS层开始##############################>");

        // 获取昨天的日期
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayISO = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE);
        logger.info("聚合数据日期: " + yesterdayISO);

        try {
            // 1. 查询所有需要聚合的阅读数据（使用带index的msgid，按msgid累加）
            String readQuerySql = "SELECT " +
                    "msgid, " +
                    "sum(read_user_total) as read_user_total, " +
                    "sum(read_user_source_all) as read_user_source_all, " +
                    "sum(read_user_source_msg) as read_user_source_msg, " +
                    "sum(read_user_source_chat) as read_user_source_chat, " +
                    "sum(read_user_source_moments) as read_user_source_moments, " +
                    "sum(read_user_source_homepage) as read_user_source_homepage, " +
                    "sum(read_user_source_other) as read_user_source_other, " +
                    "sum(read_user_source_recommend) as read_user_source_recommend, " +
                    "sum(read_user_source_search) as read_user_source_search " +
                    "FROM ods_article_read_daily " +
                    "GROUP BY msgid";

            List<Map<String, Object>> readDataList = clickhouseService.readData(readQuerySql);
            logger.info("查询到阅读数据条数: " + (readDataList != null ? readDataList.size() : 0));

            // 2. 查询所有需要聚合的分享数据（使用带index的msgid，按msgid累加）
            String shareQuerySql = "SELECT " +
                    "msgid, " +
                    "sum(share_user) as share_user " +
                    "FROM ods_article_share_daily " +
                    "GROUP BY msgid";

            List<Map<String, Object>> shareDataList = clickhouseService.readData(shareQuerySql);
            logger.info("查询到分享数据条数: " + (shareDataList != null ? shareDataList.size() : 0));

            // 3. 构建msgid映射：从带index的msgid提取基础msgid（去掉_1后缀）
            Map<String, Map<String, Object>> aggregatedData = new java.util.HashMap<>();

            // 聚合阅读数据（SQL已完成累加，直接提取）
            if (readDataList != null && !readDataList.isEmpty()) {
                for (Map<String, Object> readData : readDataList) {
                    String msgidWithIndex = (String) readData.get("msgid");
                    String baseMsgid = extractBaseMsgid(msgidWithIndex);

                    Map<String, Object> data = aggregatedData.computeIfAbsent(baseMsgid, k -> new java.util.HashMap<>());

                    // 直接使用SQL累加后的值
                    data.put("read_user_total", readData.get("read_user_total"));
                    data.put("read_user_source_all", readData.get("read_user_source_all"));
                    data.put("read_user_source_msg", readData.get("read_user_source_msg"));
                    data.put("read_user_source_chat", readData.get("read_user_source_chat"));
                    data.put("read_user_source_moments", readData.get("read_user_source_moments"));
                    data.put("read_user_source_homepage", readData.get("read_user_source_homepage"));
                    data.put("read_user_source_other", readData.get("read_user_source_other"));
                    data.put("read_user_source_recommend", readData.get("read_user_source_recommend"));
                    data.put("read_user_source_search", readData.get("read_user_source_search"));
                }
            }

            // 聚合分享数据（SQL已完成累加，直接提取）
            if (shareDataList != null && !shareDataList.isEmpty()) {
                for (Map<String, Object> shareData : shareDataList) {
                    String msgidWithIndex = (String) shareData.get("msgid");
                    String baseMsgid = extractBaseMsgid(msgidWithIndex);

                    Map<String, Object> data = aggregatedData.computeIfAbsent(baseMsgid, k -> new java.util.HashMap<>());

                    // 直接使用SQL累加后的值
                    data.put("share_user", shareData.get("share_user"));
                }
            }

            logger.info("聚合后的文章数量: " + aggregatedData.size());

            // 4. 查询文章基本信息（create_time和title）
            if (!aggregatedData.isEmpty()) {
                //List<String> baseMsgids = new ArrayList<>(aggregatedData.keySet());
                //String msgidsStr = "'" + String.join("','", baseMsgids) + "'";

                //String articleQuerySql = "SELECT msgid, title, create_time FROM ods_article WHERE msgid IN ('" + msgidsStr + "')";
                String articleQuerySql = "SELECT msgid, title, create_time FROM ods_article";
                List<Map<String, Object>> articleList = clickhouseService.readData(articleQuerySql);
                logger.info("查询到文章基本信息条数: " + (articleList != null ? articleList.size() : 0));

                // 构建文章信息映射
                Map<String, Map<String, Object>> articleInfoMap = new java.util.HashMap<>();
                if (articleList != null && !articleList.isEmpty()) {
                    for (Map<String, Object> article : articleList) {
                        String msgid = (String) article.get("msgid");
                        articleInfoMap.put(msgid, article);
                    }
                }

                // 5. 准备批量插入数据
                List<Object[]> batchArgs = new ArrayList<>();
                for (Map.Entry<String, Map<String, Object>> entry : aggregatedData.entrySet()) {
                    String baseMsgid = entry.getKey();
                    Map<String, Object> data = entry.getValue();

                    // 获取文章基本信息
                    Map<String, Object> articleInfo = articleInfoMap.get(baseMsgid);
                    if (articleInfo == null) {
                        logger.warn("未找到文章信息，msgid: " + baseMsgid);
                        continue;
                    }

                    // 构建插入数据
                    batchArgs.add(new Object[]{
                        articleInfo.get("create_time"),
                        baseMsgid,
                        articleInfo.get("title"),
                        data.getOrDefault("read_user_total", 0),
                        data.getOrDefault("read_user_source_all", 0),
                        data.getOrDefault("read_user_source_msg", 0),
                        data.getOrDefault("read_user_source_chat", 0),
                        data.getOrDefault("read_user_source_moments", 0),
                        data.getOrDefault("read_user_source_homepage", 0),
                        data.getOrDefault("read_user_source_other", 0),
                        data.getOrDefault("read_user_source_recommend", 0),
                        data.getOrDefault("read_user_source_search", 0),
                        data.getOrDefault("share_user", 0)
                    });
                }

                // 6. 清空dws_article_read表，然后插入新数据（ClickHouse更新策略）
                if (!batchArgs.isEmpty()) {
                    String truncateSql = "TRUNCATE TABLE dws_article_read";
                    try {
                        clickhouseService.singleInsert(truncateSql);
                        logger.info("清空dws_article_read表成功");
                    } catch (Exception e) {
                        logger.warn("清空dws_article_read表失败（可能是第一次运行）: " + e.getMessage());
                    }

                    // 插入新数据
                    String insertSql = "INSERT INTO dws_article_read (" +
                            "create_time, " +
                            "msgid, " +
                            "title, " +
                            "read_user_total, " +
                            "read_user_source_all, " +
                            "read_user_source_msg, " +
                            "read_user_source_chat, " +
                            "read_user_source_moments, " +
                            "read_user_source_homepage, " +
                            "read_user_source_other, " +
                            "read_user_source_recommend, " +
                            "read_user_source_search, " +
                            "share_user) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                    clickhouseService.batchInsert(insertSql, batchArgs);
                    logger.info("成功插入dws_article_read数据条数: " + batchArgs.size());
                }
            }

            logger.info("<##############################聚合文章数据到DWS层结束##############################>");
        } catch (Exception e) {
            logger.error("聚合文章数据到DWS层失败: " + e.getMessage(), e);
            throw new RuntimeException("聚合文章数据到DWS层失败: " + e.getMessage(), e);
        }
    }

    /**
     * @description: 从带index的msgid提取基础msgid
     * 例如：2247489045_1 -> 2247489045
     */
    private String extractBaseMsgid(String msgidWithIndex) {
        if (msgidWithIndex == null || msgidWithIndex.isEmpty()) {
            return msgidWithIndex;
        }
        int underscoreIndex = msgidWithIndex.lastIndexOf('_');
        if (underscoreIndex > 0) {
            return msgidWithIndex.substring(0, underscoreIndex);
        }
        return msgidWithIndex;
    }
}

