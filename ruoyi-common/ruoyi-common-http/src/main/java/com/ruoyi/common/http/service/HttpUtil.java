package com.ruoyi.common.http.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class HttpUtil {
    private static final CloseableHttpClient httpClient;
    private static final ObjectMapper objectMapper;

    static {
        httpClient = HttpClients.createDefault();
        objectMapper = new ObjectMapper();
    }

    /**
     * GET请求
     * @param url 请求地址
     * @param headers 请求头
     * @return 响应体字符串
     */
    public static String get(String url, Map<String, String> headers) throws Exception {
        HttpGet httpGet = new HttpGet(url);

        if (headers != null) {
            headers.forEach(httpGet::setHeader);
        }

        return httpClient.execute(httpGet, response -> {
            int statusCode = response.getCode();
            HttpEntity entity = response.getEntity();
            String responseBody = entity != null ? EntityUtils.toString(entity, StandardCharsets.UTF_8) : "";

            if (statusCode >= HttpStatus.SC_OK && statusCode < HttpStatus.SC_REDIRECTION) {
                return responseBody;
            } else {
                throw new RuntimeException("GET请求失败，状态码: " + statusCode + ", 响应: " + responseBody);
            }
        });
    }

    /**
     * GET请求（无请求头）
     */
    public static String get(String url) throws Exception {
        return get(url, null);
    }

    /**
     * GET请求（带查询参数）
     * @param baseUrl 基础URL（不包含查询参数）
     * @param params 查询参数Map
     * @param headers 请求头
     * @return 响应体字符串
     */
    public static String getWithParams(String baseUrl, Map<String, String> params, Map<String, String> headers) throws Exception {
        String url = buildUrlWithParams(baseUrl, params);
        return get(url, headers);
    }

    /**
     * GET请求（带查询参数，无请求头）
     */
    public static String getWithParams(String baseUrl, Map<String, String> params) throws Exception {
        return getWithParams(baseUrl, params, null);
    }

    /**
     * 构建带查询参数的URL
     * @param baseUrl 基础URL
     * @param params 查询参数Map
     * @return 完整的URL
     */
    public static String buildUrlWithParams(String baseUrl, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return baseUrl;
        }

        try {
            URIBuilder uriBuilder = new URIBuilder(baseUrl);
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null && value != null) {
                    uriBuilder.addParameter(key, value);
                }
            }
            return uriBuilder.build().toString();
        } catch (Exception e) {
            // 如果解析失败，回退到原始拼接方式
            StringJoiner joiner = new StringJoiner("&");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null && value != null) {
                    joiner.add(key + "=" + value);
                }
            }

            String queryString = joiner.toString();
            if (baseUrl.contains("?")) {
                return baseUrl + "&" + queryString;
            } else {
                return baseUrl + "?" + queryString;
            }
        }
    }

    /**
     * POST请求（JSON格式）
     * @param url 请求地址
     * @param headers 请求头
     * @param body 请求体对象
     * @return 响应体字符串
     */
    public static String postJson(String url, Map<String, String> headers, Object body) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);

        HttpPost httpPost = new HttpPost(url);
        StringEntity requestEntity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
        httpPost.setEntity(requestEntity);

        if (headers != null) {
            headers.forEach(httpPost::setHeader);
        }

        return httpClient.execute(httpPost, response -> {
            int statusCode = response.getCode();
            HttpEntity entity = response.getEntity();
            String responseBody = entity != null ? EntityUtils.toString(entity, StandardCharsets.UTF_8) : "";

            if (statusCode >= HttpStatus.SC_OK && statusCode < HttpStatus.SC_REDIRECTION) {
                return responseBody;
            } else {
                throw new RuntimeException("POST请求失败，状态码: " + statusCode + ", 响应: " + responseBody);
            }
        });
    }

    /**
     * POST请求（表单格式）
     * @param url 请求地址
     * @param headers 请求头
     * @param formData 表单数据
     * @return 响应体字符串
     */
    public static String postForm(String url, Map<String, String> headers, Map<String, String> formData) throws Exception {
        HttpPost httpPost = new HttpPost(url);

        if (formData != null && !formData.isEmpty()) {
            List<org.apache.hc.core5.http.NameValuePair> params = new ArrayList<>();
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(params));
        }

        if (headers != null) {
            headers.forEach(httpPost::setHeader);
        }

        return httpClient.execute(httpPost, response -> {
            int statusCode = response.getCode();
            HttpEntity entity = response.getEntity();
            String responseBody = entity != null ? EntityUtils.toString(entity, StandardCharsets.UTF_8) : "";

            if (statusCode >= HttpStatus.SC_OK && statusCode < HttpStatus.SC_REDIRECTION) {
                return responseBody;
            } else {
                throw new RuntimeException("POST表单请求失败，状态码: " + statusCode + ", 响应: " + responseBody);
            }
        });
    }

    /**
     * DELETE请求
     * @param url 请求地址
     * @param headers 请求头
     * @return 响应体字符串
     */
    public static String delete(String url, Map<String, String> headers) throws Exception {
        HttpDelete httpDelete = new HttpDelete(url);

        if (headers != null) {
            headers.forEach(httpDelete::setHeader);
        }

        return httpClient.execute(httpDelete, response -> {
            int statusCode = response.getCode();
            HttpEntity entity = response.getEntity();
            String responseBody = entity != null ? EntityUtils.toString(entity, StandardCharsets.UTF_8) : "";

            if (statusCode >= HttpStatus.SC_OK && statusCode < HttpStatus.SC_REDIRECTION) {
                return responseBody;
            } else {
                throw new RuntimeException("DELETE请求失败，状态码: " + statusCode + ", 响应: " + responseBody);
            }
        });
    }

    /**
     * DELETE请求（无请求头）
     */
    public static String delete(String url) throws Exception {
        return delete(url, null);
    }

    /**
     * 关闭HttpClient连接
     */
    public static void close() throws Exception {
        if (httpClient != null) {
            httpClient.close();
        }
    }


    /**
     * 测试数据类
     */
    static class TestData {
        private String begin_date;
        private String end_date;

        public TestData(String begin_date, String end_date) {
            this.begin_date = begin_date;
            this.end_date = end_date;
        }

        public String getBegin_date() {
            return begin_date;
        }

        public void setBegin_date(String begin_date) {
            this.begin_date = begin_date;
        }

        public String getEnd_date() {
            return end_date;
        }

        public void setEnd_date(String end_date) {
            this.end_date = end_date;
        }
    }

}
