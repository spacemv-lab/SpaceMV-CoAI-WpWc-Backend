/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.dashboard.service;

import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.service.RedisService;
import com.txwx.social.dashboard.config.WebChatConfig;
import com.txwx.social.dashboard.domain.*;
import com.txwx.social.dashboard.mapper.DwsBizsummaryChannelDailyMapper;
import com.txwx.social.dashboard.mapper.DwsContentDataMapper;
import com.txwx.social.dashboard.util.DateValidator;
import com.txwx.social.dashboard.util.WebChatUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    private ArticleChannelAggregationService aggregateArticleChannelDwsData;


    @Async("dataSyncExecutor")
    public void syncPlatformData(String accessToken, Long accountId, String startdate, String enddate) {
        try {
            long startTime = System.currentTimeMillis();
            // 同步逻辑
            log.info("开始同步数据..." + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            // 用户趋势统计接口
            syncUserWithDate(accessToken, accountId, startdate, enddate);
            // 文章阅读数据原始抓取
            syncArticleReadDaily(accessToken, accountId, startdate, enddate);
            // 文章分享数据抓取
            processArticleShareDailyData(accessToken, accountId, startdate, enddate);
            // 文章元数据抓取
            syncPublishedArticles(accessToken, accountId);

            //文章核心统计接口，时间范围跨度30天查询
            syncArticleSummaryHistoryRange(accessToken, accountId, startdate, enddate);
            // 微信侧统计接口，这个只统计发表日期最长30天的数据 TODO qyl这个接口后期需要换 ods_article_detail_daily
            syncArticleTotalDetailHistoryRange(accessToken, accountId, startdate, enddate);

            // 汇总用户维度数据
            articleDataAggregator.aggregateDwsUsers(accountId);
            // 汇聚文章数据 dws_article_read
            articleDataAggregator.aggregateDataToDws(accountId);
            // qyl汇聚文章数据dws_content_data
            articleDataAggregator.aggregateArticleContentDataToDws(accountId);
            // 汇聚文章渠道数据
            aggregateArticleChannelDwsData.executeFullAggregation(accountId);


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
            log.error("WebChatCaptureServiceImpl.syncPlatformData", e);
        }
    }


    public void processArticleShareDailyData(String accessToken, Long accountId, String startdate, String enddate) {
        List<String> missingDates = getMissingDates("ods_article_share_daily", accountId, startdate, enddate);
        if (CollectionUtils.isEmpty(missingDates)) {
            return;
        }

        List<ArticleShareDaily> articleShareDailyList = Lists.newArrayList();
        for (String missingDate : missingDates) {
            List<ArticleShareDaily> tmpShareList = Lists.newArrayList();
            try {
                tmpShareList = WebChatUtil.getArticleShareDaily(accessToken, missingDate, missingDate);
            } catch (Exception ex) {
                log.error("抓取发表内容每日分享数据失败:" + ex.getMessage());
            }
            if (CollectionUtils.isEmpty(tmpShareList)) {
                continue;
            }
            articleShareDailyList.addAll(tmpShareList);
        }

        if (CollectionUtils.isEmpty(articleShareDailyList)) {
            return;
        }

        // (3)将获取的数据插入数据库
        List<Object[]> batchArgs = new ArrayList<>();
        for (ArticleShareDaily article : articleShareDailyList) {
            batchArgs.add(article.toObject(accountId));
        }

        String insertSql = webChatConfig.getInsertarticlesharedailysql();
        if (insertSql != null && !insertSql.isEmpty()) {
            try {
                clickhouseService.batchInsert(insertSql, batchArgs);
                log.info("成功插入发表内容每日分享数据到ClickHouse，数量: " + batchArgs.size());
            } catch (Exception ex) {
                log.error("插入发表内容每日分享数据到ClickHouse失败: " + ex.getMessage());
            }
        } else {
            log.warn("未配置insertarticlesharedailysql，无法插入数据到ClickHouse");
        }
    }

    public void syncArticleReadDaily(String accessToken, Long accountId, String startdate, String enddate) {

        List<String> missingDates = getMissingDates("ods_article_read_daily", accountId, startdate, enddate);
        if (CollectionUtils.isEmpty(missingDates)) {
            return;
        }

        List<ArticleReadDaily> articleReadDailyList = Lists.newArrayList();
        for (String missingDate : missingDates) {
            List<ArticleReadDaily> tmpReadList = Lists.newArrayList();
            try {
                tmpReadList = WebChatUtil.getArticleReadDaily(accessToken, missingDate, missingDate);
            } catch (Exception ex) {
                log.error("抓取发表内容每日阅读数据失败:" + ex.getMessage());
            }
            if (CollectionUtils.isEmpty(tmpReadList)) {
                continue;
            }
            articleReadDailyList.addAll(tmpReadList);
        }


        List<Object[]> batchArgs = new ArrayList<>();

        if (CollectionUtils.isEmpty(articleReadDailyList)) {
            return;
        }

        articleReadDailyList.forEach(readDaily -> {
            batchArgs.add(readDaily.toObject(accountId));
        });



        String insertSql = webChatConfig.getInsertarticlereaddailysql();
        if (!StringUtils.hasText(insertSql)) {
            log.error("getInsertarticlereaddailysql sql语句未拿到配置信息");
            return;
        }
        try {
            clickhouseService.batchInsert(insertSql, batchArgs);
            log.info("成功插入发表内容每日阅读数据到ClickHouse，数量: " + batchArgs.size());
        } catch (Exception ex) {
            log.error("插入发表内容每日阅读数据到ClickHouse失败: " + ex.getMessage());
        }
    }

    /**
     * @param accessToken
     * @param accountId
     */
    public void capturePublishedArticles(String accessToken, Long accountId) {
        log.info("<##############################微信公众号已发布消息列表抓取开始##############################>");
        log.info("传入的凭证->" + accessToken);
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

        for (GetPublishedListResponse.PublishedItem item : allItems) {
            if (item.getContent() == null || item.getContent().getNews_item() == null) {
                continue;
            }

            for (int i = 0; i < item.getContent().getNews_item().size(); i++) {
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


    /**
     * @param accessToken
     * @param accountId
     */
    public void syncPublishedArticles(String accessToken, Long accountId) {
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

        List<String> existingDates = getExistDates("ods_article", accountId, "create_time");
        List<PublishedArticle> newArticles = new ArrayList<>();

        for (GetPublishedListResponse.PublishedItem item : allItems) {
            if (item.getContent() == null || item.getContent().getNews_item() == null) {
                continue;
            }

            for (int i = 0; i < item.getContent().getNews_item().size(); i++) {
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
                if (!existingDates.contains(midWithIdx)) {
                    PublishedArticle article = new PublishedArticle();
                    article.setMid(midWithIdx);
                    article.setTitle(newsItem.getTitle());
                    article.setCreateTime(item.getContent().getCreate_time());
                    article.setAuthor(newsItem.getAuthor());
                    article.setUrl(newsItem.getUrl());
                    newArticles.add(article);
                    log.info("发现新文章 - midWithIdx: " + midWithIdx + ", title: " + newsItem.getTitle());
                }

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

        log.info("<##############################微信公众号已发布消息列表抓取结束##############################>");
    }

    @Deprecated
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
                //syncUserWithDate(accessToken, accountId, current);
            } catch (Exception e) {
                log.error("历史区间同步失败, refDate={}, msg={}", current, e.getMessage(), e);
            }
            current = current.plusDays(1);
        }

        log.info("微信公众号历史区间同步完成, startDate={}, endDate={}", start, end);
    }

    /**
     * 按30天分片查询，三方接口不支持30天以上的跨度查询
     * @param accessToken
     * @param accountId
     * @param startDate
     * @param endDate
     */
    public void syncArticleSummaryHistoryRange(String accessToken, Long accountId, String startDate, String endDate) {
        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            throw new ServiceException("开始日期和结束日期不能为空");
        }

        LocalDate start = DateValidator.parseAndValidateDate(startDate, "开始日期");
        LocalDate end = DateValidator.parseAndValidateDate(endDate, "结束日期");

        validateDateRange(start, end);
        end = adjustEndDateIfAfterToday(end);

        log.info("开始执行发表内容概况总数据历史区间同步, startDate={}, endDate={}, accountId={}",
                startDate, endDate, accountId);

        // 使用切片处理逻辑
        processDateRangeInSlices(accessToken, accountId, start, end);
    }


    /**
     * 验证日期范围
     */
    private void validateDateRange(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new ServiceException("开始日期不能大于结束日期");
        }
    }

    /**
     * 如果结束日期晚于今天，则调整为今天
     */
    private LocalDate adjustEndDateIfAfterToday(LocalDate end) {
        LocalDate today = LocalDate.now();
        return end.isAfter(today) ? today : end;
    }

    /**
     * 按30天跨度切片处理日期范围
     */
    private void processDateRangeInSlices(String accessToken, Long accountId, LocalDate start, LocalDate end) {
        long totalDays = ChronoUnit.DAYS.between(start, end) + 1;

        if (totalDays <= 30) {
            // 日期跨度在30天以内，直接处理
            processDateSlice(accessToken, accountId, start, end, 1, 1);
            return;
        }

        // 超过30天，进行切片处理
        LocalDate sliceStart = start;
        int sliceCount = 0;
        int totalSlices = (int) Math.ceil(totalDays / 30.0);

        while (sliceStart.isBefore(end) || sliceStart.isEqual(end)) {
            sliceCount++;

            // 计算当前切片的结束日期
            LocalDate sliceEnd = sliceStart.plusDays(29);
            if (sliceEnd.isAfter(end)) {
                sliceEnd = end;
            }

            processDateSlice(accessToken, accountId, sliceStart, sliceEnd, sliceCount, totalSlices);

            // 下一个切片开始日期
            sliceStart = sliceEnd.plusDays(1);
        }

        log.info("发表内容概况总数据历史区间同步完成, 共处理 {} 个切片, 总日期范围: {} 到 {}",
                sliceCount, start, end);
    }

    /**
     * 处理单个日期切片
     */
    private void processDateSlice(String accessToken, Long accountId,
                                  LocalDate sliceStart, LocalDate sliceEnd,
                                  int currentSlice, int totalSlices) {
        String sliceStartStr = sliceStart.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String sliceEndStr = sliceEnd.format(DateTimeFormatter.ISO_LOCAL_DATE);

        String progressInfo = totalSlices > 1 ?
                String.format("(%d/%d)", currentSlice, totalSlices) : "";

        log.info("执行切片同步{}: {} 到 {}", progressInfo, sliceStartStr, sliceEndStr);

        try {
            syncArticleSummaryDailyOneMonth(accessToken, accountId, sliceStartStr, sliceEndStr);
            log.info("切片同步{}完成: {} 到 {}", progressInfo, sliceStartStr, sliceEndStr);
        } catch (Exception e) {
            log.error("切片同步{}失败: {} 到 {}, 错误信息={}",
                    progressInfo, sliceStartStr, sliceEndStr, e.getMessage(), e);
            throw new ServiceException(String.format(
                    "切片同步失败(%d/%d): %s 到 %s",
                    currentSlice, totalSlices, sliceStartStr, sliceEndStr), 501);
        }
    }

    /**
     * 按天分片，三方接口只支持单天查询
     * 但是因为要查询30天，所以要往前推30天
     * @param accessToken
     * @param accountId
     * @param startDate
     * @param endDate
     */
    public void syncArticleTotalDetailHistoryRange(String accessToken, Long accountId, String startDate, String endDate) {
        log.info("<##############################发表内容发表详细数据历史区间同步开始##############################>");
       
        log.info("开始日期={}, 结束日期={}", startDate, endDate);

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
        // 按照30天往前推30天看数据，因为这个传入的日期是计算的发表日期
        LocalDate current = start.minusDays(30);
        LocalDate before = LocalDate.of(2025,11,1);
        // 需要处理最早数据为2025-11-01
        if (current.isBefore(before)) {
            current = before;
        }
        while (!current.isAfter(end)) {
            try {
                String curStr = current.format(DateTimeFormatter.ISO_LOCAL_DATE);
                syncArticleDetailWithdate(accessToken, accountId, curStr);
            } catch (Exception e) {
                log.error("历史区间同步失败, baseDate={}, msg={}", current, e.getMessage(), e);
            }
            current = current.plusDays(1);
        }
    }

    public void syncUserWithDate(String accessToken, Long accountId, String startdate, String enddate) {

        log.info("<##############################微信公众号用户抓取开始，开始日期：{}, 结束日期:{} ##############################>", startdate, enddate);
   

        List<String> missingDates = getMissingDates("ods_users", accountId, startdate, enddate);
        if (CollectionUtils.isEmpty(missingDates)) {
            log.info("ods_users表抓取日期已存在数据，执行跳过。开始日期，{}，结束日期{}, 账号id{}", startdate, enddate, accountId);
            return;
        }
        // 1. 抓取指定日期数据
        List<WebChatUser> userDayTemp = null;
        try {
            userDayTemp = WebChatUtil.getUserWithDate(accessToken, startdate, enddate);
        } catch (Exception ex) {
            log.error("抓取微信公众号关注或取消用户失败, startdate={}, enddate={}, msg={}", startdate, enddate, ex.getMessage(), ex);
        }

        if (CollectionUtils.isEmpty(userDayTemp)) {
            return;
        }

        Map<String, List<WebChatUser>> date2UserMap = userDayTemp.stream()
                .filter(article -> article.getRef_date() != null)  // 过滤掉refDate为null的数据
                .collect(Collectors.groupingBy(
                        WebChatUser::getRef_date,  // 按refDate分组
                        Collectors.toList()                // 收集为List
                ));

        log.info("<------获取的用户数据条数------> {}", userDayTemp.size());

        List<Object[]> batchArgs = new ArrayList<>();

        // 2. 写入 ods_users
        date2UserMap.forEach((refDate, userDays) -> {
            if (!missingDates.contains(refDate)) {
                return;
            }
            // 只插入没有的数据
            userDays.forEach(user -> {
                batchArgs.add(user.toObject(accountId));
            });
        });


        clickhouseService.batchInsert(webChatConfig.getInsertusersql(), batchArgs);

        log.info("<##############################微信公众号用户抓取结束，开始日期：{}，结束关系：{} ##############################>", startdate, enddate);
    }


    public void syncArticleSummaryDailyOneMonth(String accessToken, Long accountId, String startdate, String refDateStr) {

        log.info("<##############################发表内容概况总数据抓取开始，日期：{} ##############################>", refDateStr);
    

        List<String> missingDates = getMissingDates("ods_article_summary_daily", accountId, startdate, refDateStr);
        if (CollectionUtils.isEmpty(missingDates)) {
            log.info("ods_article_summary_daily表抓取日期已存在数据，执行跳过。开始日期，{}，结束日期{}, 账号id{}", startdate, refDateStr, accountId);
            return;
        }

        // 1. 抓取指定日期数据
        List<ArticleSummaryDaily> articleSummaryDailyList = null;
        try {
            // 这是一个统计接口，统计时间最大跨度为30天
            articleSummaryDailyList = WebChatUtil.getArticleSummaryDaily(accessToken, startdate, refDateStr);
        } catch (Exception ex) {
            log.error("抓取发表内容概况总数据失败, refDate={}, msg={}", refDateStr, ex.getMessage(), ex);
        }

        if (CollectionUtils.isEmpty(articleSummaryDailyList)) {
            return;
        }

        Map<String, List<ArticleSummaryDaily>> date2ArticleMap = articleSummaryDailyList.stream()
                .filter(article -> article.getRef_date() != null)  // 过滤掉refDate为null的数据
                .collect(Collectors.groupingBy(
                        ArticleSummaryDaily::getRef_date,  // 按refDate分组
                        Collectors.toList()                // 收集为List
                ));

        log.info("<------获取的发表内容概况总数据条数------> {}", articleSummaryDailyList.size());
        log.info("<------获取的发表内容概况总数据------> {}", articleSummaryDailyList);

        List<Object[]> batchArgs = new ArrayList<>();

        date2ArticleMap.forEach((refDate, articles) -> {
            if (!missingDates.contains(refDate)) {
                return;
            }

            articles.forEach(article -> {
                batchArgs.add(article.toObject(accountId));
            });
        });

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

        log.info("<##############################发表内容概况总数据抓取结束，日期：{} ##############################>", refDateStr);
    }

    public void syncArticleDetailWithdate(String accessToken, Long accountId, String curdate) {
        log.info("<##############################发表内容发表详细数据单天同步开始##############################>");
    
        log.info("开始日期->{}， 结束日期->{}", curdate, curdate);

        String insertSql = webChatConfig.getInsertarticledetaildailysql();
        if (insertSql == null || insertSql.isEmpty()) {
            log.error("ClickHouse 插入 SQL 未配置，任务终止");
            return;
        }

        List<Object[]> allBatchArgs = new ArrayList<>();
        log.info("正在抓取发布日期范围为 [{}-{}] 的文章数据", curdate, curdate);

        List<ArticleDetailDaily> articleDetailDailyList = null;
        try {
            // 这是一个统计接口，最大统计周期为30天，但是只能传时间跨度为1的入参
            articleDetailDailyList = WebChatUtil.getArticleDetailDaily(accessToken, curdate, curdate);
        } catch (Exception ex) {
            log.error("抓取发表内容发表详细数据失败, startdate={}, enddate={}, msg={}", curdate, curdate, ex.getMessage(), ex);
            return;
        }

        if (CollectionUtils.isEmpty(articleDetailDailyList)) {
            log.warn("抓取发表内容发表详细数据为空, startdate={}, enddate={}", curdate, curdate);
            return;
        }

        for (ArticleDetailDaily articleDetailDaily : articleDetailDailyList) {
            allBatchArgs.addAll(articleDetailDaily.toFlattenObjectList(accountId));
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


    /**
     * 检查指定时间范围内每一天是否有数据
     * 返回缺失的日期列表，如果列表为空表示所有日期都有数据
     */
    public List<String> getMissingDates(String tableName, Long accountId, String startdate, String enddate) {
        List<String> missingDates = new ArrayList<>();

        try {
            // 1. 生成日期范围内的所有日期
            List<String> allDates = generateDateRange(startdate, enddate);

            if (allDates.isEmpty()) {
                return missingDates;
            }

            // 2. 查询数据库中存在的日期
            String sql = "SELECT DISTINCT ref_date FROM " + tableName +
                    " WHERE account_id = ? AND ref_date BETWEEN ? AND ?";

            List<Map<String, Object>> result = clickhouseService.readData(sql, accountId, startdate, enddate);

            // 3. 提取数据库中已存在的日期
            Set<String> existingDates = new HashSet<>();
            for (Map<String, Object> row : result) {
                Object dateObj = row.get("ref_date");
                if (dateObj != null) {
                    existingDates.add(dateObj.toString());
                }
            }

            // 4. 找出缺失的日期
            for (String date : allDates) {
                if (!existingDates.contains(date)) {
                    missingDates.add(date);
                }
            }

            if (!missingDates.isEmpty()) {
                log.info("表{}中accountId={}在{}到{}范围内缺失{}天的数据: {}",
                        tableName, accountId, startdate, enddate, missingDates.size(), missingDates);
            }

        } catch (Exception e) {
            log.error("检查缺失日期失败, tableName={}, accountId={}, startdate={}, enddate={}, msg={}",
                    tableName, accountId, startdate, enddate, e.getMessage(), e);
        }

        return missingDates;
    }

    /**
     * 检查指定时间范围内每一天是否有数据
     * 返回缺失的日期列表，如果列表为空表示所有日期都有数据
     */
    public List<String> getExistDates(String tableName, Long accountId, String dateFeild) {
        List<String> existDates = new ArrayList<>();

        try {

            // 2. 查询数据库中存在的日期
            String sql = "SELECT DISTINCT "+ dateFeild+" FROM " + tableName +
                    " WHERE account_id = ? ";

            List<Map<String, Object>> result = clickhouseService.readData(sql, accountId);

            // 3. 提取数据库中已存在的日期
            Set<String> existingDates = new HashSet<>();
            for (Map<String, Object> row : result) {
                Object dateObj = row.get("ref_date");
                if (dateObj != null) {
                    existingDates.add(dateObj.toString());
                }
            }
            existDates.addAll(existingDates);
        } catch (Exception e) {
            log.error("检查缺失日期失败, tableName={}, accountId={}, msg={}",
                    tableName, accountId, e.getMessage(), e);
        }

        return existDates;
    }

    /**
     * 生成日期范围内的所有日期
     */
    private List<String> generateDateRange(String startdate, String enddate) {
        List<String> dateList = new ArrayList<>();

        try {
            LocalDate start = LocalDate.parse(startdate);
            LocalDate end = LocalDate.parse(enddate);

            LocalDate current = start;
            while (!current.isAfter(end)) {
                dateList.add(current.toString());
                current = current.plusDays(1);
            }
        } catch (Exception e) {
            log.error("生成日期范围失败, startdate={}, enddate={}, msg={}",
                    startdate, enddate, e.getMessage(), e);
        }

        return dateList;
    }


}

