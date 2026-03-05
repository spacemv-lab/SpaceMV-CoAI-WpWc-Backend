package com.txwx.webchat.schedule;

import com.txwx.webchat.service.IWebChatCaptureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@EnableScheduling
public class WebChatDataCaptureTasks {
    private static Logger logger = LoggerFactory.getLogger(WebChatDataCaptureTasks.class);

    @Autowired
    private IWebChatCaptureService webChatCaptureService;

    /**
     * @description: 每天8:30获取并保存关注或取消关注人数
     */
    @Scheduled(cron = "0 30 8 * * ?")
    public void getYesterdayUsers() {
        logger.info("<===================微信公众号数据每日抓取开始=======" + timestamp() + "==============>");
        String accessToken = webChatCaptureService.getAccessToken();
        logger.info("\n");
        // 获取 用户增减数据 【ods_users】
        // 聚合            【dws_users】
        // todo 20260226 数据逻辑仍有一些问题
        webChatCaptureService.webChatUserCapture(accessToken);

        logger.info("\n");
        // 获取 图文群发 每日数据 【article_perday】
        // todo 20260226 微信后续会停用
        webChatCaptureService.webChatArticleUptackCapture(accessToken);

        logger.info("\n");
        //  获取 图文阅读 概括数据 【user_read】
        // todo 20260226 微信后续会停用
        webChatCaptureService.webChatUserReadCapture(accessToken);

        logger.info("\n");
        // 获取 每日阅读数据 【ods_article_read_daily】
        webChatCaptureService.captureArticleReadDaily(accessToken);

        logger.info("\n");
        // 获取 每日分享数据 【ods_article_share_daily】
        webChatCaptureService.captureArticleShareDaily(accessToken);

        logger.info("\n");
        // 获取 概况总数据 【ods_article_summary_daily】
        webChatCaptureService.captureArticleSummaryDaily(accessToken);

        logger.info("\n");
        // 聚合ODS层数据到DWS层（文章维度统计） 【dws_article_read】依据 read_daily 和 share_daily 和 ods_article
        webChatCaptureService.aggregateArticleDataToDws();

        logger.info("\n");
        // 获取 详细文章数据 【ods_article_detail_daily】
        webChatCaptureService.captureArticleTotalDetailDaily(accessToken);

        logger.info("\n");
        logger.info("<===================微信公众号数据每日抓取结束=======" + timestamp() + "==============>");
    }

    /**
     * @description: 每天凌晨3点获取并保存已发布消息列表
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void capturePublishedArticles() {
        logger.info("<===================微信公众号已发布消息列表抓取开始=======" + timestamp() + "==============>");
        String accessToken = webChatCaptureService.getAccessToken();
        logger.info("\n");
        // 获取已发布的消息列表 【ods_article】
        webChatCaptureService.capturePublishedArticles(accessToken);
        logger.info("\n");
        logger.info("<===================微信公众号已发布消息列表抓取结束=======" + timestamp() + "==============>");
    }

    private String timestamp(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = now.format(formatter);

        return timestamp;
    }
}
