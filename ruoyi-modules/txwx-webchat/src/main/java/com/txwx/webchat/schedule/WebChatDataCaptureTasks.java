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

    @Scheduled(cron = "0 30 8 * * ?")
    public void getYesterdayUsers() {
        logger.info("<===================微信公众号数据每日抓取开始=======" + timestamp() + "==============>");
        String accessToken = webChatCaptureService.getAccessToken();
        logger.info("\n");
        webChatCaptureService.webChatUserCapture(accessToken);

        logger.info("\n");
        webChatCaptureService.webChatArticleUptackCapture(accessToken);

        logger.info("\n");
        webChatCaptureService.webChatUserReadCapture(accessToken);

        logger.info("\n");
        webChatCaptureService.captureArticleReadDaily(accessToken);

        logger.info("\n");
        webChatCaptureService.captureArticleSummaryDaily(accessToken);

        logger.info("\n");
        webChatCaptureService.captureArticleShareDaily(accessToken);

        logger.info("\n");
        webChatCaptureService.aggregateArticleDataToDws();

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
