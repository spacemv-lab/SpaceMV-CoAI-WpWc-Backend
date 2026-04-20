package com.txwx.social.dashboard.controller;

import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.social.api.client.AccountApiClient;
import com.txwx.social.dashboard.domain.request.HistoryTriggerRequest;
import com.txwx.social.dashboard.schedule.WebChatDataCaptureTasks;
import com.txwx.social.dashboard.service.IWebChatCaptureService;
import com.txwx.social.dashboard.service.ArticleDataAggregator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dataCapture")
@Tag(name = "数据采集")
public class WebChatDataCaptureContorller extends BaseController {

    @Autowired
    private IWebChatCaptureService webChatCaptureService;

    @Autowired
    private AccountApiClient accountApiClient;

    @Autowired
    private ArticleDataAggregator articleDataAggregator;

    @Autowired
    private WebChatDataCaptureTasks webChatDataCaptureTasks;


    @GetMapping("/dataSync/{accountId}")
    @Operation(summary = "数据同步")
    public AjaxResult dataSync(@PathVariable("accountId") Long accountId,
                               @RequestParam("startdate") String startdate,
                               @RequestParam("enddate") String enddate) {
        if (accountId == null
                || StringUtils.isEmpty(startdate)
                || StringUtils.isEmpty(enddate)) {
            throw new RuntimeException("请检查传入参数");
        }
        Boolean res = webChatCaptureService.dataSync(accountId, startdate, enddate);
        if (res) {
            return success("首次同步时间较长,预计30分钟以上,请耐心等待.");
        }else {
            return error("数据同步失败!");
        }
    }

    @GetMapping("/dataSchedule/test")
    @Operation(summary = "同步任务测试")
    public AjaxResult dataSchedule() {
        webChatDataCaptureTasks.getYesterdayDatas();
        return AjaxResult.success();
    }

    @PostMapping("/users")
    public AjaxResult users(@RequestBody List<Long> accountIds){
        // 查询关注或取消关注人数
        //TODO 当前仅支持单账号
        webChatCaptureService.webChatUserCapture(accountIds.get(0));
        return success();
    }

    @PostMapping("/usersHistory")
    public AjaxResult usersHistory(@RequestBody HistoryTriggerRequest request){
        // 查询关注或取消关注人数的历史数据
        webChatCaptureService.webChatUserCaptureHistory(request.getStartdate(),
                request.getEnddate(), request.getAccountIds().get(0));
        return success();
    }

    @PostMapping("/articlePerday")
    public AjaxResult articlePerday(@RequestBody List<Long> accountIds){
        // 查询每日文章阅读、分享、收藏数据
        webChatCaptureService.webChatArticleUptackCapture(accountIds.get(0));
        return success();
    }

    @PostMapping("/articlePerdayHistory")
    public AjaxResult articlePerdayHistory(@RequestBody HistoryTriggerRequest request){
        // 查询每日文章阅读、分享、收藏的历史数据
        webChatCaptureService.webChatArticleUptackCaptureHistory(request.getStartdate(),
                request.getEnddate(), request.getAccountIds().get(0));
        return success();
    }

    @PostMapping("/userreadPerday")
    public AjaxResult userreadPerday(@RequestBody List<Long> accountIds){
        // 查询每日文图文阅读概括数据
        webChatCaptureService.webChatUserReadCapture(accountIds.get(0));
        return success();
    }

    @PostMapping("/userreadPerdayHistory")
    public AjaxResult userreadPerdayHistory(@RequestBody HistoryTriggerRequest request){
        // 查询每日图文阅读概括数据
        webChatCaptureService.webChatUserReadCaptureHistory(request.getStartdate(),
                request.getEnddate(), request.getAccountIds().get(0));
        return success();
    }

    @PostMapping("/capturePublishedArticles")
    public AjaxResult capturePublishedArticles(@RequestBody List<Long> accountIds){
        // 已发布消息列表
        webChatCaptureService.capturePublishedArticles(accountIds.get(0));
        return success();
    }

    @PostMapping("/captureArticleReadDaily")
    public AjaxResult captureArticleReadDaily(@RequestBody List<Long> accountIds){
        webChatCaptureService.captureArticleReadDaily(accountIds.get(0));
        return success();
    }

    @PostMapping("/captureArticleSummaryDaily")
    public AjaxResult captureArticleSummaryDaily(@RequestBody List<Long> accountIds){

        webChatCaptureService.captureArticleSummaryDaily(accountIds.get(0));
        return success();
    }

    @PostMapping("/captureArticleReadDailyHistory")
    public AjaxResult captureArticleReadDailyHistory(@RequestBody HistoryTriggerRequest request){
        webChatCaptureService.captureArticleReadDailyHistory(request.getStartdate(), request.getEnddate(),
                request.getAccountIds().get(0));
        return success();
    }

    @PostMapping("/captureArticleSummaryDailyHistory")
    public AjaxResult captureArticleSummaryDailyHistory(@RequestBody HistoryTriggerRequest request){

        webChatCaptureService.captureArticleSummaryDailyHistory(request.getStartdate(), request.getEnddate(),
                request.getAccountIds().get(0));
        return success();
    }

    @PostMapping("/captureArticleShareDaily")
    public AjaxResult captureArticleShareDaily(@RequestBody List<Long> accountIds){

        webChatCaptureService.captureArticleShareDaily(accountIds.get(0));
        return success();
    }

    @PostMapping("/captureArticleShareDailyHistory")
    public AjaxResult captureArticleShareDailyHistory(@RequestBody HistoryTriggerRequest request){
        webChatCaptureService.captureArticleShareDailyHistory(request.getStartdate(), request.getEnddate(),
                request.getAccountIds().get(0));
        return success();
    }

    @PostMapping("/aggregateArticleDataToDws")
    public AjaxResult aggregateArticleDataToDws(@RequestBody List<Long> accountIds){
        articleDataAggregator.aggregateDataToDws(accountIds.get(0));
        return success();
    }

}
