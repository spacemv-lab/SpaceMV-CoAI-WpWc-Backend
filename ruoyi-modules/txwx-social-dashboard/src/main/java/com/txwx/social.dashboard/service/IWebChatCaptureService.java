package com.txwx.social.dashboard.service;

public interface IWebChatCaptureService {

    /**
     * @description: 通过调用微信公众号接口获取调用凭据
     */
    String getAccessToken(Long accountId);

    Boolean dataSync(Long accountId, String startdate, String enddate);

    void syncPlatformData(Long accountId, String startdate, String enddate);

    String getAccessToken(String appId, String secret);

    /**
     * @description: 通过调用微信公众号接口查询关注或取消关注人数
     */
    void webChatUserCapture(Long accountId);

    void webChatUserCapture(String accessToken, Long accountId);

    /**
     * @description: 通过调用微信公众号接口查询关注或取消关注人数的历史数据
     */
    void webChatUserCaptureHistory(String startdate, String endDate, Long accountId);

    /**
     * @description: 通过调用微信公众号接口查询关注或取消关注人数的历史数据
     */
    void webChatUserCaptureHistory(String startdate, String endDate, String accessToken, Long accountId);

    /**
     * @description: 通过调用微信公众号接口查询每日文章阅读、分享、收藏数据
     */
    void webChatArticleUptackCapture(Long accountId);

    /**
     * @description: 通过调用微信公众号接口查询每日文章阅读、分享、收藏的历史数据
     */
    void webChatArticleUptackCaptureHistory(String startdate, String endDate, Long accountId);

    /**
     * @description: 通过调用微信公众号接口查询每日文图文阅读概括数据
     */
    void webChatUserReadCapture(Long accountId);

    /**
     * @description: 通过调用微信公众号接口查询每日图文阅读概括数据
     */
    void webChatUserReadCaptureHistory(String startdate, String enddate, Long accountId);

    /**
     * @description: 获取并保存已发布消息列表（定时任务）
     */
    void capturePublishedArticles(Long accountId);

    /**
     * @description: 获取并保存发表内容每日阅读数据（定时任务）
     */
    void captureArticleReadDaily(Long accountId);

    /**
     * @description: 获取并保存发表内容概况总数据（定时任务）
     */
    void captureArticleSummaryDaily(Long accountId);

    /**
     * @description: 获取并保存发表内容每日分享数据（定时任务）
     */
    void captureArticleShareDaily(Long accountId);

    /**
     * @description: 获取并保存历史发表内容每日阅读数据（2025-11-01 到 2026-12-04）
     */
    void captureArticleReadDailyHistory(String startdate, String endDate, Long accountId);

    /**
     * @description: 获取并保存历史发表内容概况总数据（2025-11-01 到 2026-02-05）
     */
    void captureArticleSummaryDailyHistory(String startdate, String endDate, Long accountId);

    /**
     * @description: 获取并保存历史发表内容每日分享数据（2025-11-01 到 2026-02-05）
     */
    void captureArticleShareDailyHistory(String startdate, String endDate, Long accountId);

    /**
     * @description: 聚合ODS层数据到DWS层（文章维度统计）
     */

    void captureArticleTotalDetailDaily(Long accountId);

    void captureArticleTotalDetailDaily(String accessToken, Long accountId);

    void webChatArticleUptackCapture(String accessToken, Long accountId);

    /**
     * @description: 通过调用微信公众号接口查询每日文章阅读、分享、收藏的历史数据
     */
    void webChatArticleUptackCaptureHistory(String startdate, String endDate, String accessToken, Long accountId);

    /**
     * @description: 通过调用微信公众号接口查询每日文图文阅读概括数据
     */
    void webChatUserReadCapture(String accessToken, Long accountId);

    /**
     * @description: 通过调用微信公众号接口查询每日图文阅读概括数据
     */
    void webChatUserReadCaptureHistory(String startdate, String endDate, String accessToken, Long accountId);

    /**
     * @description: 获取并保存已发布消息列表（定时任务）
     */
    void capturePublishedArticles(String accessToken, Long accountId);

    /**
     * @description: 获取并保存发表内容每日阅读数据（定时任务）
     */
    void captureArticleReadDaily(String accessToken, Long accountId);

    /**
     * @description: 获取并保存发表内容概况总数据（定时任务）
     */
    void captureArticleSummaryDaily(String accessToken, Long accountId);

    /**
     * @description: 获取并保存发表内容每日分享数据（定时任务）
     */
    void captureArticleShareDaily(String accessToken, Long accountId);

    /**
     * @description: 获取并保存历史发表内容每日阅读数据（2025-11-01 到 2026-12-04）
     */
    void captureArticleReadDailyHistory(String startdate, String endDate, String accessToken, Long accountId);

    /**
     * @description: 获取并保存历史发表内容概况总数据（2025-11-01 到 2026-02-05）
     */
    void captureArticleSummaryDailyHistory(String startdate, String endDate, String accessToken, Long accountId);

    /**
     * @description: 获取并保存历史发表内容每日分享数据（2025-11-01 到 2026-02-05）
     */
    void captureArticleShareDailyHistory(String startdate, String endDate, String accessToken, Long accountId);

}
