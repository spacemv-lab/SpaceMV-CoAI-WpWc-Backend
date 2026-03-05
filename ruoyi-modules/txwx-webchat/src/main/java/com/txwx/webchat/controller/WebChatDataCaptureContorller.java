package com.txwx.webchat.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.webchat.service.IWebChatCaptureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dataCapture")
@Tag(name = "数据采集")
public class WebChatDataCaptureContorller extends BaseController {

    @Autowired
    private IWebChatCaptureService webChatCaptureService;

    @GetMapping("/token")
    public AjaxResult token(){
        String accessToken = webChatCaptureService.getAccessToken();
        return success(accessToken);
    }

    @GetMapping("/users")
    public AjaxResult users(){
        String accessToken = webChatCaptureService.getAccessToken();
        // 查询关注或取消关注人数
        webChatCaptureService.webChatUserCapture(accessToken);
        return success();
    }

    @GetMapping("/usersHistory")
    public AjaxResult usersHistory(){
        String accessToken = webChatCaptureService.getAccessToken();
        // 查询关注或取消关注人数的历史数据
        webChatCaptureService.webChatUserCaptureHistory(accessToken);
        return success();
    }

    @GetMapping("/articlePerday")
    public AjaxResult articlePerday(){
        String accessToken = webChatCaptureService.getAccessToken();
        // 查询每日文章阅读、分享、收藏数据
        webChatCaptureService.webChatArticleUptackCapture(accessToken);
        return success();
    }

    @GetMapping("/articlePerdayHistory")
    public AjaxResult articlePerdayHistory(){
        String accessToken = webChatCaptureService.getAccessToken();
        // 查询每日文章阅读、分享、收藏的历史数据
        webChatCaptureService.webChatArticleUptackCaptureHistory(accessToken);
        return success();
    }

    @GetMapping("/userreadPerday")
    public AjaxResult userreadPerday(){
        String accessToken = webChatCaptureService.getAccessToken();
        // 查询每日文图文阅读概括数据
        webChatCaptureService.webChatUserReadCapture(accessToken);
        return success();
    }

    @GetMapping("/userreadPerdayHistory")
    public AjaxResult userreadPerdayHistory(){
        String accessToken = webChatCaptureService.getAccessToken();
        // 查询每日图文阅读概括数据
        webChatCaptureService.webChatUserReadCaptureHistory(accessToken);
        return success();
    }

    @GetMapping("/capturePublishedArticles")
    public AjaxResult capturePublishedArticles(){
        String accessToken = webChatCaptureService.getAccessToken();
        // 已发布消息列表
        webChatCaptureService.capturePublishedArticles(accessToken);
        return success();
    }

    @GetMapping("/captureArticleReadDaily")
    public AjaxResult captureArticleReadDaily(){
        String accessToken = webChatCaptureService.getAccessToken();
        //
        webChatCaptureService.captureArticleReadDaily(accessToken);
        return success();
    }

    @GetMapping("/captureArticleSummaryDaily")
    public AjaxResult captureArticleSummaryDaily(){
        String accessToken = webChatCaptureService.getAccessToken();

        webChatCaptureService.captureArticleSummaryDaily(accessToken);
        return success();
    }

    @GetMapping("/captureArticleReadDailyHistory")
    public AjaxResult captureArticleReadDailyHistory(){
        String accessToken = webChatCaptureService.getAccessToken();

        webChatCaptureService.captureArticleReadDailyHistory(accessToken);
        return success();
    }

    @GetMapping("/captureArticleSummaryDailyHistory")
    public AjaxResult captureArticleSummaryDailyHistory(){
        String accessToken = webChatCaptureService.getAccessToken();

        webChatCaptureService.captureArticleSummaryDailyHistory(accessToken);
        return success();
    }

    @GetMapping("/captureArticleShareDaily")
    public AjaxResult captureArticleShareDaily(){
        String accessToken = webChatCaptureService.getAccessToken();

        webChatCaptureService.captureArticleShareDaily(accessToken);
        return success();
    }

    @GetMapping("/captureArticleShareDailyHistory")
    public AjaxResult captureArticleShareDailyHistory(){
        String accessToken = webChatCaptureService.getAccessToken();

        webChatCaptureService.captureArticleShareDailyHistory(accessToken);
        return success();
    }

    @GetMapping("/aggregateArticleDataToDws")
    public AjaxResult aggregateArticleDataToDws(){
        webChatCaptureService.aggregateArticleDataToDws();
        return success();
    }
}
