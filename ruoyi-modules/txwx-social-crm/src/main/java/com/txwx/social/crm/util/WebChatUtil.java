/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.util;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.utils.sign.RsaUtils;
import com.ruoyi.common.http.service.HttpUtil;
import com.txwx.social.crm.domain.query.ChannelAccessToken;
import com.txwx.social.crm.domain.vo.WebChatMaterialPermanentVO;
import com.txwx.social.crm.dto.*;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.util.concurrent.TimeUnit;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebChatUtil {

    /**
     * @description: 获取微信接口调用接入码
     */
    public static String getAccessToken(String appId, String secret) throws Exception{
        int maxRetries = 3;
        int retryDelayMs = 3000;
        RuntimeException lastException = null;

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                Map<String, String> queryParams = new HashMap<>();
                queryParams.put("grant_type", "client_credential");
                queryParams.put("appid", appId);
                queryParams.put("secret", RsaUtils.decryptByPrivateKey(secret));

                String paramResponse = HttpUtil.getWithParams("https://api.weixin.qq.com/cgi-bin/token", queryParams);
                ChannelAccessToken channelAccessToken = JSONObject.parseObject(paramResponse, ChannelAccessToken.class);

                if (channelAccessToken.getErrcode() != null && channelAccessToken.getErrcode() != 0) {
                    if (channelAccessToken.getErrcode() == 40164 && attempt < maxRetries) {
                        lastException = new RuntimeException("微信连通性检查失败: " + channelAccessToken.getErrmsg() + " (errcode: " + channelAccessToken.getErrcode() + ")");
                        Thread.sleep(retryDelayMs);
                        continue;
                    }
                    throw new RuntimeException("微信连通性检查失败: " + channelAccessToken.getErrmsg() + " (errcode: " + channelAccessToken.getErrcode() + ")");
                }

                return channelAccessToken.getAccess_token();
            } catch (RuntimeException e) {
                if (attempt >= maxRetries) {
                    throw e;
                }
                lastException = e;
                Thread.sleep(retryDelayMs);
            }
        }

        throw lastException != null ? lastException : new RuntimeException("获取access_token失败");
    }

    /**
     * @description: 调用微信公众号官方接口上传图文消息图片
     */
    public static String uploadGraphicInformationImage(String accessToken, MultipartFile file) throws Exception{
        File tempFile = convertMultipartFileToFile(file);
        try {
            String urlStr = "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=" + accessToken;

            String response = uploadFileWithApacheClient(urlStr, tempFile);
            UploadGraphicImageResponse result = JSONObject.parseObject(response, UploadGraphicImageResponse.class);

            if(result != null){
                if(result.getErrcode() == null){
                    return result.getUrl();
                }else{
                    throw new RuntimeException("微信官网报错:" + result.getErrcode() + ". " + result.getErrmsg());
                }
            }else{
                throw new RuntimeException("微信官网报错: 转换官网结果失败:" + response);
            }
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    /**
     * @description: 调用微信公众号官方接口上传永久素材
     */
    public static WebChatMaterialPermanentVO uploadMaterialPermanent(String accessToken, MultipartFile file) throws Exception{
        File tempFile = convertMultipartFileToFile(file);
        try {
            String urlStr = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=" + accessToken + "&type=image";

            String response = uploadFileWithApacheClient(urlStr, tempFile);
            UploadMaterialResponse result = JSONObject.parseObject(response, UploadMaterialResponse.class);

            if(result.getErrcode() == 0){
                WebChatMaterialPermanentVO material = new WebChatMaterialPermanentVO();
                material.setMediaId(result.getMedia_id());
                material.setUrl(result.getUrl());
                material.setName(file.getOriginalFilename());
                material.setUpdateTime(String.valueOf(System.currentTimeMillis() / 1000));

                return material;
            }else{
                throw new Exception("微信官网报错:" + result.getErrcode() + ". " + result.getErrmsg());
            }
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    /**
     * @description: 从URL下载图片并上传为微信图文消息图片（用于正文内容替换）
     */
    public static String uploadGraphicImageFromUrl(String accessToken, String imageUrl) throws Exception {
        URL url = new URL(imageUrl);
        File tempFile = File.createTempFile("ginfo_", ".jpg");
        try (InputStream in = url.openStream()) {
            Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        try {
            String urlStr = "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=" + accessToken;
            String response = uploadFileWithApacheClient(urlStr, tempFile);
            UploadGraphicImageResponse result = JSONObject.parseObject(response, UploadGraphicImageResponse.class);
            if (result != null && result.getErrcode() == null) {
                return result.getUrl();
            } else {
                throw new Exception("微信官网报错:" + (result != null ? result.getErrcode() + ". " + result.getErrmsg() : "响应为空"));
            }
        } finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    /**
     * @description: 从URL下载图片并上传为微信永久素材
     */
    public static WebChatMaterialPermanentVO uploadImageFromUrl(String accessToken, String imageUrl) throws Exception {
        URL url = new URL(imageUrl);
        String fileName = "cover_" + System.currentTimeMillis() + ".jpg";
        File tempFile = File.createTempFile("cover_", ".jpg");
        try (InputStream in = url.openStream()) {
            Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        try {
            String urlStr = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=" + accessToken + "&type=image";
            String response = uploadFileWithApacheClient(urlStr, tempFile);
            UploadMaterialResponse result = JSONObject.parseObject(response, UploadMaterialResponse.class);
            if (result.getErrcode() == 0) {
                WebChatMaterialPermanentVO material = new WebChatMaterialPermanentVO();
                material.setMediaId(result.getMedia_id());
                material.setUrl(result.getUrl());
                material.setName(fileName);
                material.setUpdateTime(String.valueOf(System.currentTimeMillis() / 1000));
                return material;
            } else {
                throw new Exception("微信官网报错:" + result.getErrcode() + ". " + result.getErrmsg());
            }
        } finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    /**
     * @description: 调用微信公众号官方接口查询永久素材列表
     */
    public static List<WebChatMaterialPermanentVO> searchMaterialPermanentList(String accessToken, int offset, int count) throws Exception{
        String urlStr = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=" + accessToken;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("type", "image");
        requestBody.put("offset", offset);
        requestBody.put("count", count);

        String response = HttpUtil.postJson(urlStr, null, requestBody);
        MaterialListResponse result = JSONObject.parseObject(response, MaterialListResponse.class);

        List<WebChatMaterialPermanentVO> materialList = new ArrayList<>();
        if (result.getItem() != null) {
            for (MaterialItem item : result.getItem()) {
                WebChatMaterialPermanentVO material = new WebChatMaterialPermanentVO();
                material.setMediaId(item.getMedia_id());
                material.setName(item.getName());
                material.setUpdateTime(item.getUpdate_time());
                material.setUrl(item.getUrl());
                materialList.add(material);
            }
        }

        return materialList;
    }

    /**
     * @description: 调用微信公众号官方接口获取永久素材总数
     */
    public static MaterialCountResponse getMaterialCount(String accessToken) throws Exception{
        String urlStr = "https://api.weixin.qq.com/cgi-bin/material/get_materialcount?access_token=" + accessToken;

        String response = HttpUtil.get(urlStr);
        MaterialCountResponse result = JSONObject.parseObject(response, MaterialCountResponse.class);

        if (result.getErrcode() != null && result.getErrcode() != 0) {
            throw new RuntimeException("获取永久素材总数失败: " + result.getErrmsg());
        }

        return result;
    }

    /**
     * @description: 调用微信公众号官方接口删除永久素材
     */
    public static void deleteMaterialPermanent(String accessToken, String mediaId) throws Exception{
        String urlStr = "https://api.weixin.qq.com/cgi-bin/material/del_material?access_token=" + accessToken;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("media_id", mediaId);

        String response = HttpUtil.postJson(urlStr, null, requestBody);
        MaterialDeleteResponse result = JSONObject.parseObject(response, MaterialDeleteResponse.class);

        if (result.getErrcode() != null && result.getErrcode() != 0) {
            throw new RuntimeException("删除永久素材失败: " + result.getErrmsg());
        }
    }

    /**
     * @description: 调用微信公众号官方接口新增草稿
     */
    public static AddDraftResponse addDraft(String accessToken, ArticleDTO articleDTO) throws Exception {
        String urlStr = "https://api.weixin.qq.com/cgi-bin/draft/add?access_token=" + accessToken;

        String response = HttpUtil.postJson(urlStr, null, articleDTO);
        AddDraftResponse result = JSONObject.parseObject(response, AddDraftResponse.class);

        if (result.getErrcode() != null && result.getErrcode() != 0) {
            throw new RuntimeException("新增草稿失败: " + result.getErrmsg());
        }

        return result;
    }

    /**
     * @description: 调用微信公众号官方接口更新草稿
     */
    public static void updateDraft(String accessToken, ArticleUpdateDTO articleDTO) throws Exception {
        String urlStr = "https://api.weixin.qq.com/cgi-bin/draft/update?access_token=" + accessToken;

        String response = HttpUtil.postJson(urlStr, null, articleDTO);

        com.txwx.social.crm.dto.MaterialDeleteResponse result = JSONObject.parseObject(response, com.txwx.social.crm.dto.MaterialDeleteResponse.class);

        if (result.getErrcode() != null && result.getErrcode() != 0) {
            throw new RuntimeException("更新草稿失败: " + result.getErrmsg());
        }
    }

    /**
     * @description: 调用微信公众号官方接口删除草稿
     */
    public static void deleteDraft(String accessToken, String mediaId) throws Exception {
        String urlStr = "https://api.weixin.qq.com/cgi-bin/draft/delete?access_token=" + accessToken;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("media_id", mediaId);

        String response = HttpUtil.postJson(urlStr, null, requestBody);
        com.txwx.social.crm.dto.MaterialDeleteResponse result = JSONObject.parseObject(response, com.txwx.social.crm.dto.MaterialDeleteResponse.class);

        if (result.getErrcode() != null && result.getErrcode() != 0) {
            throw new RuntimeException("删除草稿失败: " + result.getErrmsg());
        }
    }

    /**
     * @description: 调用微信公众号官方接口获取草稿详情
     */
    public static GetDraftDetailResponse getDraftDetail(String accessToken, String mediaId) throws Exception {
        String urlStr = "https://api.weixin.qq.com/cgi-bin/draft/get?access_token=" + accessToken;

        GetDraftDetailRequest request = new GetDraftDetailRequest();
        request.setMedia_id(mediaId);

        // 使用try-with-resources确保HttpClient自动关闭
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(10, TimeUnit.SECONDS)
                .setResponseTimeout(30, TimeUnit.SECONDS)
                .build();
        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .build()) {
            HttpPost httpPost = new HttpPost(urlStr);

            // 设置请求体，指定UTF-8编码
            String jsonBody = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(request);
            StringEntity requestEntity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
            httpPost.setEntity(requestEntity);

            String response = httpClient.execute(httpPost, httpResponse -> {
                HttpEntity entity = httpResponse.getEntity();
                return entity != null ? EntityUtils.toString(entity, "UTF-8") : "";
            });

            GetDraftDetailResponse result = JSONObject.parseObject(response, GetDraftDetailResponse.class);

            if (result.getErrcode() != null && result.getErrcode() != 0) {
                throw new RuntimeException("获取草稿详情失败: " + result.getErrmsg());
            }

            return result;
        }
    }

    /**
     * @description: 调用微信公众号官方接口查询发布状态
     */
    public static GetPublishStatusResponse getPublishStatus(String accessToken, String publishId) throws Exception {
        String urlStr = "https://api.weixin.qq.com/cgi-bin/freepublish/get?access_token=" + accessToken;

        GetPublishStatusRequest request = new GetPublishStatusRequest();
        request.setPublish_id(publishId);

        String response = HttpUtil.postJson(urlStr, null, request);
        GetPublishStatusResponse result = JSONObject.parseObject(response, GetPublishStatusResponse.class);

        if (result.getErrcode() != null && result.getErrcode() != 0) {
            throw new RuntimeException("查询发布状态失败: " + result.getErrmsg());
        }

        return result;
    }

    /**
     * @description: 调用微信公众号官方接口获取草稿列表
     */
    public static GetDraftListResponse getDraftList(String accessToken, Integer offset, Integer count, Integer noContent) throws Exception {
        String urlStr = "https://api.weixin.qq.com/cgi-bin/draft/batchget?access_token=" + accessToken;

        GetDraftListRequest request = new GetDraftListRequest();
        request.setOffset(offset);
        request.setCount(count);
        request.setNo_content(noContent);

        String response = HttpUtil.postJson(urlStr, null, request);
        GetDraftListResponse result = JSONObject.parseObject(response, GetDraftListResponse.class);

        if (result.getErrcode() != null && result.getErrcode() != 0) {
            throw new RuntimeException("获取草稿列表失败: " + result.getErrmsg());
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
     * @description: 调用微信公众号官方接口发布草稿
     */
    public static PublishDraftResponse publishDraft(String accessToken, String mediaId) throws Exception {
        String urlStr = "https://api.weixin.qq.com/cgi-bin/freepublish/submit?access_token=" + accessToken;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("media_id", mediaId);

        String response = HttpUtil.postJson(urlStr, null, requestBody);
        PublishDraftResponse result = JSONObject.parseObject(response, PublishDraftResponse.class);

        if (result.getErrcode() != null && result.getErrcode() != 0) {
            throw new RuntimeException("发布草稿失败: " + result.getErrmsg());
        }

        return result;
    }

    /**
     * @description: 调用微信公众号官方接口删除已发布文章
     */
    public static void deletePublishedArticle(String accessToken, int idx, String articleId) throws Exception {
        String urlStr = "https://api.weixin.qq.com/cgi-bin/freepublish/delete?access_token=" + accessToken;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("article_id", articleId);
        requestBody.put("index", idx);

        String response = HttpUtil.postJson(urlStr, null, requestBody);
        com.txwx.social.crm.dto.MaterialDeleteResponse result = JSONObject.parseObject(response, com.txwx.social.crm.dto.MaterialDeleteResponse.class);

        if (result.getErrcode() != null && result.getErrcode() != 0) {
            throw new RuntimeException("删除已发布文章失败: " + result.getErrmsg());
        }
    }

    /**
     * @description: 转换MultipartFile为File
     */
    private static File convertMultipartFileToFile(MultipartFile multipartFile) throws Exception {
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        File tempFile = File.createTempFile("upload_", extension);
        Path path = tempFile.toPath();
        Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return tempFile;
    }

    /**
     * @description: 使用Apache HttpClient上传文件
     */
    private static String uploadFileWithApacheClient(String urlStr, File file) throws Exception {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(10, TimeUnit.SECONDS)
                .setResponseTimeout(60, TimeUnit.SECONDS)
                .build();
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .build();
        HttpPost httpPost = new HttpPost(urlStr);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            contentType = ContentType.APPLICATION_OCTET_STREAM.getMimeType();
        }

        builder.addBinaryBody("media", file,
                ContentType.parse(contentType), file.getName());

        httpPost.setEntity(builder.build());

        try {
            return httpClient.execute(httpPost, response -> {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : "";
            });
        } finally {
            httpClient.close();
        }
    }
}
