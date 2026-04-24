package com.txwx.social.dashboard.service;

import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.txwx.social.dashboard.util.DateValidator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 文章渠道数据聚合服务(Spring Boot版)
 * 从ods_article_detail_daily表聚合数据到dws_bizsummary_channel_daily表
 */
@Service
@Slf4j
public class ArticleChannelAggregationService {

    @Autowired
    private ClickhouseService clickhouseService;

    // 分批处理配置
    private static final int BATCH_SIZE = 10000; // 每批处理的数据量
    private static final int INSERT_BATCH_SIZE = 1000; // 批量插入的大小

    // 渠道定义
    private static final String[] CHANNELS = {
            "全部", "公众号消息", "聊天会话", "朋友圈",
            "公众号主页", "其他", "推荐", "搜一搜"
    };

    // 渠道字段映射
    private static final Map<String, String> CHANNEL_FIELD_MAP = new HashMap<>();

    static {
        // 初始化渠道与字段的映射关系
        CHANNEL_FIELD_MAP.put("全部", "read_user_source_all");
        CHANNEL_FIELD_MAP.put("公众号消息", "read_user_source_msg");
        CHANNEL_FIELD_MAP.put("聊天会话", "read_user_source_chat");
        CHANNEL_FIELD_MAP.put("朋友圈", "read_user_source_moments");
        CHANNEL_FIELD_MAP.put("公众号主页", "read_user_source_homepage");
        CHANNEL_FIELD_MAP.put("其他", "read_user_source_other");
        CHANNEL_FIELD_MAP.put("推荐", "read_user_source_recommend");
        CHANNEL_FIELD_MAP.put("搜一搜", "read_user_source_search");
    }

    /**
     * 渠道聚合中间结果
     */
    @Data
    public static class ChannelAggregation {
        private LocalDate refDate;
        private Long accountId;
        private String channel;
        private AtomicLong readUserCnt = new AtomicLong(0);
        private AtomicLong shareUser = new AtomicLong(0);
        private AtomicLong collectionCount = new AtomicLong(0);
        private AtomicLong collectionUser = new AtomicLong(0);
        private AtomicLong sendPageCount = new AtomicLong(0);

        public ChannelAggregation(LocalDate refDate, Long accountId, String channel) {
            this.refDate = refDate;
            this.accountId = accountId;
            this.channel = channel;
        }

        public void addReadUserCnt(long count) {
            readUserCnt.addAndGet(count);
        }

        public void addShareUser(long count) {
            shareUser.addAndGet(count);
        }

        public void addCollectionCount(long count) {
            collectionCount.addAndGet(count);
        }

        public void addCollectionUser(long count) {
            collectionUser.addAndGet(count);
        }

        public void addSendPageCount(long count) {
            sendPageCount.addAndGet(count);
        }

        public ChannelSummary toSummary() {
            return new ChannelSummary(
                    refDate,
                    accountId,
                    channel,
                    readUserCnt.get(),
                    shareUser.get(),
                    0L,  // redirect_ori_page_count
                    0L,  // redirect_ori_page_user
                    collectionCount.get(),
                    collectionUser.get(),
                    sendPageCount.get()
            );
        }
    }

    /**
     * 渠道聚合结果DTO
     */
    @Data
    public static class ChannelSummary {
        private LocalDate refDate;
        private Long accountId;
        private String channel;
        private Long readUserCnt;
        private Long shareUser;
        private Long redirectOriPageCount;
        private Long redirectOriPageUser;
        private Long collectionCount;
        private Long collectionUser;
        private Long sendPageCount;

        public ChannelSummary(LocalDate refDate, Long accountId, String channel,
                              Long readUserCnt, Long shareUser, Long redirectOriPageCount,
                              Long redirectOriPageUser, Long collectionCount, Long collectionUser,
                              Long sendPageCount) {
            this.refDate = refDate;
            this.accountId = accountId;
            this.channel = channel;
            this.readUserCnt = readUserCnt;
            this.shareUser = shareUser;
            this.redirectOriPageCount = redirectOriPageCount;
            this.redirectOriPageUser = redirectOriPageUser;
            this.collectionCount = collectionCount;
            this.collectionUser = collectionUser;
            this.sendPageCount = sendPageCount;
        }

        public Object[] toInsertParams() {
            return new Object[] {
                    refDate,
                    accountId,
                    channel,
                    readUserCnt,
                    shareUser,
                    redirectOriPageCount,
                    redirectOriPageUser,
                    collectionCount,
                    collectionUser,
                    sendPageCount,
                    LocalDateTime.now()  // pull_time
            };
        }

        @Override
        public String toString() {
            return String.format("ChannelSummary{refDate=%s, accountId=%d, channel='%s', " +
                            "readUserCnt=%d, shareUser=%d, sendPageCount=%d}",
                    refDate, accountId, channel, readUserCnt, shareUser, sendPageCount);
        }
    }

