package com.txwx.webchat.service;

public interface IWebChatCaptureService {

    /**
     * @description: 通过调用微信公众号接口获取调用凭据
     */
    String getAccessToken();

    /**
     * @description: 通过调用微信公众号接口查询关注或取消关注人数
     */
    void webChatUserCapture(String accessToken);

    /**
     * @description: 通过调用微信公众号接口查询关注或取消关注人数的历史数据
     */
    void webChatUserCaptureHistory(String accessToken);

    /**
     * @description: 通过调用微信公众号接口查询每日文章阅读、分享、收藏数据
     */
    void webChatArticleUptackCapture(String accessToken);

    /**
     * @description: 通过调用微信公众号接口查询每日文章阅读、分享、收藏的历史数据
     */
    void webChatArticleUptackCaptureHistory(String accessToken);

    /**
     * @description: 通过调用微信公众号接口查询每日文图文阅读概括数据
     */
    void webChatUserReadCapture(String accessToken);

    /**
     * @description: 通过调用微信公众号接口查询每日图文阅读概括数据
     */
    void webChatUserReadCaptureHistory(String accessToken);

    /**
     * @description: 获取并保存已发布消息列表（定时任务）
     */
    void capturePublishedArticles(String accessToken);

    /**
     * @description: 获取并保存发表内容每日阅读数据（定时任务）
     */
    void captureArticleReadDaily(String accessToken);

    /**
     * @description: 获取并保存发表内容概况总数据（定时任务）
     */
    void captureArticleSummaryDaily(String accessToken);

    /**
     * @description: 获取并保存发表内容每日分享数据（定时任务）
     */
    void captureArticleShareDaily(String accessToken);

    /**
     * @description: 获取并保存历史发表内容每日阅读数据（2025-11-01 到 2026-12-04）
     */
    void captureArticleReadDailyHistory(String accessToken);

    /**
     * @description: 获取并保存历史发表内容概况总数据（2025-11-01 到 2026-02-05）
     */
    void captureArticleSummaryDailyHistory(String accessToken);

    /**
     * @description: 获取并保存历史发表内容每日分享数据（2025-11-01 到 2026-02-05）
     */
    void captureArticleShareDailyHistory(String accessToken);

    /**
     * @description: 聚合ODS层数据到DWS层（文章维度统计）
     */
    void aggregateArticleDataToDws();
}
