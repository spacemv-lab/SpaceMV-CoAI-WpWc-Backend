/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.common.http.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

@Component
public class HttpUtil {
    private static final CloseableHttpClient httpClient;
    private static final ObjectMapper objectMapper;

    static {
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(10, TimeUnit.SECONDS)
            .setResponseTimeout(30, TimeUnit.SECONDS)
            .build();
        httpClient = HttpClientBuilder.create()
            .setDefaultRequestConfig(requestConfig)
            .build();
        objectMapper = new ObjectMapper();
    }

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
            }
            throw new RuntimeException("GET request failed, status: " + statusCode + ", response: " + responseBody);
        });
    }

    public static String get(String url) throws Exception {
        return get(url, null);
    }

    public static String getWithParams(String baseUrl, Map<String, String> params, Map<String, String> headers) throws Exception {
        String url = buildUrlWithParams(baseUrl, params);
        return get(url, headers);
    }

    public static String getWithParams(String baseUrl, Map<String, String> params) throws Exception {
        return getWithParams(baseUrl, params, null);
    }

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
            }
            return baseUrl + "?" + queryString;
        }
    }

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
            }
            throw new RuntimeException("POST request failed, status: " + statusCode + ", response: " + responseBody);
        });
    }

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
            }
            throw new RuntimeException("POST form request failed, status: " + statusCode + ", response: " + responseBody);
        });
    }

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
            }
            throw new RuntimeException("DELETE request failed, status: " + statusCode + ", response: " + responseBody);
        });
    }

    public static String delete(String url) throws Exception {
        return delete(url, null);
    }

    public static void close() throws Exception {
        if (httpClient != null) {
            httpClient.close();
        }
    }
}
