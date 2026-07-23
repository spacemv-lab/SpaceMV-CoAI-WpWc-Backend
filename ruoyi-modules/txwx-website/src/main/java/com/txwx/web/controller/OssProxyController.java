/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * MinIO图片代理Controller
 * <p>
 * 解决HTTPS页面加载HTTP图片被浏览器拦截的问题。
 * 将数据库中 http://222.212.83.51:9000/... 替换为 https://api.spacemv.com/crm-website/oss-proxy/...
 * 此Controller收到请求后，从内网MinIO拉取图片返回，全链路HTTPS。
 */
@RestController
@RequestMapping("/oss-proxy")
public class OssProxyController {

    private static final Logger log = LoggerFactory.getLogger(OssProxyController.class);

    private final RestTemplate restTemplate = new RestTemplate();

    /** MinIO内网地址 */
    private static final String MINIO_BASE =
        System.getenv().getOrDefault("OSS_PROXY_MINIO_BASE", "http://YOUR_MINIO_HOST:9000");

    /** 图片Content-Type映射 */
    private static final Map<String, String> MIME_MAP = Map.of(
        "jpg", "image/jpeg",
        "jpeg", "image/jpeg",
        "png", "image/png",
        "gif", "image/gif",
        "webp", "image/webp",
        "svg", "image/svg+xml",
        "ico", "image/x-icon",
        "bmp", "image/bmp"
    );

    /**
     * 代理图片请求
     * <p>
     * 路径格式: GET /oss-proxy/{bucket}/**
     * 例如:   GET /oss-proxy/spacecrm-oss/2024/08/abc.jpg
     * <p>
     * 网关路由 /crm-website/oss-proxy/** → lb://txwx-website (StripPrefix=1)
     * 所以网关进来时路径为 /oss-proxy/spacecrm-oss/...
     */
    @GetMapping("/{bucket}/**")
    public void proxyImage(
            @PathVariable String bucket,
            HttpServletRequest request,
            HttpServletResponse response) {

        // 提取完整object路径
        String requestURI = request.getRequestURI();
        String prefix = "/oss-proxy/" + bucket + "/";
        String objectPath = requestURI.substring(prefix.length());

        if (objectPath.isEmpty()) {
            response.setStatus(404);
            return;
        }

        // 解码URL编码的中文（如 %E4%BA%8C%E7%BB%B4%E7%A0%81 → 二维码）
        // 否则 RestTemplate 会对 % 做二次编码，MinIO 找不到对象
        String decodedPath = URLDecoder.decode(objectPath, StandardCharsets.UTF_8);
        String minioUrl = MINIO_BASE + "/" + bucket + "/" + decodedPath;

        try {
            ResponseEntity<byte[]> minioResp = restTemplate.exchange(
                    minioUrl, HttpMethod.GET, null, byte[].class);

            if (minioResp.getStatusCode() == HttpStatus.OK && minioResp.getBody() != null) {
                byte[] data = minioResp.getBody();
                String ext = getExtension(objectPath);
                String contentType = MIME_MAP.getOrDefault(ext, "application/octet-stream");

                response.setContentType(contentType);
                response.setContentLength(data.length);
                response.setHeader("Cache-Control", "public, max-age=86400");

                try (OutputStream os = response.getOutputStream()) {
                    os.write(data);
                    os.flush();
                }
            } else {
                response.setStatus(minioResp.getStatusCodeValue());
            }
        } catch (Exception e) {
            log.warn("图片代理失败: {} -> {}", minioUrl, e.getMessage());
            response.setStatus(404);
        }
    }

    private String getExtension(String path) {
        int dot = path.lastIndexOf('.');
        if (dot < 0) return "";
        return path.substring(dot + 1).toLowerCase();
    }
}
