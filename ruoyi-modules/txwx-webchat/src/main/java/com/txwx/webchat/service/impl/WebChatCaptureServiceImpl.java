package com.txwx.webchat.service.impl;

import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.txwx.webchat.config.WebChatConfig;
import com.txwx.webchat.domain.WebChatArticleUptackPerday;
import com.txwx.webchat.domain.WebChatUser;
import com.txwx.webchat.domain.WebChatUserRead;
import com.txwx.webchat.service.IWebChatCaptureService;
import com.txwx.webchat.util.WebChatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class WebChatCaptureServiceImpl implements IWebChatCaptureService {

    private static Logger logger = LoggerFactory.getLogger(WebChatCaptureServiceImpl.class);

    @Autowired
    private WebChatConfig webChatConfig;

    @Autowired
    private ClickhouseService clickhouseService;

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
        LocalDate endDate = LocalDate.of(2026, 1, 5);

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
}
