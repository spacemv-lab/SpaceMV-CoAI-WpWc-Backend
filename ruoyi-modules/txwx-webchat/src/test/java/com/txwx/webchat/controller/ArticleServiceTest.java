package com.txwx.webchat.controller;

import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.txwx.webchat.config.WebChatConfig;
import com.txwx.webchat.domain.ArticleDetailDaily;
import com.txwx.webchat.domain.ArticleSummaryDaily;
import com.txwx.webchat.domain.entity.DwsBizsummaryChannelDaily;
import com.txwx.webchat.mapper.DwsBizsummaryChannelDailyMapper;
import com.txwx.webchat.mapper.DwsContentDataMapper;
import com.txwx.webchat.service.IDwsBizsummaryChannelDailyService;
import com.txwx.webchat.service.IWebChatCaptureService;
import com.txwx.webchat.service.impl.WebChatCaptureServiceImpl;
import com.txwx.webchat.util.WebChatUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * IArticleService 测试类
 *
 * @author txwx
 */
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    @Autowired
    private DwsContentDataMapper  dwsContentDataMapper;
    @Autowired
    private IDwsBizsummaryChannelDailyService iDwsBizsummaryChannelDailyService;
    @Autowired
    private ClickhouseService clickhouseService;
    @Autowired
    private IWebChatCaptureService webChatCaptureService;
    @Autowired
    private DwsBizsummaryChannelDailyMapper dwsBizsummaryChannelDailyMapper;
    private static Logger logger = LoggerFactory.getLogger(WebChatCaptureServiceImpl.class);
    @Autowired
    private WebChatConfig webChatConfig;


    @Test
    void testAggregateArticleDetailsDataToDws() {
        // 测试数据
        dwsContentDataMapper.truncateDwsContentData();
        dwsContentDataMapper.aggregateArticleDetailsDataToDws();
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Test
    void testAggregateArticleDataToDws() throws Exception {
        String accessToken = webChatCaptureService.getAccessToken();
        List<ArticleSummaryDaily> articleSummaryDailyList = WebChatUtil.getArticleSummaryDaily(accessToken, "2026-03-16", "2026-03-16");
        if (articleSummaryDailyList != null && articleSummaryDailyList.size() > 0) {
            List<DwsBizsummaryChannelDaily> batchArgs = new ArrayList<>();
            for (ArticleSummaryDaily article : articleSummaryDailyList) {
                batchArgs.addAll(article.toDwsContentData());
            }
            dwsBizsummaryChannelDailyMapper.insertBatch(batchArgs);
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Test
    void testArticleDetailDaily() {
        String accessToken = webChatCaptureService.getAccessToken();
        String insertSql = webChatConfig.getInsertarticledetaildailysql();
        if (insertSql == null || insertSql.isEmpty()) {
            logger.error("ClickHouse 插入 SQL 未配置，任务终止");
            return;
        }
        List<Object[]> allBatchArgs = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            LocalDate yesterday = LocalDate.now().minusDays(i);
            String yesterdayISO = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE);
            // (1)定义抓取日期
            List<ArticleDetailDaily> articleDetailDailyList = null;
            try {
                articleDetailDailyList = WebChatUtil.getArticleDetailDaily(accessToken, yesterdayISO, yesterdayISO);
            } catch (Exception ex) {
                logger.error("抓取发表内容发表详细数据失败:" + ex.getMessage());
            }
            if (articleDetailDailyList != null && articleDetailDailyList.size() > 0) {
                logger.info("<------获取的发表内容发表详细数据条数------> " + articleDetailDailyList.size());
                for (ArticleDetailDaily articleDetailDaily : articleDetailDailyList) {
                    allBatchArgs.addAll(articleDetailDaily.toFlattenObjectList());
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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
        dwsContentDataMapper.truncateDwsContentData();
        dwsContentDataMapper.aggregateArticleDetailsDataToDws();
        logger.info("<##############################发表内容发表详细数据抓取结束##############################>");
    }
}
