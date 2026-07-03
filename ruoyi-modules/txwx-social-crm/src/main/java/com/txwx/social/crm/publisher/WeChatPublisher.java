package com.txwx.social.crm.publisher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.crm.domain.content.ContentArticle;
import com.txwx.social.crm.domain.content.ContentPublishJob;
import com.txwx.social.crm.domain.content.ContentPublishResult;
import com.txwx.social.crm.domain.content.ContentPublishSiteResponse;
import com.txwx.social.crm.domain.po.TxwxAccountPO;
import com.txwx.social.crm.domain.po.TxwxPermanentMaterialImagePO;
import com.txwx.social.crm.domain.vo.WebChatMaterialPermanentVO;
import com.txwx.social.crm.dto.AddDraftResponse;
import com.txwx.social.crm.dto.ArticleDTO;
import com.txwx.social.crm.dto.ArticleItemDTO;
import com.txwx.social.crm.dto.ArticleUpdateDTO;
import com.txwx.social.crm.dto.PublishDraftResponse;
import com.txwx.social.crm.mapper.TxwxPermanentMaterialImageMapper;
import com.txwx.social.crm.mapper.content.ContentPublishJobMapper;
import com.txwx.social.crm.mapper.content.ContentPublishResultMapper;
import com.txwx.social.crm.publisher.formatter.IContentFormatter;
import com.txwx.social.crm.publisher.formatter.WeChatHtmlAdapter;
import com.txwx.social.crm.service.IAccountService;
import com.txwx.social.crm.util.WebChatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class WeChatPublisher implements IContentPublisher {

    private static final String TARGET_CODE = TargetCodes.WECHAT_OFFICIAL_ACCOUNT;
    private static final String STATUS_PROCESSING = "PROCESSING";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final IAccountService accountService;
    private final ContentPublishJobMapper publishJobMapper;
    private final ContentPublishResultMapper publishResultMapper;
    private final TxwxPermanentMaterialImageMapper permanentMaterialImageMapper;
    private final IContentFormatter weChatFormatter;
    private final WeChatHtmlAdapter weChatHtmlAdapter;

    public WeChatPublisher(IAccountService accountService,
                           ContentPublishJobMapper publishJobMapper,
                           ContentPublishResultMapper publishResultMapper,
                           TxwxPermanentMaterialImageMapper permanentMaterialImageMapper,
                           WeChatHtmlAdapter weChatHtmlAdapter,
                           List<IContentFormatter> formatters) {
        this.accountService = accountService;
        this.publishJobMapper = publishJobMapper;
        this.publishResultMapper = publishResultMapper;
        this.permanentMaterialImageMapper = permanentMaterialImageMapper;
        this.weChatHtmlAdapter = weChatHtmlAdapter;
        this.weChatFormatter = formatters.stream()
                .filter(f -> TARGET_CODE.equals(f.getTargetCode()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("WeChatFormatter not found, cannot publish to WeChat"));
    }

    @Override
    public String getTargetCode() {
        return TARGET_CODE;
    }

    @Override
    public ContentPublishSiteResponse publish(ContentArticle article, PublishContext context) {
        if (context.getAccountId() == null) {
            throw new ServiceException("微信公众号账号ID不能为空");
        }

        ContentPublishJob existing = publishJobMapper.selectSuccessByArticleIdAndTarget(article.getId(), TARGET_CODE);
        if (existing != null) {
            log.info("文章 {} 已有微信发布记录 (jobId={}), 跳过重复发布", article.getId(), existing.getId());
            ContentPublishSiteResponse resp = new ContentPublishSiteResponse();
            resp.setArticleId(article.getId());
            resp.setArticleStatus(article.getStatus());
            resp.setJobId(existing.getId());
            resp.setResultId(existing.getResultId());
            resp.setTargetCode(TARGET_CODE);
            resp.setSiteUrl(existing.getSiteUrl());
            resp.setExternalUrl(existing.getExternalUrl());
            return resp;
        }

        TxwxAccountPO account = accountService.selectAccountById(context.getAccountId());
        if (account == null) {
            throw new ServiceException("微信公众号账号不存在: " + context.getAccountId());
        }

        String accessToken;
        try {
            accessToken = WebChatUtil.getAccessToken(account.getAppId(), account.getSecret());
        } catch (Exception e) {
            throw new ServiceException("获取微信公众号 access_token 失败: " + e.getMessage());
        }

        if (article.getCoverUrl() == null || article.getCoverUrl().isBlank()) {
            throw new ServiceException("文章没有设置封面图，无法发布到微信公众号");
        }

        Date now = new Date();
        String thumbMediaId = resolveCoverMediaId(article, context, accessToken, now);

        String wechatContentHtml = buildWechatContentHtml(article, context);
        wechatContentHtml = convertInlineImages(accessToken, wechatContentHtml, context.getAccountId());

        ArticleItemDTO item = buildArticleItem(article, wechatContentHtml, thumbMediaId);

        String mediaId = article.getWechatMediaId();
        boolean draftExists = false;
        if (mediaId != null && !mediaId.isBlank() && context.getAccountId().equals(article.getWechatAccountId())) {
            ArticleUpdateDTO updateDTO = new ArticleUpdateDTO();
            updateDTO.setMedia_id(mediaId);
            updateDTO.setIndex(0);
            updateDTO.setArticles(item);
            try {
                WebChatUtil.updateDraft(accessToken, updateDTO);
                draftExists = true;
                log.info("文章 {} 更新微信草稿成功, mediaId={}", article.getId(), mediaId);
            } catch (Exception e) {
                log.warn("更新微信草稿失败, 将创建新草稿: {}", e.getMessage());
            }
        }

        if (!draftExists) {
            ArticleDTO articleDTO = new ArticleDTO();
            articleDTO.setArticles(List.of(item));
            try {
                AddDraftResponse draftResponse = WebChatUtil.addDraft(accessToken, articleDTO);
                mediaId = draftResponse.getMedia_id();
                log.info("文章 {} 创建微信草稿成功, mediaId={}", article.getId(), mediaId);
            } catch (Exception e) {
                throw new ServiceException("创建微信草稿失败: " + e.getMessage());
            }
        }

        PublishDraftResponse publishResponse;
        try {
            publishResponse = WebChatUtil.publishDraft(accessToken, mediaId);
        } catch (Exception e) {
            throw new ServiceException("提交微信发布失败: " + e.getMessage());
        }

        String operator = currentUsername();

        ContentPublishJob job = new ContentPublishJob();
        job.setArticleId(article.getId());
        job.setTargetCode(TARGET_CODE);
        job.setStatus(STATUS_PROCESSING);
        job.setStartedAt(now);
        job.setOperatorId(currentUserId());
        job.setOperatorName(operator);
        job.setCreateBy(operator);
        job.setUpdateBy(operator);
        job.setRemark("publish to WECHAT_OFFICIAL_ACCOUNT");
        publishJobMapper.insert(job);

        ContentPublishResult result = new ContentPublishResult();
        result.setJobId(job.getId());
        result.setArticleId(article.getId());
        result.setTargetCode(TARGET_CODE);
        result.setExternalId(mediaId);
        result.setPayloadJson("{\"targetCode\":\"WECHAT_OFFICIAL_ACCOUNT\",\"articleId\":" + article.getId()
                + ",\"accountId\":" + context.getAccountId()
                + ",\"mediaId\":\"" + mediaId
                + "\",\"publishId\":\"" + publishResponse.getPublish_id() + "\"}");
        result.setResultJson("{\"status\":\"PROCESSING\",\"publishId\":\"" + publishResponse.getPublish_id() + "\"}");
        result.setCreateBy(operator);
        result.setUpdateBy(operator);
        result.setRemark("publish to WECHAT_OFFICIAL_ACCOUNT");
        publishResultMapper.insert(result);

        log.info("文章 {} 提交到微信发布成功, mediaId={}, publishId={}", article.getId(), mediaId, publishResponse.getPublish_id());

        ContentPublishSiteResponse response = new ContentPublishSiteResponse();
        response.setArticleId(article.getId());
        response.setArticleStatus(article.getStatus());
        response.setJobId(job.getId());
        response.setResultId(result.getId());
        response.setTargetCode(TARGET_CODE);
        return response;
    }

    public String saveDraft(ContentArticle article, PublishContext context) {
        if (context.getAccountId() == null) {
            throw new ServiceException("微信公众号账号ID不能为空");
        }

        TxwxAccountPO account = accountService.selectAccountById(context.getAccountId());
        if (account == null) {
            throw new ServiceException("微信公众号账号不存在: " + context.getAccountId());
        }

        String accessToken;
        try {
            accessToken = WebChatUtil.getAccessToken(account.getAppId(), account.getSecret());
        } catch (Exception e) {
            throw new ServiceException("获取微信公众号 access_token 失败: " + e.getMessage());
        }

        if (article.getCoverUrl() == null || article.getCoverUrl().isBlank()) {
            throw new ServiceException("文章没有设置封面图，无法保存到微信公众号草稿箱");
        }

        Date now = new Date();
        String thumbMediaId = resolveCoverMediaId(article, context, accessToken, now);

        String wechatContentHtml = buildWechatContentHtml(article, context);
        wechatContentHtml = convertInlineImages(accessToken, wechatContentHtml, context.getAccountId());

        ArticleItemDTO item = buildArticleItem(article, wechatContentHtml, thumbMediaId);

        String mediaId = article.getWechatMediaId();
        boolean sameAccount = context.getAccountId().equals(article.getWechatAccountId());
        if (mediaId != null && !mediaId.isBlank() && sameAccount) {
            ArticleUpdateDTO updateDTO = new ArticleUpdateDTO();
            updateDTO.setMedia_id(mediaId);
            updateDTO.setIndex(0);
            updateDTO.setArticles(item);
            try {
                WebChatUtil.updateDraft(accessToken, updateDTO);
                log.info("文章 {} 更新微信草稿成功, mediaId={}", article.getId(), mediaId);
                return mediaId;
            } catch (Exception e) {
                log.warn("更新微信草稿失败, 将创建新草稿: {}", e.getMessage());
            }
        }

        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setArticles(List.of(item));
        try {
            AddDraftResponse draftResponse = WebChatUtil.addDraft(accessToken, articleDTO);
            log.info("文章 {} 创建微信草稿成功, mediaId={}", article.getId(), draftResponse.getMedia_id());
            return draftResponse.getMedia_id();
        } catch (Exception e) {
            throw new ServiceException("创建微信草稿失败: " + e.getMessage());
        }
    }

    @Override
    public String preview(ContentArticle article, String stylePreset) {
        PublishContext context = new PublishContext(TARGET_CODE, article.getWechatAccountId(), stylePreset);
        return buildWechatContentHtml(article, context);
    }

    private String buildWechatContentHtml(ContentArticle article, PublishContext context) {
        if (article.getContentHtml() != null && !article.getContentHtml().isBlank()) {
            return weChatHtmlAdapter.adapt(article, buildChartImageMap(article), buildMapImageMap(article));
        }
        return weChatFormatter.format(article.getContentJson(), context.getStylePreset());
    }

    private Map<String, String> buildChartImageMap(ContentArticle article) {
        Map<String, String> chartImageMap = new HashMap<>();
        String contentJson = article.getContentJson();
        if (contentJson == null || contentJson.isBlank()) {
            return chartImageMap;
        }
        try {
            collectChartImages(OBJECT_MAPPER.readTree(contentJson), chartImageMap);
        } catch (Exception e) {
            log.warn("解析文章图表图片映射失败, articleId={}, error={}", article.getId(), e.getMessage());
        }
        return chartImageMap;
    }

    private void collectChartImages(JsonNode node, Map<String, String> chartImageMap) {
        if (node == null || node.isNull()) {
            return;
        }
        if (node.isObject()) {
            if ("chart".equals(node.path("type").asText())) {
                JsonNode attrs = node.path("attrs");
                String slug = attrs.path("slug").asText("");
                String wechatImageUrl = attrs.path("wechatImageUrl").asText("");
                String imageUrl = attrs.path("imageUrl").asText("");
                if (!slug.isBlank()) {
                    if (!wechatImageUrl.isBlank()) {
                        chartImageMap.put(slug, wechatImageUrl);
                    } else if (!imageUrl.isBlank()) {
                        chartImageMap.put(slug, imageUrl);
                    }
                }
            }
            node.fields().forEachRemaining(entry -> collectChartImages(entry.getValue(), chartImageMap));
            return;
        }
        if (node.isArray()) {
            for (JsonNode child : node) {
                collectChartImages(child, chartImageMap);
            }
        }
    }

    private Map<String, String> buildMapImageMap(ContentArticle article) {
        Map<String, String> mapImageMap = new HashMap<>();
        String contentJson = article.getContentJson();
        if (contentJson == null || contentJson.isBlank()) {
            return mapImageMap;
        }
        try {
            collectMapImages(OBJECT_MAPPER.readTree(contentJson), mapImageMap);
        } catch (Exception e) {
            log.warn("解析文章地图图片映射失败, articleId={}, error={}", article.getId(), e.getMessage());
        }
        return mapImageMap;
    }

    private void collectMapImages(JsonNode node, Map<String, String> mapImageMap) {
        if (node == null || node.isNull()) {
            return;
        }
        if (node.isObject()) {
            if ("map".equals(node.path("type").asText())) {
                JsonNode attrs = node.path("attrs");
                String token = attrs.path("token").asText("");
                String wechatImageUrl = attrs.path("wechatImageUrl").asText("");
                String imageUrl = attrs.path("imageUrl").asText("");
                if (!token.isBlank()) {
                    if (!wechatImageUrl.isBlank()) {
                        mapImageMap.put(token, wechatImageUrl);
                    } else if (!imageUrl.isBlank()) {
                        mapImageMap.put(token, imageUrl);
                    }
                }
            }
            node.fields().forEachRemaining(entry -> collectMapImages(entry.getValue(), mapImageMap));
            return;
        }
        if (node.isArray()) {
            for (JsonNode child : node) {
                collectMapImages(child, mapImageMap);
            }
        }
    }

    private String currentUsername() {
        try {
            String username = SecurityUtils.getUsername();
            return username == null || username.isBlank() ? "system" : username;
        } catch (Exception ignored) {
            return "system";
        }
    }

    private Long currentUserId() {
        try {
            return SecurityUtils.getUserId();
        } catch (Exception ignored) {
            return null;
        }
    }

    private String resolveCoverMediaId(ContentArticle article, PublishContext context, String accessToken, Date now) {
        String coverSourceUrl = article.getCoverUrl();
        TxwxPermanentMaterialImagePO cached = permanentMaterialImageMapper
                .selectPermanentMaterialImageBySourceUrl(coverSourceUrl, context.getAccountId());
        if (cached != null) {
            log.info("封面图命中缓存 sourceUrl={}, mediaId={}", coverSourceUrl, cached.getMediaId());
            return cached.getMediaId();
        }
        try {
            WebChatMaterialPermanentVO material = WebChatUtil.uploadImageFromUrl(accessToken, coverSourceUrl);
            TxwxPermanentMaterialImagePO entity = new TxwxPermanentMaterialImagePO();
            entity.setMediaId(material.getMediaId());
            entity.setName("cover_" + article.getId() + "_" + System.currentTimeMillis());
            entity.setUrl(material.getUrl());
            entity.setSourceUrl(coverSourceUrl);
            entity.setAccountId(context.getAccountId());
            entity.setCreateTime(now);
            entity.setCreateBy(currentUsername());
            permanentMaterialImageMapper.insertPermanentMaterialImage(entity);
            return material.getMediaId();
        } catch (Exception e) {
            throw new ServiceException("上传封面图片到微信永久素材失败: " + e.getMessage());
        }
    }

    private ArticleItemDTO buildArticleItem(ContentArticle article, String wechatContentHtml, String thumbMediaId) {
        ArticleItemDTO item = new ArticleItemDTO();
        item.setTitle(article.getTitle());
        item.setAuthor(article.getAuthorName() != null ? article.getAuthorName() : "");
        item.setDigest(article.getSummary() != null ? article.getSummary() : "");
        item.setContent(wechatContentHtml);
        item.setThumb_media_id(thumbMediaId);
        item.setNeed_open_comment(1);
        item.setOnly_fans_can_comment(0);
        return item;
    }

    private String convertInlineImages(String accessToken, String contentHtml, Long accountId) {
        if (contentHtml == null || contentHtml.isBlank()) {
            return contentHtml;
        }
        Pattern imgPattern = Pattern.compile("<img[^>]+src\\s*=\\s*[\"']([^\"']+)[\"'][^>]*>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = imgPattern.matcher(contentHtml);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String src = matcher.group(1);
            String originalTag = matcher.group(0);
            if (src.contains("mmbiz.qpic.cn") || src.contains("mmbiz.qlogo.cn")) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(originalTag));
                continue;
            }
            TxwxPermanentMaterialImagePO cached = permanentMaterialImageMapper
                    .selectPermanentMaterialImageBySourceUrl(src, accountId);
            String wechatUrl;
            if (cached != null) {
                wechatUrl = cached.getUrl();
            } else {
                try {
                    wechatUrl = WebChatUtil.uploadGraphicImageFromUrl(accessToken, src);
                    TxwxPermanentMaterialImagePO entity = new TxwxPermanentMaterialImagePO();
                    entity.setMediaId("inline_" + System.currentTimeMillis());
                    entity.setName("inline_" + System.currentTimeMillis());
                    entity.setUrl(wechatUrl);
                    entity.setSourceUrl(src);
                    entity.setAccountId(accountId);
                    entity.setCreateTime(new Date());
                    entity.setCreateBy(currentUsername());
                    permanentMaterialImageMapper.insertPermanentMaterialImage(entity);
                } catch (Exception e) {
                    log.warn("上传正文图片到微信 CDN 失败, 保留原URL: {}, error: {}", src, e.getMessage());
                    matcher.appendReplacement(sb, Matcher.quoteReplacement(originalTag));
                    continue;
                }
            }
            String replacedTag = originalTag.replace(src, wechatUrl);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacedTag));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
