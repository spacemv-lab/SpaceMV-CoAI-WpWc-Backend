package com.txwx.social.dashboard.controller;

import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.txwx.social.dashboard.domain.vo.MediaProductVo;
import com.txwx.social.dashboard.domain.vo.UserPlatformVo;
import com.txwx.social.dashboard.mapper.DwsBizsummaryChannelDailyMapper;
import com.txwx.social.dashboard.mapper.DwsContentDataMapper;
import com.txwx.social.dashboard.service.IDwsBizsummaryChannelDailyService;
import com.txwx.social.dashboard.service.IMediaProductsService;
import com.txwx.social.dashboard.service.IWebChatCaptureService;
import com.txwx.social.dashboard.service.impl.SyncDataServiceImpl;
import com.txwx.social.dashboard.service.impl.WebChatCaptureServiceImpl;
import com.txwx.social.dashboard.util.WebChatUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


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


    @Autowired
    private IMediaProductsService iMediaProductsService;
    @Autowired
    private SyncDataServiceImpl syncDataServiceImpl;
    @Test
    void testAggregateArticleDataToDws2() throws Exception {

        List<MediaProductVo> mediaProductVos = iMediaProductsService.selectList();
        if (mediaProductVos == null || mediaProductVos.size() == 0) {
            logger.error("未建立自媒体产品及平台!");
            throw new RuntimeException("未建立自媒体产品及平台!");
        }
        for (MediaProductVo mediaProductVo : mediaProductVos) {
            // 获取当前系统绑定的平台及产品id
            Long productId = mediaProductVo.getId();
            for (UserPlatformVo platformVo : mediaProductVo.getUserPlatformList()) {
                Long platformId = platformVo.getMediaPlatform().getId();

                // String accessToken = webChatCaptureService.getAccessToken();
                String accessToken = WebChatUtil.getAccessToken(platformVo.getMediaPlatform().getAppId(), platformVo.getMediaPlatform().getSecret());
                if (accessToken == null) {
                    logger.error("获取微信公众号access_token失败!");
                    continue;
                }
                LocalDate today = LocalDate.now().minusDays(1);

                test111(accessToken, platformId, productId, today);
            }
        }
    }

    private void test111(String accessToken, Long platformId, Long productId, LocalDate today) {
        syncDataServiceImpl.syncUserOneDay(accessToken, platformId, productId, today);
        syncDataServiceImpl.syncArticleSummaryDailyOneDay(accessToken, platformId, productId, today);
        String formatted = today.format(DateTimeFormatter.ISO_LOCAL_DATE);
        syncDataServiceImpl.syncArticleTotalDetailHistoryRange(accessToken, productId, platformId, formatted, formatted);
        webChatCaptureService.capturePublishedArticles(accessToken, platformId, productId);
    }
}
