package com.txwx.webchat.service.impl;

import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.ruoyi.common.redis.service.RedisService;
import com.txwx.webchat.config.WebChatConfig;
import com.txwx.webchat.domain.*;
import com.txwx.webchat.service.IWebChatCaptureService;
import com.txwx.webchat.domain.ArticleShareDaily;
import com.txwx.webchat.util.WebChatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WebChatCaptureServiceImpl implements IWebChatCaptureService {

    private static Logger logger = LoggerFactory.getLogger(WebChatCaptureServiceImpl.class);

    @Autowired
    private WebChatConfig webChatConfig;

    @Autowired
    private ClickhouseService clickhouseService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private WebChatHistoryDataCapture webChatHistoryDataCapture;

    @Override
    public String getAccessToken() {
        String accessToken = null;
        try{
            accessToken = WebChatUtil.getAccessToken(webChatConfig.getAppid(), webChatConfig.getSecret());
        }catch(Exception ex){
            logger.error("获取微信公众号凭据失败:" + ex.getMessage());
        }

        return accessToken;
    }

    @Override
    public void webChatUserCapture(String accessToken) {
        logger.info("<##############################微信公众号用户抓取开始##############################>");
        logger.info("传入的凭证->" + accessToken);

        //(1)定义抓取日期
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayISO = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE);
        logger.info("定义抓取日期:" + yesterdayISO);

        //(2)抓取关注或取消关注人数
        List<WebChatUser> userYesterday = null;
        try{
            userYesterday = WebChatUtil.getUserYesterday(accessToken, yesterdayISO, yesterdayISO);
        }catch (Exception ex){
            logger.error("抓取微信公众号关注或取消用户失败:" + ex.getMessage());
        }

        //(3)将获取的数据插入数据库
        if(userYesterday != null && userYesterday.size() > 0){
            logger.info("<------获取的昨天用户数据条数------> " + userYesterday.size());
            logger.info("<------获取的昨天用户------> " + userYesterday.toString());
            List<Object[]> batchArgs = new ArrayList<>();
            for(WebChatUser user : userYesterday){
                batchArgs.add(user.toObject());
            }

            clickhouseService.batchInsert(webChatConfig.getInsertusersql(), batchArgs);
        }

        logger.info("<##############################微信公众号用户抓取结束##############################>");
    }

    @Override
    public void webChatUserCaptureHistory(String accessToken) {
        LocalDate startDate = LocalDate.of(2025, 7, 24);
        LocalDate endDate = LocalDate.of(2026, 2, 5);

        // 使用ISO_LOCAL_DATE格式器，输出格式为YYYY-MM-DD
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        // 从起始日期循环到结束日期，逐天输出格式化字符串
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            String formattedDate = currentDate.format(formatter);
            System.out.println(formattedDate);

            //(2)抓取关注或取消关注人数
            List<WebChatUser> userYesterday = null;
            try{
                userYesterday = WebChatUtil.getUserYesterday(accessToken, formattedDate, formattedDate);
            }catch (Exception ex){
                System.out.println("抓取微信公众号关注或取消用户失败:" + ex.getMessage());
            }

            //(3)将获取的数据插入数据库
            if(userYesterday != null && userYesterday.size() > 0){
                System.out.println("<------获取的昨天用户数据条数------> " + userYesterday.size());
                System.out.println("<------获取的昨天用户------> " + userYesterday.toString());
                List<Object[]> batchArgs = new ArrayList<>();
                for(WebChatUser user : userYesterday){
                    batchArgs.add(user.toObject());
                }

                clickhouseService.batchInsert(webChatConfig.getInsertusersql(), batchArgs);
            }

            currentDate = currentDate.plusDays(1);
        }
    }

    @Override
    public void webChatArticleUptackCapture(String accessToken) {
        logger.info("<##############################微信公众号每日文章阅读/分享/收藏抓取开始##############################>");
        logger.info("传入的凭证->" + accessToken);

        //(1)定义抓取日期
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayISO = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE);
        logger.info("定义抓取日期:" + yesterdayISO);

        //(2)抓取关注或取消关注人数
        List<WebChatArticleUptackPerday> articleUptackPerday = null;
        try{
            articleUptackPerday = WebChatUtil.getArticleUptackPerday(accessToken, yesterdayISO, yesterdayISO);
        }catch (Exception ex){
            logger.error("抓取微信公众号每日文章阅读/分享/收藏失败:" + ex.getMessage());
        }

        //(3)将获取的数据插入数据库
        if(articleUptackPerday != null && articleUptackPerday.size() > 0){
            logger.info("<------获取的昨天每日文章阅读/分享/收藏数据条数------> " + articleUptackPerday.size());
            logger.info("<------获取的昨天每日文章阅读/分享/收藏------> " + articleUptackPerday.toString());
            List<Object[]> batchArgs = new ArrayList<>();
            for(WebChatArticleUptackPerday article : articleUptackPerday){
                batchArgs.add(article.toObject());
            }

            clickhouseService.batchInsert(webChatConfig.getInsertarticleperdaysql(), batchArgs);
        }

        logger.info("<##############################微信公众号每日文章阅读/分享/收藏抓取结束##############################>");
    }

    @Override
    public void webChatArticleUptackCaptureHistory(String accessToken) {
        LocalDate startDate = LocalDate.of(2025, 7, 24);
        LocalDate endDate = LocalDate.of(2026, 1, 5);

        // 使用ISO_LOCAL_DATE格式器，输出格式为YYYY-MM-DD
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        // 从起始日期循环到结束日期，逐天输出格式化字符串
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            String formattedDate = currentDate.format(formatter);
            System.out.println(formattedDate);

            //(2)抓取关注或取消关注人数
            List<WebChatArticleUptackPerday> articleUptackPerday = null;
            try{
                articleUptackPerday = WebChatUtil.getArticleUptackPerday(accessToken, formattedDate, formattedDate);
            }catch (Exception ex){
                logger.error("抓取微信公众号每日文章阅读/分享/收藏失败:" + ex.getMessage());
            }

            //(3)将获取的数据插入数据库
            if(articleUptackPerday != null && articleUptackPerday.size() > 0){
                logger.info("<------获取的昨天每日文章阅读/分享/收藏数据条数------> " + articleUptackPerday.size());
                logger.info("<------获取的昨天每日文章阅读/分享/收藏------> " + articleUptackPerday.toString());
                List<Object[]> batchArgs = new ArrayList<>();
                for(WebChatArticleUptackPerday article : articleUptackPerday){
                    batchArgs.add(article.toObject());
                }

                clickhouseService.batchInsert(webChatConfig.getInsertarticleperdaysql(), batchArgs);
            }

            currentDate = currentDate.plusDays(1);
        }
    }

    @Override
    public void webChatUserReadCapture(String accessToken) {
        logger.info("<##############################微信公众号每日图文阅读概括数据抓取开始##############################>");
        logger.info("传入的凭证->" + accessToken);

        //(1)定义抓取日期
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayISO = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE);
        logger.info("定义抓取日期:" + yesterdayISO);

        //(2)抓取关注或取消关注人数
        List<WebChatUserRead> userReadPerday = null;
        try{
            userReadPerday = WebChatUtil.getUserReadPerday(accessToken, yesterdayISO, yesterdayISO);
        }catch (Exception ex){
            logger.error("抓取微信公众号每日图文阅读概括数据失败:" + ex.getMessage());
        }

        //(3)将获取的数据插入数据库
        if(userReadPerday != null && userReadPerday.size() > 0){
            logger.info("<------获取的昨天每日图文阅读概括数据条数------> " + userReadPerday.size());
            logger.info("<------获取的昨天每日图文阅读概括数据------> " + userReadPerday.toString());
            List<Object[]> batchArgs = new ArrayList<>();
            for(WebChatUserRead article : userReadPerday){
                batchArgs.add(article.toObject());
            }

            clickhouseService.batchInsert(webChatConfig.getInsertuserreadsql(), batchArgs);
        }

        logger.info("<##############################微信公众号每日图文阅读概括数据抓取结束##############################>");
    }

    @Override
    public void webChatUserReadCaptureHistory(String accessToken) {
        LocalDate startDate = LocalDate.of(2025, 7, 24);
        LocalDate endDate = LocalDate.of(2026, 1, 5);

        // 使用ISO_LOCAL_DATE格式器，输出格式为YYYY-MM-DD
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        // 从起始日期循环到结束日期，逐天输出格式化字符串
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            String formattedDate = currentDate.format(formatter);
            System.out.println(formattedDate);

            //(2)抓取关注或取消关注人数
            List<WebChatUserRead> userReadPerday = null;
            try{
                userReadPerday = WebChatUtil.getUserReadPerday(accessToken, formattedDate, formattedDate);
            }catch (Exception ex){
                logger.error("抓取微信公众号每日图文阅读概括数据失败:" + ex.getMessage());
            }

            //(3)将获取的数据插入数据库
            if(userReadPerday != null && userReadPerday.size() > 0){
                logger.info("<------获取的昨天每日图文阅读概括数据条数------> " + userReadPerday.size());
                logger.info("<------获取的昨天每日图文阅读概括数据------> " + userReadPerday.toString());
                List<Object[]> batchArgs = new ArrayList<>();
                for(WebChatUserRead article : userReadPerday){
                    batchArgs.add(article.toObject());
                }

                clickhouseService.batchInsert(webChatConfig.getInsertuserreadsql(), batchArgs);
            }

            currentDate = currentDate.plusDays(1);
        }
    }

    @Override
    public void capturePublishedArticles(String accessToken) {
        logger.info("<##############################微信公众号已发布消息列表抓取开始##############################>");
        logger.info("传入的凭证->" + accessToken);

        // Redis中存储已发布文章mid的key
        String redisKey = "webchat:published_articles:mid_map";

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

                if (response != null && response.getItem() != null && !response.getItem().isEmpty()) {
                    allItems.addAll(response.getItem());
                    logger.info("获取第 " + (offset / pageSize + 1) + " 页数据，数量: " + response.getItem().size());

                    // 判断是否还有更多数据
                    if (response.getItem_count() < pageSize || allItems.size() >= response.getTotal_count()) {
                        hasMore = false;
                    } else {
                        offset += pageSize;
                    }
                } else {
                    hasMore = false;
                }
            } catch (Exception ex) {
                logger.error("获取已发布消息列表失败，offset: " + offset + ", 错误: " + ex.getMessage());
                hasMore = false;
            }
        }

        logger.info("全量查询完成，总数据量: " + allItems.size());

        // 解析并比对数据
        List<PublishedArticle> newArticles = new ArrayList<>();
        Map<String, String> newMidMap = new HashMap<>();

        for (GetPublishedListResponse.PublishedItem item : allItems) {
            if (item.getContent() == null || item.getContent().getNews_item() == null) {
                continue;
            }

            for (GetPublishedListResponse.NewsItem newsItem : item.getContent().getNews_item()) {
                String url = newsItem.getUrl();
                if (url == null || url.isEmpty()) {
                    continue;
                }

                // 从URL中解析mid
                String mid = extractMidFromUrl(url);
                if (mid == null || mid.isEmpty()) {
                    logger.warn("无法从URL解析mid: " + url);
                    continue;
                }

                // 比对是否为新增数据
                if (!existingMids.contains(mid)) {
                    PublishedArticle article = new PublishedArticle();
                    article.setMid(mid);
                    article.setTitle(newsItem.getTitle());
                    article.setCreateTime(item.getContent().getCreate_time());
                    newArticles.add(article);
                    logger.info("发现新文章 - mid: " + mid + ", title: " + newsItem.getTitle());
                }

                // 更新Redis中的数据（包括已有的和新发现的）
                newMidMap.put(mid, newsItem.getTitle());
            }
        }

        logger.info("发现新文章数量: " + newArticles.size());

        // 将新文章批量插入ClickHouse
        if (!newArticles.isEmpty()) {
            List<Object[]> batchArgs = new ArrayList<>();
            for (PublishedArticle article : newArticles) {
                batchArgs.add(article.toObject());
            }

            String insertSql = webChatConfig.getInsertpublishedarticlesql();
            if (insertSql != null && !insertSql.isEmpty()) {
                try {
                    clickhouseService.batchInsert(insertSql, batchArgs);
                    logger.info("成功插入新文章到ClickHouse，数量: " + batchArgs.size());
                } catch (Exception ex) {
                    logger.error("插入新文章到ClickHouse失败: " + ex.getMessage());
                }
            } else {
                logger.warn("未配置insertpublishedarticlesql，无法插入数据到ClickHouse");
            }
        }

        // 更新Redis中的所有mid（包括已有的和新发现的）
        redisService.setCacheMap(redisKey, newMidMap);
        logger.info("已更新Redis中的文章mid集合，总数: " + newMidMap.size());

        logger.info("<##############################微信公众号已发布消息列表抓取结束##############################>");
    }

    @Override
    public void captureArticleReadDaily(String accessToken) {
        logger.info("<##############################发表内容每日阅读数据抓取开始##############################>");
        logger.info("传入的凭证->" + accessToken);

        // (1)定义抓取日期
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayISO = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE);
        logger.info("定义抓取日期:" + yesterdayISO);

        // (2)抓取发表内容每日阅读数据
        List<ArticleReadDaily> articleReadDailyList = null;
        try {
            articleReadDailyList = WebChatUtil.getArticleReadDaily(accessToken, yesterdayISO, yesterdayISO);
        } catch (Exception ex) {
            logger.error("抓取发表内容每日阅读数据失败:" + ex.getMessage());
        }

        // (3)将获取的数据插入数据库
        if (articleReadDailyList != null && articleReadDailyList.size() > 0) {
            logger.info("<------获取的发表内容每日阅读数据条数------> " + articleReadDailyList.size());
            logger.info("<------获取的发表内容每日阅读数据------> " + articleReadDailyList.toString());
            List<Object[]> batchArgs = new ArrayList<>();
            for (ArticleReadDaily article : articleReadDailyList) {
                batchArgs.add(article.toObject());
            }

            String insertSql = webChatConfig.getInsertarticlereaddailysql();
            if (insertSql != null && !insertSql.isEmpty()) {
                try {
                    clickhouseService.batchInsert(insertSql, batchArgs);
                    logger.info("成功插入发表内容每日阅读数据到ClickHouse，数量: " + batchArgs.size());
                } catch (Exception ex) {
                    logger.error("插入发表内容每日阅读数据到ClickHouse失败: " + ex.getMessage());
                }
            } else {
                logger.warn("未配置insertarticlereaddailysql，无法插入数据到ClickHouse");
            }
        }

        logger.info("<##############################发表内容每日阅读数据抓取结束##############################>");
    }

    @Override
    public void captureArticleSummaryDaily(String accessToken) {
        logger.info("<##############################发表内容概况总数据抓取开始##############################>");
        logger.info("传入的凭证->" + accessToken);

        // (1)定义抓取日期
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayISO = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE);
        logger.info("定义抓取日期:" + yesterdayISO);

        // (2)抓取发表内容概况总数据
        List<ArticleSummaryDaily> articleSummaryDailyList = null;
        try {
            articleSummaryDailyList = WebChatUtil.getArticleSummaryDaily(accessToken, yesterdayISO, yesterdayISO);
        } catch (Exception ex) {
            logger.error("抓取发表内容概况总数据失败:" + ex.getMessage());
        }

        // (3)将获取的数据插入数据库
        if (articleSummaryDailyList != null && articleSummaryDailyList.size() > 0) {
            logger.info("<------获取的发表内容概况总数据条数------> " + articleSummaryDailyList.size());
            logger.info("<------获取的发表内容概况总数据------> " + articleSummaryDailyList.toString());
            List<Object[]> batchArgs = new ArrayList<>();
            for (ArticleSummaryDaily article : articleSummaryDailyList) {
                batchArgs.add(article.toObject());
            }

            String insertSql = webChatConfig.getInsertarticlesummarydailysql();
            if (insertSql != null && !insertSql.isEmpty()) {
                try {
                    clickhouseService.batchInsert(insertSql, batchArgs);
                    logger.info("成功插入发表内容概况总数据到ClickHouse，数量: " + batchArgs.size());
                } catch (Exception ex) {
                    logger.error("插入发表内容概况总数据到ClickHouse失败: " + ex.getMessage());
                }
            } else {
                logger.warn("未配置insertarticlesummarydailysql，无法插入数据到ClickHouse");
            }
        }

        logger.info("<##############################发表内容概况总数据抓取结束##############################>");
    }

    @Override
    public void captureArticleShareDaily(String accessToken) {
        logger.info("<##############################发表内容每日分享数据抓取开始##############################>");
        logger.info("传入的凭证->" + accessToken);

        // (1)定义抓取日期
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayISO = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE);
        logger.info("定义抓取日期:" + yesterdayISO);

        // (2)抓取发表内容每日分享数据
        List<ArticleShareDaily> articleShareDailyList = null;
        try {
            articleShareDailyList = WebChatUtil.getArticleShareDaily(accessToken, yesterdayISO, yesterdayISO);
        } catch (Exception ex) {
            logger.error("抓取发表内容每日分享数据失败:" + ex.getMessage());
        }

        // (3)将获取的数据插入数据库
        if (articleShareDailyList != null && articleShareDailyList.size() > 0) {
            logger.info("<------获取的发表内容每日分享数据条数------> " + articleShareDailyList.size());
            logger.info("<------获取的发表内容每日分享数据------> " + articleShareDailyList.toString());
            List<Object[]> batchArgs = new ArrayList<>();
            for (ArticleShareDaily article : articleShareDailyList) {
                batchArgs.add(article.toObject());
            }

            String insertSql = webChatConfig.getInsertarticlesharedailysql();
            if (insertSql != null && !insertSql.isEmpty()) {
                try {
                    clickhouseService.batchInsert(insertSql, batchArgs);
                    logger.info("成功插入发表内容每日分享数据到ClickHouse，数量: " + batchArgs.size());
                } catch (Exception ex) {
                    logger.error("插入发表内容每日分享数据到ClickHouse失败: " + ex.getMessage());
                }
            } else {
                logger.warn("未配置insertarticlesharedailysql，无法插入数据到ClickHouse");
            }
        }

        logger.info("<##############################发表内容每日分享数据抓取结束##############################>");
    }

    @Override
    public void captureArticleReadDailyHistory(String accessToken) {
        webChatHistoryDataCapture.captureArticleReadDailyHistory(accessToken);
    }

    @Override
    public void captureArticleSummaryDailyHistory(String accessToken) {
        webChatHistoryDataCapture.captureArticleSummaryDailyHistory(accessToken);
    }

    @Override
    public void captureArticleShareDailyHistory(String accessToken) {
        webChatHistoryDataCapture.captureArticleShareDailyHistory(accessToken);
    }

    /**
     * @description: 从URL中解析mid值
     *               URL示例: http://mp.weixin.qq.com/s?__biz=MzE5ODQ1OTMxMg==&mid=2247488911&idx=1&sn=...
     */
    private String extractMidFromUrl(String url) {
        try {
            Pattern pattern = Pattern.compile("mid=([^&]+)");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            logger.error("解析URL中的mid失败: " + url + ", 错误: " + e.getMessage());
        }
        return null;
    }
}
