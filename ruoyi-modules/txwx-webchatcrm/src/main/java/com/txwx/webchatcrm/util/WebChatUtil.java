/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the MIT License;
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 
 * the copywrite is belongs to  SpaceMV  team
 */
package com.txwx.webchatcrm.util;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.http.service.HttpUtil;
import com.txwx.webchatcrm.domain.WebChatAccessToken;
import com.txwx.webchatcrm.domain.vo.WebChatMaterialPermanentVO;
import com.txwx.webchatcrm.dto.*;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("grant_type", "client_credential");
        queryParams.put("appid", appId);
        queryParams.put("secret", secret);

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
                material.setMedia_id(result.getMedia_id());
                material.setUrl(result.getUrl());
                material.setName(file.getOriginalFilename());
                material.setUpdate_time(String.valueOf(System.currentTimeMillis() / 1000));

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
                material.setMedia_id(item.getMedia_id());
                material.setName(item.getName());
                material.setUpdate_time(item.getUpdate_time());
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

        com.txwx.webchatcrm.dto.MaterialDeleteResponse result = JSONObject.parseObject(response, com.txwx.webchatcrm.dto.MaterialDeleteResponse.class);

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
        com.txwx.webchatcrm.dto.MaterialDeleteResponse result = JSONObject.parseObject(response, com.txwx.webchatcrm.dto.MaterialDeleteResponse.class);

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

        // 使用Apache HttpClient指定UTF-8编码
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(urlStr);

        // 设置请求体，指定UTF-8编码
        String jsonBody = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(request);
        StringEntity requestEntity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
        httpPost.setEntity(requestEntity);

        String response = httpClient.execute(httpPost, httpResponse -> {
            HttpEntity entity = httpResponse.getEntity();
            return entity != null ? EntityUtils.toString(entity, "UTF-8") : "";
        });

        httpClient.close();

        GetDraftDetailResponse result = JSONObject.parseObject(response, GetDraftDetailResponse.class);

        if (result.getErrcode() != null && result.getErrcode() != 0) {
            throw new RuntimeException("获取草稿详情失败: " + result.getErrmsg());
        }

        return result;
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
        com.txwx.webchatcrm.dto.MaterialDeleteResponse result = JSONObject.parseObject(response, com.txwx.webchatcrm.dto.MaterialDeleteResponse.class);

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
        CloseableHttpClient httpClient = HttpClients.createDefault();
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
