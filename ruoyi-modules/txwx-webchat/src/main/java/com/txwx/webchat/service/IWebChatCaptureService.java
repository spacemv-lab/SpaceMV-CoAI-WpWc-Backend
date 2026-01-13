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
}
