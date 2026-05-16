/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.dashboard.util;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.utils.sign.RsaUtils;
import com.ruoyi.common.http.service.HttpUtil;
import com.txwx.social.dashboard.domain.*;
import com.txwx.social.dashboard.domain.dto.GetPublishedListRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebChatUtil {

    public static String extractMidFromUrl(String url) {
        try {
            Pattern pattern = Pattern.compile("mid=([^&]+)");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    /**
     * @description: 获取微信接口调用接入码
     */
    public static String getAccessToken(String appId, String secret) throws Exception{
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("grant_type", "client_credential");
        queryParams.put("appid", appId);
        queryParams.put("secret", RsaUtils.decryptByPrivateKey(secret));

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
    public static List<WebChatUser> getUserWithDate(String token, String beginDate, String endDate) throws Exception {
        Map<String, String> specialParams = new HashMap<>();
        specialParams.put("access_token", token);

        // 将字符串日期转换为LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDate start = LocalDate.parse(beginDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        // 计算总天数
        long totalDays = ChronoUnit.DAYS.between(start, end) + 1;

        List<WebChatUser> allUsers = new ArrayList<>();

        // 如果时间跨度小于等于7天，直接调用
        if (totalDays <= 7) {
            return getUserWithDateRange(token, start, end);
        }

        // 分片处理
        LocalDate currentStart = start;

        while (!currentStart.isAfter(end)) {
            // 计算当前分片的结束日期（不超过7天）
            LocalDate currentEnd = currentStart.plusDays(6);
            if (currentEnd.isAfter(end)) {
                currentEnd = end;
            }

            // 调用接口获取当前时间段的数据
            List<WebChatUser> segmentResult = getUserWithDateRange(token, currentStart, currentEnd);
            if (segmentResult != null) {
                allUsers.addAll(segmentResult);
            }

            // 设置下一个分片的开始日期
            currentStart = currentEnd.plusDays(1);
        }

        return allUsers;
    }

    /**
     * 获取指定时间范围内的用户数据（时间跨度不超过7天）
     */
    private static List<WebChatUser> getUserWithDateRange(String token, LocalDate beginDate, LocalDate endDate) throws Exception {
        Map<String, String> specialParams = new HashMap<>();
        specialParams.put("access_token", token);
        String url = HttpUtil.buildUrlWithParams("https://api.weixin.qq.com/datacube/getusersummary", specialParams);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        WebChatDate date = new WebChatDate();
        date.setBegin_date(beginDate.format(formatter));
        date.setEnd_date(endDate.format(formatter));

        try {
            String postResponse = HttpUtil.postJson(url, null, date);
            JSONObject jsonObject = JSONObject.parseObject(postResponse);
            return jsonObject.getList("list", WebChatUser.class);
        } catch (Exception e) {
            throw new Exception("获取" + beginDate + "到" + endDate + "的用户数据失败: " + e.getMessage(), e);
        }
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
    @Deprecated
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
     * 用于替换 getArticleDetailDaily
     * 需要按每天切片
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
     * 用于替换 getArticleDetailDaily
     * 但其实也没必要，就拿getbizsummary就都包括了
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

    @Deprecated
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
