package com.txwx.social.dashboard.service;

import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.txwx.social.dashboard.util.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @description: ODS层数据聚合到DWS层的工具类
 * 将ods_article、ods_article_read_daily、ods_article_share_daily聚合到dws_article_read
 */
@Component
@Slf4j
public class ArticleDataAggregator {

    private static Logger logger = LoggerFactory.getLogger(ArticleDataAggregator.class);

    @Autowired
    private ClickhouseService clickhouseService;

    /**
     * @description: 聚合所有数据到DWS层（按msgid维度全量累加）
     */
    public void aggregateDataToDws(Long accountId) {
        logger.info("<##############################聚合文章数据到DWS层开始##############################>");
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
                    "WHERE account_id = ?" +
                    "GROUP BY msgid";

            List<Map<String, Object>> readDataList = clickhouseService.readData(readQuerySql, accountId);
            logger.info("查询到阅读数据条数: " + (readDataList != null ? readDataList.size() : 0));

            // 2. 查询所有需要聚合的分享数据（使用带index的msgid，按msgid累加）
            String shareQuerySql = "SELECT " +
                    "msgid, " +
                    "sum(share_user) as share_user " +
                    "FROM ods_article_share_daily " +
                    "WHERE account_id = ?" +
                    "GROUP BY msgid";

            List<Map<String, Object>> shareDataList = clickhouseService.readData(shareQuerySql, accountId);
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
                    data.put("account_id", readData.get("account_id"));
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
                    data.put("account_id", shareData.get("account_id"));
                }
            }

            logger.info("聚合后的文章数量: " + aggregatedData.size());

            // 4. 查询文章基本信息（create_time和title）
            if (!aggregatedData.isEmpty()) {
                List<String> baseMsgids = new ArrayList<>(aggregatedData.keySet());
                String msgidsStr = "'" + String.join("','", baseMsgids) + "'";

                String articleQuerySql = "SELECT msgid, title, create_time FROM ods_article WHERE msgid IN ('" + msgidsStr + "') and account_id = ?";
                //String articleQuerySql = "SELECT msgid, title, create_time FROM ods_article WHERE account_id = ?";
                List<Map<String, Object>> articleList = clickhouseService.readData(articleQuerySql, accountId);
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
                        data.getOrDefault("share_user", 0),
                        accountId
                    });
                }

                // 6. 清空dws_article_read表，然后插入新数据（ClickHouse更新策略）
                if (!batchArgs.isEmpty()) {
                    String truncateSql = SqlUtils.deleteSql("dws_article_read");
                    try {
                        clickhouseService.singleInsert(truncateSql, accountId);
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
                            "share_user, account_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

    /**
     * 分步聚合内容数据到DWS层
     * 策略：分表查询、内存合并、批量插入
     */
    public void aggregateContentDataToDws(Long accountId) {
        log.info("<================= 开始聚合内容数据到DWS层，accountId: {} =================>", accountId);

        try {
            // 1. 查询文章详情日粒度聚合数据
            List<Map<String, Object>> detailData = aggregateArticleDetailData(accountId);
            if (CollectionUtils.isEmpty(detailData)) {
                log.warn("没有找到文章详情数据，accountId: {}", accountId);
                return;
            }
            log.info("查询到文章详情聚合数据条数: {}", detailData.size());

            // 2. 查询文章基础信息聚合数据
            List<Map<String, Object>> articleData = aggregateArticleData(accountId);
            log.info("查询到文章基础信息聚合数据条数: {}",
                    articleData != null ? articleData.size() : 0);

            // 3. 构建msgid到文章详情的映射
            Map<String, Map<String, Object>> detailMap = buildDetailMap(detailData);

            // 4. 构建msgid到文章基础信息的映射
            Map<String, Map<String, Object>> articleMap = buildArticleMap(articleData);

            // 5. 合并数据
            List<Object[]> batchArgs = mergeData(detailMap, articleMap, accountId);

            if (!CollectionUtils.isEmpty(batchArgs)) {
                // 6. 先删除该账号的旧数据
                deleteOldData(accountId);

                // 7. 批量插入新数据
                batchInsertData(batchArgs);

                log.info("成功聚合并插入数据条数: {}", batchArgs.size());
            } else {
                log.warn("没有需要插入的数据");
            }

            log.info("<================= 聚合内容数据到DWS层完成 =================>");

        } catch (Exception e) {
            log.error("聚合内容数据到DWS层失败: ", e);
            throw new RuntimeException("聚合内容数据到DWS层失败: " + e.getMessage(), e);
        }
    }

    /**
     * 步骤1：聚合文章详情日粒度数据
     * 替代原SQL中的子查询d
     */
    private List<Map<String, Object>> aggregateArticleDetailData(Long accountId) {
        String sql = "SELECT " +
                "msgid, " +
                "argMax(title, tuple(stat_date, update_time)) AS detail_title, " +
                "argMax(ref_date, tuple(stat_date, update_time)) AS ref_date, " +
                "max(stat_date) AS latest_stat_date, " +
                "argMax(read_user, tuple(stat_date, update_time)) AS read_user_total, " +
                "argMax(read_user_source_all, tuple(stat_date, update_time)) AS read_user_source_all, " +
                "argMax(read_user_source_msg, tuple(stat_date, update_time)) AS read_user_source_msg, " +
                "argMax(read_user_source_chat, tuple(stat_date, update_time)) AS read_user_source_chat, " +
                "argMax(read_user_source_moments, tuple(stat_date, update_time)) AS read_user_source_moments, " +
                "argMax(read_user_source_homepage, tuple(stat_date, update_time)) AS read_user_source_homepage, " +
                "argMax(read_user_source_other, tuple(stat_date, update_time)) AS read_user_source_other, " +
                "argMax(read_user_source_recommend, tuple(stat_date, update_time)) AS read_user_source_recommend, " +
                "argMax(read_user_source_search, tuple(stat_date, update_time)) AS read_user_source_search, " +
                "argMax(share_user, tuple(stat_date, update_time)) AS share_user, " +
                "argMax(read_subscribe_user, tuple(stat_date, update_time)) AS read_subscribe_user, " +
                "argMax(url, tuple(stat_date, update_time)) AS detail_url, " +
                "account_id " +
                "FROM ods_article_detail_daily " +
                "WHERE account_id = ? " +
                "GROUP BY msgid, account_id";

        return clickhouseService.readData(sql, accountId);
    }

    /**
     * 步骤2：聚合文章基础信息数据
     * 替代原SQL中的子查询a
     */
    private List<Map<String, Object>> aggregateArticleData(Long accountId) {
        String sql = "SELECT " +
                "msgid, " +
                "account_id, " +
                "argMax(title, tuple(update_time, create_time)) AS article_title, " +
                "argMax(create_time, tuple(update_time, create_time)) AS article_create_time, " +
                "argMax(author, tuple(update_time, create_time)) AS article_author, " +
                "argMax(url, tuple(update_time, create_time)) AS article_url " +
                "FROM ods_article " +
                "WHERE account_id = ? " +
                "GROUP BY msgid, account_id";

        return clickhouseService.readData(sql, accountId);
    }

    /**
     * 构建文章详情映射
     */
    private Map<String, Map<String, Object>> buildDetailMap(List<Map<String, Object>> detailData) {
        Map<String, Map<String, Object>> detailMap = new HashMap<>();

        for (Map<String, Object> detail : detailData) {
            String msgid = (String) detail.get("msgid");
            if (msgid != null && !msgid.isEmpty()) {
                // 处理msgid后缀（如果需要）
                String baseMsgid = extractBaseMsgid(msgid);
                detailMap.put(baseMsgid, detail);
            }
        }

        return detailMap;
    }

    /**
     * 构建文章信息映射
     */
    private Map<String, Map<String, Object>> buildArticleMap(List<Map<String, Object>> articleData) {
        if (CollectionUtils.isEmpty(articleData)) {
            return new HashMap<>();
        }

        Map<String, Map<String, Object>> articleMap = new HashMap<>();

        for (Map<String, Object> article : articleData) {
            String msgid = (String) article.get("msgid");
            if (msgid != null && !msgid.isEmpty()) {
                // 处理msgid后缀（如果需要）
                String baseMsgid = extractBaseMsgid(msgid);
                articleMap.put(baseMsgid, article);
            }
        }

        return articleMap;
    }

    /**
     * 步骤5：合并数据
     */
    private List<Object[]> mergeData(Map<String, Map<String, Object>> detailMap,
                                     Map<String, Map<String, Object>> articleMap,
                                     Long accountId) {
        List<Object[]> batchArgs = new ArrayList<>();

        for (Map.Entry<String, Map<String, Object>> entry : detailMap.entrySet()) {
            String msgid = entry.getKey();
            Map<String, Object> detail = entry.getValue();
            Map<String, Object> article = articleMap.get(msgid);

            // 构建插入数据
            Object[] rowData = buildRowData(msgid, detail, article, accountId);
            if (rowData != null) {
                batchArgs.add(rowData);
            }
        }

        return batchArgs;
    }

    /**
     * 构建单行插入数据
     */
    private Object[] buildRowData(String msgid,
                                  Map<String, Object> detail,
                                  Map<String, Object> article,
                                  Long accountId) {
        try {
            // 判断使用哪个数据源
            boolean hasArticle = article != null && article.get("msgid") != null;

            // 字段顺序必须与INSERT语句完全一致
            return new Object[] {
                    // title
                    hasArticle ?
                            getStringValue(article, "article_title") :
                            getStringValue(detail, "detail_title"),

                    // create_time
                    hasArticle ?
                            convertToDate(getStringValue(article, "article_create_time")) :
                            getDateValue(detail, "ref_date"),

                    // latest_stat_date
                    getDateValue(detail, "latest_stat_date"),

                    // msgid
                    msgid,

                    // author
                    hasArticle ? getStringValue(article, "article_author") : "",

                    // read_user_total
                    getLongValue(detail, "read_user_total", 0L),

                    // read_user_source_all
                    getLongValue(detail, "read_user_source_all", 0L),

                    // read_user_source_msg
                    getLongValue(detail, "read_user_source_msg", 0L),

                    // read_user_source_chat
                    getLongValue(detail, "read_user_source_chat", 0L),

                    // read_user_source_moments
                    getLongValue(detail, "read_user_source_moments", 0L),

                    // read_user_source_homepage
                    getLongValue(detail, "read_user_source_homepage", 0L),

                    // read_user_source_other
                    getLongValue(detail, "read_user_source_other", 0L),

                    // read_user_source_recommend
                    getLongValue(detail, "read_user_source_recommend", 0L),

                    // read_user_source_search
                    getLongValue(detail, "read_user_source_search", 0L),

                    // share_user
                    getLongValue(detail, "share_user", 0L),

                    // read_subscribe_user
                    getLongValue(detail, "read_subscribe_user", 0L),

                    // url
                    hasArticle ?
                            getStringValue(article, "article_url") :
                            getStringValue(detail, "detail_url"),

                    // account_id
                    accountId,

                    // pull_time
                    new Date()  // now()
            };
        } catch (Exception e) {
            log.error("构建行数据失败, msgid: {}", msgid, e);
            return null;
        }
    }

    /**
     * 步骤6：删除旧数据
     */
    private void deleteOldData(Long accountId) {
        String deleteSql = SqlUtils.deleteSql("dws_content_data");
        try {
            clickhouseService.singleInsert(deleteSql, accountId);
            log.info("删除accountId={}的旧数据成功", accountId);
        } catch (Exception e) {
            log.warn("删除旧数据失败: {}", e.getMessage());
        }
    }

    /**
     * 步骤7：批量插入数据
     */
    private void batchInsertData(List<Object[]> batchArgs) {
        String insertSql = "INSERT INTO dws_content_data (" +
                "title, create_time, latest_stat_date, msgid, author, " +
                "read_user_total, read_user_source_all, read_user_source_msg, " +
                "read_user_source_chat, read_user_source_moments, " +
                "read_user_source_homepage, read_user_source_other, " +
                "read_user_source_recommend, read_user_source_search, " +
                "share_user, read_subscribe_user, url, account_id, pull_time" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        clickhouseService.batchInsert(insertSql, batchArgs);
    }

    /**
     * 辅助方法：安全获取字符串值
     */
    private String getStringValue(Map<String, Object> map, String key) {
        if (map == null) return "";
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }

    /**
     * 辅助方法：安全获取Long值
     */
    private Long getLongValue(Map<String, Object> map, String key, Long defaultValue) {
        if (map == null) return defaultValue;
        Object value = map.get(key);
        if (value == null) return defaultValue;

        try {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            } else {
                return Long.parseLong(value.toString());
            }
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 辅助方法：安全获取Date值
     */
    private Date getDateValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Date) {
            return (Date) value;
        } else if (value instanceof java.sql.Date) {
            return new Date(((java.sql.Date) value).getTime());
        } else if (value instanceof String) {
            try {
                // 尝试解析字符串日期
                java.sql.Date sqlDate = java.sql.Date.valueOf((String) value);
                return new Date(sqlDate.getTime());
            } catch (Exception e) {
                return new Date(); // 默认当前日期
            }
        }
        return new Date(); // 默认当前日期
    }

    /**
     * 转换字符串为Date
     */
    private Date convertToDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return new Date();
        }
        try {
            java.sql.Date sqlDate = java.sql.Date.valueOf(dateStr);
            return new Date(sqlDate.getTime());
        } catch (Exception e) {
            return new Date();
        }
    }
}

