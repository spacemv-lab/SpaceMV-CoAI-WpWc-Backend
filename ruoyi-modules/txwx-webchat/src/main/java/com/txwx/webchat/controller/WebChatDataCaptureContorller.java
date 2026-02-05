package com.txwx.webchat.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.webchat.service.IWebChatCaptureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dataCapture")
public class WebChatDataCaptureContorller extends BaseController {

    @Autowired
    private IWebChatCaptureService webChatCaptureService;

    @GetMapping("/users")
    public AjaxResult users(){
        String accessToken = webChatCaptureService.getAccessToken();

        webChatCaptureService.webChatUserCapture(accessToken);
        return success();
    }

    @GetMapping("/usersHistory")
    public AjaxResult usersHistory(){
        String accessToken = webChatCaptureService.getAccessToken();

        webChatCaptureService.webChatUserCaptureHistory(accessToken);
        return success();
    }

    @GetMapping("/articlePerday")
    public AjaxResult articlePerday(){
        String accessToken = webChatCaptureService.getAccessToken();

        webChatCaptureService.webChatArticleUptackCapture(accessToken);
        return success();
    }

    @GetMapping("/articlePerdayHistory")
    public AjaxResult articlePerdayHistory(){
        String accessToken = webChatCaptureService.getAccessToken();

        webChatCaptureService.webChatArticleUptackCaptureHistory(accessToken);
        return success();
    }

    @GetMapping("/userreadPerday")
    public AjaxResult userreadPerday(){
        String accessToken = webChatCaptureService.getAccessToken();

        webChatCaptureService.webChatUserReadCapture(accessToken);
        return success();
    }

    @GetMapping("/userreadPerdayHistory")
    public AjaxResult userreadPerdayHistory(){
        String accessToken = webChatCaptureService.getAccessToken();

        webChatCaptureService.webChatUserReadCaptureHistory(accessToken);
        return success();
    }

    @GetMapping("/capturePublishedArticles")
    public AjaxResult capturePublishedArticles(){
        String accessToken = webChatCaptureService.getAccessToken();

        webChatCaptureService.capturePublishedArticles(accessToken);
        return success();
    }

    @GetMapping("/captureArticleReadDaily")
    public AjaxResult captureArticleReadDaily(){
        String accessToken = webChatCaptureService.getAccessToken();

        webChatCaptureService.captureArticleReadDaily(accessToken);
        return success();
    }

    @GetMapping("/captureArticleSummaryDaily")
    public AjaxResult captureArticleSummaryDaily(){
        String accessToken = webChatCaptureService.getAccessToken();

        webChatCaptureService.captureArticleSummaryDaily(accessToken);
        return success();
    }
}