    /**
     * 获取数据总行数
     */
    public long getTotalRowCount(Long accountId) {
        String countSql = "SELECT COUNT(*) as total FROM wcai.ods_article_detail_daily Where account_id=?";
        Map<String, Object> result = clickhouseService.readData(countSql, accountId).stream().findFirst().orElse(null);
        return result != null ? ((Number) result.get("total")).longValue() : 0L;
    }

    /**
     * 分批读取数据
     */
    public List<Map<String, Object>> readBatchData(long offset, Long accountId) {
        String querySql = "SELECT ref_date, account_id, msgid, publish_type, " +
                "read_user_source_all, read_user_source_msg, read_user_source_chat, " +
                "read_user_source_moments, read_user_source_homepage, read_user_source_other, " +
                "read_user_source_recommend, read_user_source_search, " +
                "share_user, collection_user " +
                "FROM wcai.ods_article_detail_daily " +
                "WHERE account_id = ? " +
                "ORDER BY ref_date, account_id, msgid " +
                "LIMIT ? OFFSET ?";

        return clickhouseService.readData(querySql, accountId, BATCH_SIZE, offset);
    }

    /**
     * 处理单行数据
     */
    private void processRow(Map<String, Object> row,
                            ConcurrentHashMap<String, ChannelAggregation> aggregationMap) {
        // 解析基础字段
        LocalDate refDate = row.get("ref_date") != null ?
                ((java.sql.Date) row.get("ref_date")).toLocalDate() : null;
        Long accountId = row.get("account_id") != null ?
                ((Number) row.get("account_id")).longValue() : null;
        Integer publishType = row.get("publish_type") != null ?
                ((Number) row.get("publish_type")).intValue() : null;
        String msgid = (String) row.get("msgid");

        if (refDate == null || accountId == null || msgid == null) {
            return; // 跳过无效数据
        }

        // 解析数值字段
        long shareUser = row.get("share_user") != null ?
                ((Number) row.get("share_user")).longValue() : 0L;
        long collectionUser = row.get("collection_user") != null ?
                ((Number) row.get("collection_user")).longValue() : 0L;

        // 计算总阅读用户数
        long totalRead = row.get("read_user_source_all") != null ?
                ((Number) row.get("read_user_source_all")).longValue() : 0L;

        // 处理每个渠道
        for (String channel : CHANNELS) {
            String fieldName = CHANNEL_FIELD_MAP.get(channel);
            if (fieldName == null) continue;

            long readUserCnt = row.get(fieldName) != null ?
                    ((Number) row.get(fieldName)).longValue() : 0L;

            if (readUserCnt > 0) {
                String key = String.format("%s_%d_%s",
                        refDate.toString(), accountId, channel);

                // 获取或创建聚合对象
                ChannelAggregation aggregation = aggregationMap.computeIfAbsent(key, k ->
                        new ChannelAggregation(refDate, accountId, channel)
                );

                // 累加阅读用户数
                aggregation.addReadUserCnt(readUserCnt);

                // 累加分享用户数（按渠道阅读比例分配）
                if (shareUser > 0 && totalRead > 0) {
                    double ratio = (double) readUserCnt / totalRead;
                    long channelShareUser = Math.round(shareUser * ratio);
                    aggregation.addShareUser(channelShareUser);
                }

                // 累加收藏用户数
                if (collectionUser > 0 && totalRead > 0) {
                    double ratio = (double) readUserCnt / totalRead;
                    long channelCollectionUser = Math.round(collectionUser * ratio);
                    aggregation.addCollectionUser(channelCollectionUser);
                    aggregation.addCollectionCount(channelCollectionUser);
                }

                // 累加群发篇数
                if (publishType != null && publishType == 1) {
                    aggregation.addSendPageCount(1);
                }
            }
        }
    }

    /**
     * 处理一批数据
     */
    private void processBatch(List<Map<String, Object>> batchData,
                              ConcurrentHashMap<String, ChannelAggregation> aggregationMap) {
        for (Map<String, Object> row : batchData) {
            processRow(row, aggregationMap);
        }
    }

    /**
     * 执行全量数据聚合
     */
    public void executeFullAggregation(Long accountId) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("全量聚合");

