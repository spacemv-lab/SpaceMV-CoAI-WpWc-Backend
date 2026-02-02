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
            String responseBody = entity != null ? EntityUtils.toString(entity) : "";

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
            String responseBody = entity != null ? EntityUtils.toString(entity) : "";

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
            String responseBody = entity != null ? EntityUtils.toString(entity) : "";

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
            String responseBody = entity != null ? EntityUtils.toString(entity) : "";

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
     * 测试方法
     */
    /*
    public static void main(String[] args) {
        try {
            // 测试普通GET请求
            System.out.println("=== 测试普通GET请求 ===");
            String getResponse = get("https://httpbin.org/get");
            System.out.println("普通GET响应: " + getResponse.substring(0, Math.min(200, getResponse.length())) + "...");

            // 测试带参数的GET请求
            System.out.println("\n=== 测试带参数的GET请求 ===");

            // 创建查询参数Map
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put("page", "1");
            queryParams.put("limit", "20");
            queryParams.put("keyword", "java");
            queryParams.put("sort", "desc");
            queryParams.put("category", "programming");

            // 测试不带请求头的带参数GET请求
            String paramResponse1 = getWithParams("https://httpbin.org/get", queryParams);
            System.out.println("带参数GET响应1: " + paramResponse1.substring(0, Math.min(300, paramResponse1.length())) + "...");

            // 测试带请求头的带参数GET请求
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer test_token_12345");
            headers.put("User-Agent", "MyHttpClient/1.0");
            headers.put("Accept", "application/json");

            String paramResponse2 = getWithParams("https://httpbin.org/get", queryParams, headers);
            System.out.println("带参数GET响应2: " + paramResponse2.substring(0, Math.min(300, paramResponse2.length())) + "...");

            // 测试URL中已包含参数的GET请求
            System.out.println("\n=== 测试URL中已包含参数的GET请求 ===");
            Map<String, String> additionalParams = new HashMap<>();
            additionalParams.put("name", "张三");
            additionalParams.put("age", "25");
            String complexUrlResponse = getWithParams("https://httpbin.org/get?base=value", additionalParams);
            System.out.println("混合参数GET响应: " + complexUrlResponse.substring(0, Math.min(300, complexUrlResponse.length())) + "...");

            // 测试特殊字符参数
            System.out.println("\n=== 测试特殊字符参数的GET请求 ===");
            Map<String, String> specialParams = new HashMap<>();
            specialParams.put("email", "user@example.com");
            specialParams.put("search", "java & spring");
            specialParams.put("score", "95.5");
            String specialResponse = getWithParams("https://httpbin.org/get", specialParams);
            System.out.println("特殊字符参数GET响应: " + specialResponse.substring(0, Math.min(300, specialResponse.length())) + "...");

            // 测试空参数的GET请求
            System.out.println("\n=== 测试空参数的GET请求 ===");
            String noParamResponse = getWithParams("https://httpbin.org/get", new HashMap<>());
            System.out.println("空参数GET响应: " + noParamResponse.substring(0, Math.min(200, noParamResponse.length())) + "...");

            // 测试POST JSON请求
            System.out.println("\n=== 测试POST JSON请求 ===");
            TestData testData = new TestData("测试用户", "test@example.com");
            String postResponse = postJson("https://httpbin.org/post", null, testData);
            System.out.println("POST JSON响应: " + postResponse.substring(0, Math.min(200, postResponse.length())) + "...");

            // 测试POST表单请求
            System.out.println("\n=== 测试POST表单请求 ===");
            Map<String, String> formData = new HashMap<>();
            formData.put("username", "testuser");
            formData.put("password", "testpass");
            formData.put("remember", "true");
            String postFormResponse = postForm("https://httpbin.org/post", null, formData);
            System.out.println("POST表单响应: " + postFormResponse.substring(0, Math.min(200, postFormResponse.length())) + "...");

            // 测试DELETE请求
            System.out.println("\n=== 测试DELETE请求 ===");
            String deleteResponse = delete("https://httpbin.org/delete");
            System.out.println("DELETE响应: " + deleteResponse.substring(0, Math.min(200, deleteResponse.length())) + "...");

            System.out.println("\n所有测试请求执行完成！");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
     */

    public static void main(String[] args) {
        try {
            /*
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put("grant_type", "client_credential");
            queryParams.put("appid", "******");
            queryParams.put("secret", "******");

            // 测试不带请求头的带参数GET请求
            String paramResponse = getWithParams("https://api.weixin.qq.com/cgi-bin/token", queryParams);
            System.out.println("带参数GET响应: " + paramResponse);
             */

            Map<String, String> specialParams = new HashMap<>();
            specialParams.put("access_token", "99_jmNUs3aY9NQ_t-VgbQlmSLn0sMqyqtgE2xVVJsvB56orXIH64cp-rF2ESVbFVRQiTR4m-F3BUkJ80F5VGwZV6XMXWrP171PZ31GFCjg-1aUlmRjpEIl8SHhSzlkLHKjAJAGCF");
            String url = buildUrlWithParams("https://api.weixin.qq.com/datacube/getusersummary", specialParams);

            TestData testData = new TestData("2025-07-24", "2025-07-26");
            String postResponse = postJson(url, null, testData);
            System.out.println("带参数GET响应: " + postResponse);
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                close();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
