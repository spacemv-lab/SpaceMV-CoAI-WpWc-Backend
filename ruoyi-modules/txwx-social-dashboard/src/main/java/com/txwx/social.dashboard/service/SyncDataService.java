package com.txwx.social.dashboard.service;

import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.service.RedisService;
import com.txwx.social.dashboard.config.WebChatConfig;
import com.txwx.social.dashboard.domain.*;
import com.txwx.social.dashboard.domain.entity.DwsBizsummaryChannelDaily;
import com.txwx.social.dashboard.mapper.DwsBizsummaryChannelDailyMapper;
import com.txwx.social.dashboard.mapper.DwsContentDataMapper;
import com.txwx.social.dashboard.util.SqlUtils;
import com.txwx.social.dashboard.util.WebChatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class SyncDataService {

    @Autowired
    private WebChatConfig webChatConfig;
    @Autowired
    private ClickhouseService clickhouseService;
    @Autowired
    private DwsBizsummaryChannelDailyMapper dwsBizsummaryChannelDailyMapper;
    @Autowired
    private DwsContentDataMapper dwsContentDataMapper;
    @Autowired
    private RedisService redisService;

    @Autowired
    private ArticleDataAggregator articleDataAggregator;


    @Async("dataSyncExecutor")
    public void syncPlatformData(String accessToken, Long accountId, String startdate, String enddate) {
        try {
            long startTime = System.currentTimeMillis();
            // 同步逻辑
            log.info("开始同步数据..." + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            System.out.println("开始同步数据..." + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            syncUserHistoryRange(accessToken, accountId, startdate, enddate);
            syncArticleSummaryHistoryRange(accessToken, accountId, startdate, enddate);
            syncArticleTotalDetailHistoryRange(accessToken, accountId, startdate, enddate);
            //TODO 下面两个函数还没改造支持时间区间
            // 20260417 把py老师的同步表补回来
            //articleDataAggregator.aggregateDataToDws(accountId);

            log.info("开始同步数据..." + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            System.out.println("停止同步数据..." + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            // 计算同步所用的时间
            long endTime = System.currentTimeMillis();
            long durationMs = endTime - startTime;
            long seconds = durationMs / 1000;
            long minutes = seconds / 60;
            long remainSeconds = seconds % 60;

            System.out.println("本次同步耗时: " + minutes + " 分 " + remainSeconds + " 秒");
            log.info("本次同步耗时: " + minutes + " 分 " + remainSeconds + " 秒");
        } catch (Exception e) {
            log.error("WebChatCaptureServiceImpl.syncPlatformData");
        }
    }

    public void capturePublishedArticles(String accessToken, Long accountId) {
        log.info("<##############################微信公众号已发布消息列表抓取开始##############################>");
        log.info("传入的凭证->" + accessToken);
        // TODO 这个干嘛的感觉读取了全量数据
        // Redis中存储已发布文章mid的key
        String redisKey = "webchat:published_articles:mid_map" + ":" + accountId;

        // 从Redis获取已有的mid集合
        Map<String, String> existingMidMap = redisService.getCacheMap(redisKey);
        Set<String> existingMids = existingMidMap != null ? existingMidMap.keySet() : new HashSet<>();

        // 全量分页查询已发布消息列表
        List<GetPublishedListResponse.PublishedItem> allItems = new ArrayList<>();
        int offset = 0;
        int pageSize = 20;
        boolean hasMore = true;

        while (hasMore) {
            try {
                GetPublishedListResponse response = WebChatUtil.getPublishedList(accessToken, offset, pageSize, 1);

                if (response.getItem() != null && !response.getItem().isEmpty()) {
                    allItems.addAll(response.getItem());
                    log.info("获取第 " + (offset / pageSize + 1) + " 页数据，数量: " + response.getItem().size());

                    // 判断是否还有更多数据
                    if (response.getItem_count() < pageSize || allItems.size() >= response.getTotal_count()) {
                        hasMore = false;
                    } else {
                        offset += pageSize;
                    }
                } else {
                    hasMore = false;
                }
                Thread.sleep(100);
            } catch (Exception ex) {
                log.error("获取已发布消息列表失败，offset: " + offset + ", 错误: " + ex.getMessage());
                hasMore = false;
            }
        }

        log.info("全量查询完成，总数据量: " + allItems.size());

        // 解析并比对数据
        List<PublishedArticle> newArticles = new ArrayList<>();
        Map<String, String> newMidMap = new HashMap<>();

//        for (int i = 0; i < allItems.size(); i++) {
        for (GetPublishedListResponse.PublishedItem item : allItems) {
//            GetPublishedListResponse.PublishedItem item = allItems.get(i);
            if (item.getContent() == null || item.getContent().getNews_item() == null) {
                continue;
            }

            for (int i = 0; i < item.getContent().getNews_item().size(); i++) {
//            for (GetPublishedListResponse.NewsItem newsItem : item.getContent().getNews_item()) {
                GetPublishedListResponse.NewsItem newsItem = item.getContent().getNews_item().get(i);
                String url = newsItem.getUrl();
                if (url == null || url.isEmpty()) {
                    continue;
                }
                // 从URL中解析mid
                String mid = WebChatUtil.extractMidFromUrl(url);
                if (mid == null || mid.isEmpty()) {
                    log.warn("无法从URL解析mid: " + url);
                    continue;
                }
                String midWithIdx = mid + "_" + (i + 1);
                // 比对是否为新增数据
                if (!existingMids.contains(midWithIdx)) {
                    PublishedArticle article = new PublishedArticle();
                    article.setMid(midWithIdx);
                    article.setTitle(newsItem.getTitle());
                    article.setCreateTime(item.getContent().getCreate_time());
                    article.setAuthor(newsItem.getAuthor());
                    article.setUrl(newsItem.getUrl());
                    newArticles.add(article);
                    log.info("发现新文章 - midWithIdx: " + midWithIdx + ", title: " + newsItem.getTitle());
                }

                // 更新Redis中的数据（包括已有的和新发现的）
                newMidMap.put(midWithIdx, newsItem.getTitle());
            }
        }

        log.info("发现新文章数量: " + newArticles.size());

        // 将新文章批量插入ClickHouse
        if (!newArticles.isEmpty()) {
            List<Object[]> batchArgs = new ArrayList<>();
            for (PublishedArticle article : newArticles) {
                batchArgs.add(article.toObject(accountId));
            }

            String insertSql = webChatConfig.getInsertpublishedarticlesql();
            if (insertSql != null && !insertSql.isEmpty()) {
                try {
                    clickhouseService.batchInsert(insertSql, batchArgs);
                    log.info("成功插入新文章到ClickHouse，数量: " + batchArgs.size());
                } catch (Exception ex) {
                    log.error("插入新文章到ClickHouse失败: " + ex.getMessage());
                }
            } else {
                log.warn("未配置insertpublishedarticlesql，无法插入数据到ClickHouse");
            }
        }

        // 更新Redis中的所有mid（包括已有的和新发现的）
        redisService.setCacheMap(redisKey, newMidMap);
        log.info("已更新Redis中的文章mid集合，总数: " + newMidMap.size());

        log.info("<##############################微信公众号已发布消息列表抓取结束##############################>");
    }

    public void syncUserHistoryRange(String accessToken, Long accountId, String startDate, String endDate) {
        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            throw new ServiceException("开始日期和结束日期不能为空");
        }

        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
            end = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            throw new ServiceException("日期格式错误，请使用 yyyy-MM-dd");
        }

        if (start.isAfter(end)) {
            throw new ServiceException("开始日期不能大于结束日期");
        }

        LocalDate today = LocalDate.now();
        if (end.isAfter(today)) {
            end = today;
        }

        log.info("开始执行微信公众号历史区间同步, startDate={}, endDate={}", start, end);

        LocalDate current = start;
        while (!current.isAfter(end)) {
            try {
                syncUserOneDay(accessToken, accountId, current);
            } catch (Exception e) {
                log.error("历史区间同步失败, refDate={}, msg={}", current, e.getMessage(), e);
            }
            current = current.plusDays(1);
        }

        log.info("微信公众号历史区间同步完成, startDate={}, endDate={}", start, end);
    }

    public void syncArticleSummaryHistoryRange(String accessToken, Long accountId, String startDate, String endDate) {
        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            throw new ServiceException("开始日期和结束日期不能为空");
        }

        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
            end = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            throw new ServiceException("日期格式错误，请使用 yyyy-MM-dd");
        }

        if (start.isAfter(end)) {
            throw new ServiceException("开始日期不能大于结束日期");
        }

        LocalDate today = LocalDate.now();
        if (end.isAfter(today)) {
            end = today;
        }

        log.info("开始执行发表内容概况总数据历史区间同步, startDate={}, endDate={}", start, end);

        LocalDate current = start;
        while (!current.isAfter(end)) {
            try {
                syncArticleSummaryDailyOneDay(accessToken, accountId, current);
            } catch (Exception e) {
                log.error("历史区间同步失败, refDate={}, msg={}", current, e.getMessage(), e);
            }
            current = current.plusDays(1);
        }

        log.info("发表内容概况总数据历史区间同步完成, startDate={}, endDate={}", start, end);
    }

    public void syncArticleTotalDetailHistoryRange(String accessToken, Long accountId, String startDate, String endDate) {
        log.info("<##############################发表内容发表详细数据历史区间同步开始##############################>");
        log.info("传入的凭证->{}", accessToken);
        log.info("开始日期={}, 结束日期={}", startDate, endDate);

        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            throw new ServiceException("开始日期和结束日期不能为空");
        }

        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
            end = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            throw new ServiceException("日期格式错误，请使用 yyyy-MM-dd");
        }

        if (start.isAfter(end)) {
            throw new ServiceException("开始日期不能大于结束日期");
        }

        LocalDate today = LocalDate.now();
        if (end.isAfter(today)) {
            end = today;
        }

        LocalDate current = start;
        while (!current.isAfter(end)) {
            try {
                syncArticleDetailOneDay(accessToken, accountId, current);
            } catch (Exception e) {
                log.error("历史区间同步失败, baseDate={}, msg={}", current, e.getMessage(), e);
            }
            current = current.plusDays(1);
        }

        // 区间全部同步完成后，统一聚合一次
        try {
            String delSql = SqlUtils.deleteSql("dws_content_data");
            clickhouseService.singleInsert(delSql, accountId);
            //dwsContentDataMapper.aggregateArticleDetailsDataToDws();
            articleDataAggregator.aggregateContentDataToDws(accountId);
            log.info("成功聚合文章详细数据到 dws_content_data");
        } catch (Exception ex) {
            log.error("聚合 dws_content_data 失败: {}", ex.getMessage(), ex);
        }

        log.info("<##############################发表内容发表详细数据历史区间同步结束##############################>");
    }

    public void syncUserOneDay(String accessToken, Long accountId, LocalDate refDate) {
        String refDateStr = refDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String lasetRefDateStr = refDate.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);

        log.info("<##############################微信公众号用户抓取开始，日期：{} ##############################>", refDateStr);
        log.info("传入的凭证->{}", accessToken);

        if (exist("ods_users", accountId, refDateStr, refDateStr)) {
            log.info("ods_users表抓取日期已存在数据，执行跳过。日期，{}，账号id{}", refDateStr, accountId);
            return;
        }

        // 1. 抓取指定日期数据
        List<WebChatUser> userDayTemp = null;
        try {
            userDayTemp = WebChatUtil.getUserWithDate(accessToken, refDateStr, refDateStr);
        } catch (Exception ex) {
            log.error("抓取微信公众号关注或取消用户失败, refDate={}, msg={}", refDateStr, ex.getMessage(), ex);
        }
        final List<WebChatUser> userDay = userDayTemp;

        // 2. 写入 ods_users
        if (userDay != null && !userDay.isEmpty()) {
            log.info("<------获取的用户数据条数------> {}", userDay.size());
            log.info("<------获取的用户数据------> {}", userDay);

            List<Object[]> batchArgs = new ArrayList<>();
            for (WebChatUser user : userDay) {
                batchArgs.add(user.toObject(accountId));
            }

            clickhouseService.batchInsert(webChatConfig.getInsertusersql(), batchArgs);
        } else {
            log.warn("refDate={} 未获取到公众号用户数据", refDateStr);
        }

        // 3. 汇总写入 dws_users
        try {
            int totalNewUser = 0;
            int totalCancelUser = 0;

            if (userDay != null && !userDay.isEmpty()) {
                totalNewUser = userDay.stream().mapToInt(WebChatUser::getNew_user).sum();
                totalCancelUser = userDay.stream().mapToInt(WebChatUser::getCancel_user).sum();
            }

            log.info("refDate={}, 累加new_user={}, 累加cancel_user={}", refDateStr, totalNewUser, totalCancelUser);

            int netNewUser = totalNewUser - totalCancelUser;
            log.info("refDate={}, 净增新用户数net_new_user={}", refDateStr, netNewUser);

            Number lastAccumulateUser = 0;

            try {
                String querySql = "SELECT accumulated_user " +
                        "FROM dws_users WHERE account_id = ? and ref_date = ?";
                List<Map<String, Object>> result = clickhouseService.readData(querySql, accountId, lasetRefDateStr);

                if (result != null && !result.isEmpty()) {
                    Map<String, Object> row = result.get(0);
                    lastAccumulateUser = (Number) row.get("accumulated_user");
                }
            } catch (Exception ex) {
                log.error("查询ods_users历史累计数据失败, refDate={}, msg={}", refDateStr, ex.getMessage(), ex);
            }

            Long accumulatedUser = lastAccumulateUser.longValue() + (long) netNewUser;
            log.info("refDate={}, 历史累计净增用户accumulated_user={}", refDateStr, accumulatedUser);

            List<Object[]> dwsBatchArgs = new ArrayList<>();
            dwsBatchArgs.add(new Object[]{
                    refDateStr,
                    totalNewUser,
                    totalCancelUser,
                    netNewUser,
                    accumulatedUser,
                    accountId
            });

            String insertDwsSql = webChatConfig.getInsertdwsuserssql();
            if (insertDwsSql != null && !insertDwsSql.isEmpty()) {
                try {
                    String deleteDwsSql = SqlUtils.deleteSqlWithDate("dws_users");
                    clickhouseService.singleInsert(deleteDwsSql, accountId, refDate, refDateStr);
                    clickhouseService.batchInsert(insertDwsSql, dwsBatchArgs);
                    log.info("成功插入dws_users表数据, refDate={}", refDateStr);
                } catch (Exception ex) {
                    log.error("插入dws_users表失败, refDate={}, msg={}", refDateStr, ex.getMessage(), ex);
                }
            } else {
                log.warn("未配置insertdwsuserssql，无法插入数据到dws_users表, refDate={}", refDateStr);
            }
        } catch (Exception ex) {
            log.error("处理dws_users数据汇总失败, refDate={}, msg={}", refDateStr, ex.getMessage(), ex);
        }

        log.info("<##############################微信公众号用户抓取结束，日期：{} ##############################>", refDateStr);
    }

    public void syncArticleSummaryDailyOneDay(String accessToken, Long accountId, LocalDate refDate) {
        String refDateStr = refDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        log.info("<##############################发表内容概况总数据抓取开始，日期：{} ##############################>", refDateStr);
        log.info("传入的凭证->{}", accessToken);

        if (exist("ods_article_summary_daily", accountId, refDateStr, refDateStr)) {
            log.info("ods_article_summary_daily表抓取日期已存在数据，执行跳过。日期，{}，账号id{}", refDateStr, accountId);
            return;
        }

        // 1. 抓取指定日期数据
        List<ArticleSummaryDaily> articleSummaryDailyList = null;
        try {
            articleSummaryDailyList = WebChatUtil.getArticleSummaryDaily(accessToken, refDateStr, refDateStr);
        } catch (Exception ex) {
            log.error("抓取发表内容概况总数据失败, refDate={}, msg={}", refDateStr, ex.getMessage(), ex);
        }

        // 2. 写入 ClickHouse 明细表
        if (articleSummaryDailyList != null && !articleSummaryDailyList.isEmpty()) {
            log.info("<------获取的发表内容概况总数据条数------> {}", articleSummaryDailyList.size());
            log.info("<------获取的发表内容概况总数据------> {}", articleSummaryDailyList);

            List<Object[]> batchArgs = new ArrayList<>();
            for (ArticleSummaryDaily article : articleSummaryDailyList) {
                batchArgs.add(article.toObject(accountId));
            }

            String insertSql = webChatConfig.getInsertarticlesummarydailysql();
            if (insertSql != null && !insertSql.isEmpty()) {
                try {
                    clickhouseService.batchInsert(insertSql, batchArgs);
                    log.info("成功插入发表内容概况总数据到ClickHouse，refDate={}, 数量={}", refDateStr, batchArgs.size());
                } catch (Exception ex) {
                    log.error("插入发表内容概况总数据到ClickHouse失败, refDate={}, msg={}", refDateStr, ex.getMessage(), ex);
                }
            } else {
                log.warn("未配置insertarticlesummarydailysql，无法插入数据到ClickHouse, refDate={}", refDateStr);
            }
        } else {
            log.warn("refDate={} 未获取到发表内容概况总数据", refDateStr);
        }

        // 3. 汇聚到 dws_bizsummary_channel_daily
        try {
            String deleteSql = SqlUtils.deleteSqlWithDate("dws_bizsummary_channel_daily");
            clickhouseService.singleInsert(deleteSql, accountId, refDateStr, refDateStr);

            if (articleSummaryDailyList != null && !articleSummaryDailyList.isEmpty()) {
                List<DwsBizsummaryChannelDaily> dwsBatchArgs = new ArrayList<>();
                for (ArticleSummaryDaily article : articleSummaryDailyList) {
                    dwsBatchArgs.addAll(article.toDwsContentData(accountId));
                }

                if (!dwsBatchArgs.isEmpty()) {
                    dwsBizsummaryChannelDailyMapper.insertBatch(dwsBatchArgs);
                    log.info("成功汇聚到 dws_bizsummary_channel_daily，refDate={}, 数量={}", refDateStr, dwsBatchArgs.size());
                }
            }
        } catch (Exception ex) {
            log.error("汇聚 dws_bizsummary_channel_daily 失败, refDate={}, msg={}", refDateStr, ex.getMessage(), ex);
        }

        log.info("<##############################发表内容概况总数据抓取结束，日期：{} ##############################>", refDateStr);
    }

    public void syncArticleDetailOneDay(String accessToken, Long accountId, LocalDate baseDate) {
        log.info("<##############################发表内容发表详细数据单天同步开始##############################>");
        log.info("传入的凭证->{}", accessToken);
        log.info("基准日期->{}", baseDate);

        String insertSql = webChatConfig.getInsertarticledetaildailysql();
        if (insertSql == null || insertSql.isEmpty()) {
            log.error("ClickHouse 插入 SQL 未配置，任务终止");
            return;
        }

        List<Object[]> allBatchArgs = new ArrayList<>();

        String refDateStr = baseDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        // 1. 首先查询当前数据在库里有没有，如果有了就跳过不同步
        if (exist("ods_article_detail_daily", accountId, refDateStr, refDateStr)) {
            log.info("ods_article_detail_daily表抓取日期已存在数据，执行跳过。日期，{}，账号id{}", refDateStr, accountId);
            return;
        }
        log.info("正在抓取发布日期为 [{}] 的文章数据", refDateStr);

        List<ArticleDetailDaily> articleDetailDailyList = null;
        try {
            articleDetailDailyList = WebChatUtil.getArticleDetailDaily(accessToken, refDateStr, refDateStr);
        } catch (Exception ex) {
            log.error("抓取发表内容发表详细数据失败, refDate={}, msg={}", refDateStr, ex.getMessage(), ex);
        }

        if (articleDetailDailyList != null && !articleDetailDailyList.isEmpty()) {
            for (ArticleDetailDaily articleDetailDaily : articleDetailDailyList) {
                allBatchArgs.addAll(articleDetailDaily.toFlattenObjectList(accountId));
            }
        }

        try {
            if (!allBatchArgs.isEmpty()) {
                clickhouseService.batchInsert(insertSql, allBatchArgs);
                log.info("成功插入发表内容发表详细数据到ClickHouse，数量={}", allBatchArgs.size());
            } else {
                log.warn("本次回溯区间内未获取到任何文章详细数据");
            }
        } catch (Exception ex) {
            log.error("插入发表内容发表详细数据到ClickHouse失败: {}", ex.getMessage(), ex);
        }

        log.info("<##############################发表内容发表详细数据单天同步结束##############################>");
    }


    // Java示例：先查询后插入
    public boolean exist(String tableName, Long accountId, String startdate, String enddate) {
        String checkSql = "SELECT 1 FROM " + tableName +
                " WHERE account_id = ? AND ref_date BETWEEN ? AND ? LIMIT 1";
        try {
            List<Map<String, Object>> result = clickhouseService.readData(checkSql, accountId, startdate, enddate);
            if (result != null && !result.isEmpty()) {
                return true;
            }
        } catch (Exception e) {
            log.error("insertIfNotExists error", e);
        }
        return false;
    }
}

