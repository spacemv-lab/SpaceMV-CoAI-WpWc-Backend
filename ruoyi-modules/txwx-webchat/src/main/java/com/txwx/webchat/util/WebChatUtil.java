package com.txwx.webchat.util;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.common.http.service.HttpUtil;
import com.txwx.webchat.domain.*;
import com.txwx.webchat.dto.GetPublishedListRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebChatUtil {

    /**
     * @description: 获取微信接口调用接入码
     */
    public static String getAccessToken(String appId, String secret) throws Exception{
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("grant_type", "client_credential");
        queryParams.put("appid", appId);
        queryParams.put("secret", secret);

        // 测试不带请求头的带参数GET请求
        String token = null;
        try {
            String paramResponse = HttpUtil.getWithParams("https://api.weixin.qq.com/cgi-bin/token", queryParams);
            WebChatAccessToken webChatAccessToken = JSONObject.parseObject(paramResponse, WebChatAccessToken.class);
            token = webChatAccessToken.getAccess_token();
        } catch (Exception e) {
            throw e;
        }

        return token;
    }

    /**
     * @description: 获取前一天的关注/取消关注用户数
     */
    public static List<WebChatUser> getUserYesterday(String token, String beginDate, String endDate) throws Exception{
        Map<String, String> specialParams = new HashMap<>();
        specialParams.put("access_token", token);
        String url = HttpUtil.buildUrlWithParams("https://api.weixin.qq.com/datacube/getusersummary", specialParams);

        WebChatDate date = new WebChatDate();
        date.setBegin_date(beginDate);
        date.setEnd_date(endDate);

        List<WebChatUser> result = null;
        try {
            String postResponse = HttpUtil.postJson(url, null, date);
            JSONObject jsonObject = JSONObject.parseObject(postResponse);
            result = jsonObject.getList("list", WebChatUser.class);
        } catch (Exception e) {
            throw e;
        }

        return result;
    }

    /**
     * @description: 获取每天的文章阅读情况
     */
    public static List<WebChatArticleUptackPerday> getArticleUptackPerday(String token, String beginDate, String endDate) throws Exception{
        Map<String, String> specialParams = new HashMap<>();
        specialParams.put("access_token", token);
        String url = HttpUtil.buildUrlWithParams("https://api.weixin.qq.com/datacube/getarticlesummary", specialParams);

        WebChatDate date = new WebChatDate();
        date.setBegin_date(beginDate);
        date.setEnd_date(endDate);

        List<WebChatArticleUptackPerday> result = null;
        try {
            String postResponse = HttpUtil.postJson(url, null, date);
            JSONObject jsonObject = JSONObject.parseObject(postResponse);
            result = jsonObject.getList("list", WebChatArticleUptackPerday.class);
        } catch (Exception e) {
            throw e;
        }

        return result;
    }

    /**
     * @description: 获取每天的用户阅读数据
     */
    public static List<WebChatUserRead> getUserReadPerday(String token, String beginDate, String endDate) throws Exception{
        Map<String, String> specialParams = new HashMap<>();
        specialParams.put("access_token", token);
        String url = HttpUtil.buildUrlWithParams("https://api.weixin.qq.com/datacube/getuserread", specialParams);

        WebChatDate date = new WebChatDate();
        date.setBegin_date(beginDate);
        date.setEnd_date(endDate);

        List<WebChatUserRead> result = null;
        try {
            String postResponse = HttpUtil.postJson(url, null, date);
            JSONObject jsonObject = JSONObject.parseObject(postResponse);
            result = jsonObject.getList("list", WebChatUserRead.class);
        } catch (Exception e) {
            throw e;
        }

        return result;
    }

    /**
     * @description: 调用微信公众号官方接口获取已发布消息列表
     */
    public static GetPublishedListResponse getPublishedList(String accessToken, Integer offset, Integer count, Integer noContent) throws Exception {
        String urlStr = "https://api.weixin.qq.com/cgi-bin/freepublish/batchget?access_token=" + accessToken;

        GetPublishedListRequest request = new GetPublishedListRequest();
        request.setOffset(offset);
        request.setCount(count);
        request.setNo_content(noContent);

        String response = HttpUtil.postJson(urlStr, null, request);
        GetPublishedListResponse result = JSONObject.parseObject(response, GetPublishedListResponse.class);

        if (result.getErrcode() != null && result.getErrcode() != 0) {
            throw new RuntimeException("获取已发布消息列表失败: " + result.getErrmsg());
        }

        return result;
    }

    /**
     * @description: 获取发表内容每日阅读数据
     */
    public static List<ArticleReadDaily> getArticleReadDaily(String token, String beginDate, String endDate) throws Exception {
        Map<String, String> specialParams = new HashMap<>();
        specialParams.put("access_token", token);
        String url = HttpUtil.buildUrlWithParams("https://api.weixin.qq.com/datacube/getarticleread", specialParams);

        WebChatDate date = new WebChatDate();
        date.setBegin_date(beginDate);
        date.setEnd_date(endDate);

        List<ArticleReadDaily> result = null;
        try {
            String postResponse = HttpUtil.postJson(url, null, date);
            JSONObject jsonObject = JSONObject.parseObject(postResponse);
            result = jsonObject.getList("list", ArticleReadDaily.class);
        } catch (Exception e) {
            throw e;
        }

        return result;
    }

    /**
     * @description: 获取发表内容概况总数据
     */
    public static List<ArticleSummaryDaily> getArticleSummaryDaily(String token, String beginDate, String endDate) throws Exception {
        Map<String, String> specialParams = new HashMap<>();
        specialParams.put("access_token", token);
        String url = HttpUtil.buildUrlWithParams("https://api.weixin.qq.com/datacube/getbizsummary", specialParams);

        WebChatDate date = new WebChatDate();
        date.setBegin_date(beginDate);
        date.setEnd_date(endDate);

        List<ArticleSummaryDaily> result = null;
        try {
            String postResponse = HttpUtil.postJson(url, null, date);
            JSONObject jsonObject = JSONObject.parseObject(postResponse);
            result = jsonObject.getList("list", ArticleSummaryDaily.class);
        } catch (Exception e) {
            throw e;
        }

        return result;
    }

    /**
     * @description: 获取发表内容每日分享数据
     */
    public static List<ArticleShareDaily> getArticleShareDaily(String token, String beginDate, String endDate) throws Exception {
        Map<String, String> specialParams = new HashMap<>();
        specialParams.put("access_token", token);
        String url = HttpUtil.buildUrlWithParams("https://api.weixin.qq.com/datacube/getarticleshare", specialParams);

        WebChatDate date = new WebChatDate();
        date.setBegin_date(beginDate);
        date.setEnd_date(endDate);

        List<ArticleShareDaily> result = null;
        try {
            String postResponse = HttpUtil.postJson(url, null, date);
            JSONObject jsonObject = JSONObject.parseObject(postResponse);
            result = jsonObject.getList("list", ArticleShareDaily.class);
        } catch (Exception e) {
            throw e;
        }

        return result;
    }

    public static List<ArticleDetailDaily> getArticleDetailDaily(String token, String beginDate, String endDate) throws Exception{
        Map<String, String> specialParams = new HashMap<>();
        specialParams.put("access_token", token);
        String url = HttpUtil.buildUrlWithParams("https://api.weixin.qq.com/datacube/getarticletotaldetail", specialParams);

        WebChatDate date = new WebChatDate();
        date.setBegin_date(beginDate);
        date.setEnd_date(endDate);

        List<ArticleDetailDaily> result = null;
        try {
            String postResponse = HttpUtil.postJson(url, null, date);
            JSONObject jsonObject = JSONObject.parseObject(postResponse);
            result = jsonObject.getList("list", ArticleDetailDaily.class);
        } catch (Exception e) {
            throw e;
        }

        return result;
    }
}