        try {
            // 获取总数据量
            long totalRows = getTotalRowCount(accountId);
            log.info("开始处理全量数据，总行数: {}", totalRows);

            // 存储聚合结果的Map
            ConcurrentHashMap<String, ChannelAggregation> aggregationMap = new ConcurrentHashMap<>();

            // 分批读取和处理数据
            long processedRows = 0;
            int batchNumber = 0;

            while (processedRows < totalRows) {
                batchNumber++;
                log.info("处理第{}批数据，offset: {}", batchNumber, processedRows);

                // 读取一批数据
                List<Map<String, Object>> batchData = readBatchData(processedRows, accountId);

                if (batchData.isEmpty()) {
                    break;
                }

                // 处理这批数据
                processBatch(batchData, aggregationMap);

                log.info("第{}批数据处理完成，处理了{}行", batchNumber, batchData.size());
                processedRows += BATCH_SIZE;

                // 定期输出进度
                if (batchNumber % 10 == 0) {
                    log.info("当前进度: {}/{} {}",
                            Math.min(processedRows, totalRows), totalRows,
                            (double) Math.min(processedRows, totalRows) / totalRows * 100);
                }
            }

            // 转换为结果列表
            List<ChannelSummary> results = aggregationMap.values().stream()
                    .map(ChannelAggregation::toSummary)
                    .collect(Collectors.toList());

            log.info("聚合完成，共处理了{}行数据，生成{}条聚合结果%n",
                    Math.min(processedRows, totalRows), results.size());

            // 保存聚合结果
            saveAggregationResults(results);

            stopWatch.stop();
            log.info("全量聚合完成，总耗时: {}秒",
                    stopWatch.getTotalTimeSeconds());


        } catch (Exception e) {
            log.error("全量聚合失败: ", e);
        }
    }

    /**
     * 保存聚合结果到ClickHouse
     */
    private void saveAggregationResults(List<ChannelSummary> results) {
        if (results == null || results.isEmpty()) {
            System.out.println("没有聚合结果需要保存");
            return;
        }

        System.out.println("开始保存聚合结果到ClickHouse...");

        String insertSql = "INSERT INTO wcai.dws_bizsummary_channel_daily " +
                "(ref_date, account_id, channel, read_user_cnt, share_user, " +
                "redirect_ori_page_count, redirect_ori_page_user, collection_count, " +
                "collection_user, send_page_count, pull_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // 分批插入
        int totalCount = results.size();
        int batchCount = 0;

        for (int i = 0; i < totalCount; i += INSERT_BATCH_SIZE) {
            int end = Math.min(i + INSERT_BATCH_SIZE, totalCount);
            List<ChannelSummary> batch = results.subList(i, end);

            List<Object[]> batchArgs = batch.stream()
                    .map(ChannelSummary::toInsertParams)
                    .collect(Collectors.toList());

            clickhouseService.batchInsert(insertSql, batchArgs);
            batchCount += batch.size();

            log.info("已批量插入{}条记录，进度: {}/{}",
                    batch.size(), batchCount, totalCount);
        }

        log.info("聚合结果保存完成，共保存了{}条记录", batchCount);
    }

    /**
     * 按日期范围聚合数据
     * @param startDateStr 开始日期
     * @param endDateStr 结束日期
     */
    public void executeRangeAggregation(Long accountId, String startDateStr, String endDateStr) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("日期范围聚合");
        LocalDate startDate = DateValidator.parseAndValidateDate(startDateStr, "开始日期");
        LocalDate endDate = DateValidator.parseAndValidateDate(endDateStr, "结束日期");

        try {
            // 查询指定日期范围的数据
            String querySql =  "SELECT ref_date, account_id, msgid, publish_type, " +
                    "read_user_source_all, read_user_source_msg, read_user_source_chat, " +
                    "read_user_source_moments, read_user_source_homepage, read_user_source_other, " +
                    "read_user_source_recommend, read_user_source_search, " +
                    "share_user, collection_user " +
                    "FROM wcai.ods_article_detail_daily " +
                    "WHERE account_id = ? AND ref_date >= ? AND ref_date <= ? " +
                    "ORDER BY account_id, ref_date, msgid";

            log.info("开始处理日期范围数据: {} ~ {}", startDate, endDate);

            List<Map<String, Object>> allData = clickhouseService.readData(querySql, accountId, startDate, endDate);
            log.info("读取到{}行数据", allData.size());

            // 存储聚合结果的Map
            ConcurrentHashMap<String, ChannelAggregation> aggregationMap = new ConcurrentHashMap<>();

            // 处理所有数据
            processBatch(allData, aggregationMap);

            // 转换为结果列表
            List<ChannelSummary> results = aggregationMap.values().stream()
                    .map(ChannelAggregation::toSummary)
                    .collect(Collectors.toList());

            log.info("聚合完成，生成{}条聚合结果", results.size());

            // 保存聚合结果
            saveAggregationResults(results);

            stopWatch.stop();
            log.info("日期范围聚合完成，总耗时: {}秒",
                    stopWatch.getTotalTimeSeconds());
        } catch (Exception e) {
            log.error("日期范围聚合失败: ", e);
        }
    }


}

