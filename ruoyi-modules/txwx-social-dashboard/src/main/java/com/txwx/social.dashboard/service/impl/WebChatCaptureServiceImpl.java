package com.txwx.social.dashboard.service.impl;

import com.google.gson.Gson;
import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.CheckedException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.sign.RsaUtils;
import com.ruoyi.common.redis.service.RedisService;
import com.txwx.social.api.client.AccountApiClient;
import com.txwx.social.api.domain.dto.AccountDTO;
import com.txwx.social.dashboard.config.WebChatConfig;
import com.txwx.social.dashboard.domain.*;
import com.txwx.social.dashboard.mapper.DwsBizsummaryChannelDailyMapper;
import com.txwx.social.dashboard.mapper.DwsContentDataMapper;
import com.txwx.social.dashboard.service.*;
import com.txwx.social.dashboard.util.DateValidator;
import com.txwx.social.dashboard.util.SqlUtils;
import com.txwx.social.dashboard.util.WebChatUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
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

    @Autowired
    private ArticleDataAggregator articleDataAggregator;
    @Autowired
    private IOdsUsersService iOdsUsersService;
    @Autowired
    private IDwsUsersService iDwsUsersService;
    @Autowired
    private DwsContentDataMapper dwsContentDataMapper;
    @Autowired
    private DwsBizsummaryChannelDailyMapper dwsBizsummaryChannelDailyMapper;

    @Autowired
    private AccountApiClient accountApiClient;

    @Autowired
    private SyncDataService syncDataService;


    @Override
    public String getAccessToken(Long accountId) {
        String accessToken = null;
        try{
            R<AccountDTO> remoteRes = accountApiClient.getAccountById(accountId);
            if (remoteRes != null) {
                AccountDTO accountDTO = remoteRes.getData();
                String appId = accountDTO.getAppId();
                String secret = accountDTO.getSecret();
                return WebChatUtil.getAccessToken(appId, secret);
            } else {
                //return WebChatUtil.getAccessToken(webChatConfig.getAppid(), RsaUtils.decryptByPrivateKey(webChatConfig.getSecret()));
                return WebChatUtil.getAccessToken(webChatConfig.getAppid(), webChatConfig.getSecret());
            }
        }catch(Exception ex){
            logger.error("获取微信公众号凭据失败:" + ex.getMessage());
        }

        return accessToken;
    }

    @Override
    public Boolean dataSync(Long accountId, String startdate, String enddate) {
        DateValidator.validate(startdate, enddate);
        if (accountId == null) {
            throw new CheckedException("平台不存在!");
        }
        String accessToken = getAccessToken(accountId);
        if (StringUtils.isEmpty(accessToken)) {
            throw new CheckedException("appId及secret验证失败!");
        }
        // 异步同步数据
        syncDataService.syncPlatformData(accessToken, accountId, startdate, enddate);
        log.info("#########异步调用完成返回给前端");
        return true;
    }

    @Override
    public void webChatUserCapture(Long accountId) {
        String accessToken = getAccessToken(accountId);
        webChatUserCapture(accessToken, accountId);
    }


    @Override
    public void webChatUserCapture(String accessToken, Long accountId) {
        logger.info("<##############################微信公众号每日文章阅读/分享/收藏抓取开始##############################>");
        logger.info("传入的凭证->" + accessToken);

        //(1)定义抓取日期
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayISO = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE);
        processChatUserData(accessToken, accountId, yesterdayISO);
    }

    @Override
    public void webChatUserCaptureHistory(String startdate, String endDate, Long accountId) {
        String accessToken = getAccessToken(accountId);
        webChatUserCaptureHistory(startdate, endDate, accessToken, accountId);
    }

    private boolean validateHistoryParams(String startdate, String endDate, String accessToken, Long accountId) {
        if (StringUtils.isBlank(startdate) || StringUtils.isBlank(endDate)
                || StringUtils.isBlank(accessToken) || accountId == null) {
            System.out.println("参数不完整");
            return false;
        }
        return true;
    }

    @Override
    public void webChatUserCaptureHistory(String startdate, String endDate, String accessToken, Long accountId) {
        if (!validateHistoryParams(startdate, endDate, accessToken, accountId)) {
            throw new RuntimeException("请检查传入参数");
        }

        // 将日期字符串解析为 LocalDate
        LocalDate start = LocalDate.parse(startdate, DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD));

        // 验证日期范围
        if (start.isAfter(end)) {
            throw new RuntimeException("开始时间不能玩意结束时间");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        // 从起始日期循环到结束日期，逐天输出格式化字符串
        LocalDate currentDate = start;
        while (!currentDate.isAfter(end)) {
            String formattedDate = currentDate.format(formatter);
            processChatUserData(accessToken, accountId, formattedDate);
            currentDate = currentDate.plusDays(1);
        }
    }

    private void processChatUserData(String accessToken, Long accountId, String formattedDate) {
        //(2)抓取关注或取消关注人数
        List<WebChatUser> userReadPerday = null;
        try{
            userReadPerday = WebChatUtil.getUserWithDate(accessToken, formattedDate, formattedDate);
        }catch (Exception ex){
            logger.error("抓取微信公众号每日图文阅读概括数据失败:" + ex.getMessage());
        }

        //(3)将获取的数据插入数据库
        if(!CollectionUtils.isEmpty(userReadPerday)){
            processOdsUserData(userReadPerday, accountId);
        }
    }

    private void processOdsUserData(List<WebChatUser> allUserData, Long accountId) {
        Map<String, List<WebChatUser>> day2UserDataMap = allUserData.stream()
                .collect(Collectors.groupingBy(WebChatUser::getRef_date));
        System.out.println("<------获取的历史用户数据条数------> " + allUserData.size());
        System.out.println("<------获取的历史用户------> " + new Gson().toJson(day2UserDataMap));
        List<Object[]> batchArgs = new ArrayList<>();
        String sql = "SELECT 1 FROM ods_users WHERE ref_date = ? LIMIT 1";
        day2UserDataMap.forEach((date, users) -> {
            //(2)查询当日是否已有数据
            if (!StringUtils.isEmpty(date)) {
                List<Map<String, Object>> queryData = clickhouseService.readData(sql, date);
                if (!CollectionUtils.isEmpty(queryData)) {
                    return;
                }
                for(WebChatUser user : users){
                    batchArgs.add(user.toObject(accountId));
                }
                //(3)将获取的数据插入数据库
                clickhouseService.batchInsert(webChatConfig.getInsertusersql(), batchArgs);
            }

        });
    }


    @Override
    public void webChatArticleUptackCapture(Long accountId) {
        String accessToken = getAccessToken(accountId);
        webChatArticleUptackCapture(accessToken, accountId);
    }

    @Override
    public void webChatArticleUptackCaptureHistory(String startdate, String enddate, Long accountId) {
        String accessToken = getAccessToken(accountId);
        webChatArticleUptackCaptureHistory(startdate, enddate, accessToken, accountId);
    }

    @Override
    public void webChatUserReadCapture(Long accountId) {
        String accessToken = getAccessToken(accountId);
        webChatUserReadCapture(accessToken, accountId);
    }

    @Override
    public void webChatUserReadCaptureHistory(String startdate, String enddate, Long accountId) {
        String accessToken = getAccessToken(accountId);
        webChatUserReadCaptureHistory(startdate, enddate, accessToken, accountId);
    }

    @Override
    public void capturePublishedArticles(Long accountId) {
        String accessToken = getAccessToken(accountId);
        syncDataService.capturePublishedArticles(accessToken, accountId);
    }

    @Override
    public void captureArticleReadDaily(Long accountId) {
        String accessToken = getAccessToken(accountId);
        captureArticleReadDaily(accessToken, accountId);
    }

    @Override
    public void captureArticleTotalDetailDaily(Long accountId) {
        String accessToken = getAccessToken(accountId);
        captureArticleTotalDetailDaily(accessToken, accountId);
    }

    @Deprecated
    @Override
    public void captureArticleTotalDetailDaily(String accessToken, Long accountId) {
        logger.info("<##############################发表内容发表详细数据抓取开始##############################>");
        logger.info("传入的凭证->" + accessToken);

        String insertSql = webChatConfig.getInsertarticledetaildailysql();
        if (insertSql == null || insertSql.isEmpty()) {
            logger.error("ClickHouse 插入 SQL 未配置，任务终止");
            return;
        }

        List<Object[]> allBatchArgs = new ArrayList<>();
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayFormat = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE);
        List<ArticleDetailDaily> articleDetailDailyList = null;
        try {
            articleDetailDailyList = WebChatUtil.getArticleDetailDaily(accessToken, yesterdayFormat, yesterdayFormat);
            System.out.println("########看看原始数据##########" + new Gson().toJson(articleDetailDailyList));
        } catch (Exception ex) {
            logger.error("抓取发表内容发表详细数据失败:" + ex.getMessage());
        }

        if (articleDetailDailyList != null && !articleDetailDailyList.isEmpty()) {
            logger.info("<------获取的发表内容发表详细数据条数------> " + articleDetailDailyList.size());
            for (ArticleDetailDaily articleDetailDaily : articleDetailDailyList) {
                allBatchArgs.addAll(articleDetailDaily.toFlattenObjectList(accountId));
            }
        }

        if (!allBatchArgs.isEmpty()) {
            try {
                clickhouseService.batchInsert(insertSql, allBatchArgs);
                logger.info("成功插入发表内容发表详细数据到ClickHouse，数量: " + allBatchArgs.size());
            } catch (Exception ex) {
                logger.error("插入发表内容发表详细数据到ClickHouse失败: " + ex.getMessage());
            }
        }

        // 将数据聚合到 dws_content_data
        String delSql = SqlUtils.deleteSqlWithDate("dws_content_data");
        clickhouseService.singleInsert(delSql, accountId, yesterdayFormat, yesterdayFormat);
        dwsContentDataMapper.aggregateArticleDetailsDataToDws();

        logger.info("<##############################发表内容发表详细数据抓取结束##############################>");
    }

    @Override
    public void webChatArticleUptackCapture(String accessToken, Long accountId) {
        logger.info("<##############################微信公众号每日文章阅读/分享/收藏抓取开始##############################>");
        logger.info("传入的凭证->" + accessToken);

        //(1)定义抓取日期
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayISO = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE);
        logger.info("定义抓取日期:" + yesterdayISO);

        processChatArticleUptackData(accessToken, accountId, yesterdayISO);

        logger.info("<##############################微信公众号每日文章阅读/分享/收藏抓取结束##############################>");
    }

    private void processChatArticleUptackData(String accessToken, Long accountId, String yesterdayISO) {
        //(2)抓取关注或取消关注人数
        List<WebChatArticleUptackPerday> articleUptackPerday = null;
        try{
            articleUptackPerday = WebChatUtil.getArticleUptackPerday(accessToken, yesterdayISO, yesterdayISO);
        }catch (Exception ex){
            logger.error("抓取微信公众号每日文章阅读/分享/收藏失败:" + ex.getMessage());
        }

        //(3)将获取的数据插入数据库
        if(articleUptackPerday != null && !articleUptackPerday.isEmpty()){
            logger.info("<------获取的昨天每日文章阅读/分享/收藏数据条数------> " + articleUptackPerday.size());
            logger.info("<------获取的昨天每日文章阅读/分享/收藏------> " + new Gson().toJson(articleUptackPerday));
            List<Object[]> batchArgs = new ArrayList<>();
            for(WebChatArticleUptackPerday article : articleUptackPerday){
                batchArgs.add(article.toObject(accountId));
            }

            clickhouseService.batchInsert(webChatConfig.getInsertarticleperdaysql(), batchArgs);
        }
    }

    @Override
    public void webChatArticleUptackCaptureHistory(String startdate, String enddate, String accessToken, Long accountId) {
        if (!validateHistoryParams(startdate, enddate, accessToken, accountId)) {
            throw new RuntimeException("请检查传入参数");
        }

        // 将日期字符串解析为 LocalDate
        LocalDate start = LocalDate.parse(startdate, DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD));
        LocalDate end = LocalDate.parse(enddate, DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD));

        // 验证日期范围
        if (start.isAfter(end)) {
            throw new RuntimeException("开始时间不能玩意结束时间");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        // 从起始日期循环到结束日期，逐天输出格式化字符串
        LocalDate currentDate = start;
        while (!currentDate.isAfter(end)) {
            String formattedDate = currentDate.format(formatter);

            processChatArticleUptackData(accessToken, accountId, formattedDate);

            currentDate = currentDate.plusDays(1);
        }
    }

    @Override
    public void webChatUserReadCapture(String accessToken, Long accountId) {
        logger.info("<##############################微信公众号每日图文阅读概括数据抓取开始##############################>");
        logger.info("传入的凭证->" + accessToken);

        //(1)定义抓取日期
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayISO = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE);

        processUserReadPerdayData(accessToken, accountId, yesterdayISO);

        logger.info("<##############################微信公众号每日图文阅读概括数据抓取结束##############################>");

    }

    @Override
    public void webChatUserReadCaptureHistory(String startdate, String enddate, String accessToken, Long accountId) {

        if (!validateHistoryParams(startdate, enddate, accessToken, accountId)) {
            throw new RuntimeException("请检查传入参数");
        }

        // 将日期字符串解析为 LocalDate
        LocalDate start = LocalDate.parse(startdate, DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD));
        LocalDate end = LocalDate.parse(enddate, DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD));

        // 验证日期范围
        if (start.isAfter(end)) {
            throw new RuntimeException("开始时间不能玩意结束时间");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        // 从起始日期循环到结束日期，逐天输出格式化字符串
        LocalDate currentDate = start;
        while (!currentDate.isAfter(end)) {
            processUserReadPerdayData(accessToken, accountId, currentDate.format(formatter));
            currentDate = currentDate.plusDays(1);
        }


    }

    @Deprecated
    private void processUserReadPerdayData(String accessToken, Long accountId, String date) {
        //(2)抓取关注或取消关注人数
        List<WebChatUserRead> userReadPerday = null;
        try{
            userReadPerday = WebChatUtil.getUserReadPerday(accessToken, date, date);
        }catch (Exception ex){
            logger.error("抓取微信公众号每日图文阅读概括数据失败:" + ex.getMessage());
        }

        //(3)将获取的数据插入数据库
        if(userReadPerday != null && userReadPerday.size() > 0){
            logger.info("<------获取的昨天每日图文阅读概括数据条数------> " + userReadPerday.size());
            logger.info("<------获取的昨天每日图文阅读概括数据------> " + userReadPerday);
            List<Object[]> batchArgs = new ArrayList<>();
            for(WebChatUserRead article : userReadPerday){
                batchArgs.add(article.toObject(accountId));
            }

            clickhouseService.batchInsert(webChatConfig.getInsertuserreadsql(), batchArgs);
        }
    }

    @Override
    public void captureArticleReadDaily(String accessToken, Long accountId) {
        logger.info("<##############################发表内容每日阅读数据抓取开始##############################>");
        logger.info("传入的凭证->" + accessToken);

        // (1)定义抓取日期
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayISO = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE);
        logger.info("定义抓取日期:" + yesterdayISO);

        // (2)抓取发表内容每日阅读数据
        syncDataService.syncArticleReadDaily(accessToken, accountId, yesterdayISO, yesterdayISO);

        logger.info("<##############################发表内容每日阅读数据抓取结束##############################>");
    }




    @Override
    public void captureArticleShareDaily(String accessToken, Long accountId) {
        logger.info("<##############################发表内容每日分享数据抓取开始##############################>");
        logger.info("传入的凭证->" + accessToken);

        // (1)定义抓取日期
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayISO = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE);
        logger.info("定义抓取日期:" + yesterdayISO);

        // (2)抓取发表内容每日分享数据
        syncDataService.processArticleShareDailyData(accessToken, accountId, yesterdayISO, yesterdayISO);

        logger.info("<##############################发表内容每日分享数据抓取结束##############################>");
    }



    @Override
    public void captureArticleReadDailyHistory(String startdate, String enddate, String accessToken, Long accountId) {
        if (!validateHistoryParams(startdate, enddate, accessToken, accountId)) {
            throw new RuntimeException("请检查传入参数");
        }

        // 将日期字符串解析为 LocalDate
        LocalDate start = LocalDate.parse(startdate, DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD));
        LocalDate end = LocalDate.parse(enddate, DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD));

        // 验证日期范围
        if (start.isAfter(end)) {
            throw new RuntimeException("开始时间不能玩意结束时间");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        // 从起始日期循环到结束日期，逐天输出格式化字符串
        LocalDate currentDate = start;
        while (!currentDate.isAfter(end)) {
            String formattedDate = currentDate.format(formatter);
            // (2)抓取发表内容每日阅读数据
            syncDataService.syncArticleReadDaily(accessToken, accountId, formattedDate, formattedDate);
            currentDate = currentDate.plusDays(1);
        }

        logger.info("<##############################发表内容历史阅读数据抓取结束##############################>");
    }

    @Override
    public void captureArticleSummaryDailyHistory(String startdate, String enddate, String accessToken, Long accountId) {
        if (!validateHistoryParams(startdate, enddate, accessToken, accountId)) {
            throw new RuntimeException("请检查传入参数");
        }

        // 将日期字符串解析为 LocalDate
        LocalDate start = LocalDate.parse(startdate, DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD));
        LocalDate end = LocalDate.parse(enddate, DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD));

        // 验证日期范围
        if (start.isAfter(end)) {
            throw new RuntimeException("开始时间不能玩意结束时间");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        // 从起始日期循环到结束日期，逐天输出格式化字符串
        LocalDate currentDate = start;
        while (!currentDate.isAfter(end)) {
            String formattedDate = currentDate.format(formatter);
            // (2)抓取发表内容每日阅读数据
            List<ArticleReadDaily> articleReadDailyList = null;
            try {
                articleReadDailyList = WebChatUtil.getArticleReadDaily(accessToken, formattedDate, formattedDate);
            } catch (Exception ex) {
                logger.error("抓取发表内容每日阅读数据失败:" + ex.getMessage());
            }

            // (3)将获取的数据插入数据库
            if (articleReadDailyList != null && !articleReadDailyList.isEmpty()) {
                logger.info("<------获取的发表内容每日阅读数据条数------> " + articleReadDailyList.size());
                logger.info("<------获取的发表内容每日阅读数据------> " + articleReadDailyList);
                List<Object[]> batchArgs = new ArrayList<>();
                for (ArticleReadDaily article : articleReadDailyList) {
                    batchArgs.add(article.toObject(accountId));
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

            currentDate = currentDate.plusDays(1);
        }
    }

    @Override
    public void captureArticleShareDailyHistory(String startdate, String enddate, String accessToken, Long accountId) {
        if (!validateHistoryParams(startdate, enddate, accessToken, accountId)) {
            throw new RuntimeException("请检查传入参数");
        }

        // 将日期字符串解析为 LocalDate
        LocalDate start = LocalDate.parse(startdate, DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD));
        LocalDate end = LocalDate.parse(enddate, DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD));

        // 验证日期范围
        if (start.isAfter(end)) {
            throw new RuntimeException("开始时间不能玩意结束时间");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        // 从起始日期循环到结束日期，逐天输出格式化字符串
        LocalDate currentDate = start;
        while (!currentDate.isAfter(end)) {
            syncDataService.processArticleShareDailyData(accessToken, accountId, currentDate.format(formatter), currentDate.format(formatter));
            currentDate = currentDate.plusDays(1);
        }
    }


    @Override
    public void captureArticleShareDaily(Long accountId) {
        String accessToken = getAccessToken(accountId);
        captureArticleShareDaily(accessToken, accountId);
    }

    @Override
    public void captureArticleReadDailyHistory(String startdate, String enddate, Long accountId) {
        String accessToken = getAccessToken(accountId);
        captureArticleReadDailyHistory(startdate, enddate, accessToken, accountId);
    }

    @Override
    public void captureArticleSummaryDailyHistory(String startdate, String enddate, Long accountId) {
        String accessToken = getAccessToken(accountId);
        captureArticleSummaryDailyHistory(startdate, enddate, accessToken, accountId);
    }

    @Override
    public void captureArticleShareDailyHistory(String startdate, String enddate, Long accountId) {
        String accessToken = getAccessToken(accountId);
        captureArticleShareDailyHistory(startdate, enddate, accessToken, accountId);
    }



}
