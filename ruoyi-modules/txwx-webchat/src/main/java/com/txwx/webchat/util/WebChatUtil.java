package com.txwx.webchat.util;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.common.http.service.HttpUtil;
import com.txwx.webchat.domain.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebChatUtil {

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
}
