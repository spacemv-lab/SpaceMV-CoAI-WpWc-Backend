package com.txwx.social.crm.service.content.impl;

import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.crm.controller.content.PublishTargetDTO;
import com.txwx.social.crm.domain.content.ContentArticle;
import com.txwx.social.crm.domain.content.ContentPublishJob;
import com.txwx.social.crm.domain.content.ContentPublishResult;
import com.txwx.social.crm.domain.content.ContentPublishSiteResponse;
import com.txwx.social.crm.mapper.content.ContentArticleMapper;
import com.txwx.social.crm.mapper.content.ContentPublishJobMapper;
import com.txwx.social.crm.mapper.content.ContentPublishResultMapper;
import com.txwx.social.crm.publisher.PublishContext;
import com.txwx.social.crm.publisher.PublishDispatcher;
import com.txwx.social.crm.publisher.WeChatPublisher;
import com.txwx.social.crm.service.content.IContentPublishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentPublishServiceImpl implements IContentPublishService {

    private static final String TARGET_SITE_WENDAO = "SITE_WENDAO";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String ARTICLE_STATUS_PUBLISHED = "PUBLISHED";

    private final ContentArticleMapper articleMapper;
    private final ContentPublishJobMapper publishJobMapper;
    private final ContentPublishResultMapper publishResultMapper;
    private final PublishDispatcher publishDispatcher;
    private final WeChatPublisher weChatPublisher;

    @Override
    public List<ContentPublishSiteResponse> publishToMulti(Long articleId, List<PublishTargetDTO> targets) {
        ContentArticle article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new ServiceException("文章不存在");
        }
        if (targets == null || targets.isEmpty()) {
            throw new ServiceException("发布目标不能为空");
        }

        List<ContentPublishSiteResponse> results = new ArrayList<>();
        StringBuilder errorMsg = new StringBuilder();
        boolean hasFailure = false;

        for (PublishTargetDTO target : targets) {
            try {
                PublishContext context = new PublishContext(target.getTargetCode(), target.getAccountId(), target.getStylePreset());
                ContentPublishSiteResponse result = publishDispatcher.dispatch(context, article);
                result.setTargetCode(target.getTargetCode());
                results.add(result);
                log.info("文章 {} 发布到 {} 成功", articleId, target.getTargetCode());
            } catch (Exception e) {
                log.error("文章 {} 发布到 {} 失败: {}", articleId, target.getTargetCode(), e.getMessage(), e);
                ContentPublishSiteResponse failedResult = new ContentPublishSiteResponse();
                failedResult.setTargetCode(target.getTargetCode());
                failedResult.setArticleId(articleId);
                failedResult.setArticleStatus("FAILED");
                results.add(failedResult);
                hasFailure = true;
                if (errorMsg.length() > 0) errorMsg.append("; ");
                errorMsg.append(target.getTargetCode()).append(": ").append(e.getMessage());
            }
        }

        if (hasFailure) {
            log.warn("文章 {} 多平台发布部分失败: {}", articleId, errorMsg);
        }
        return results;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ContentPublishSiteResponse publishToSite(Long articleId) {
        ContentArticle article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new ServiceException("文章不存在");
        }
        if (article.getSlug() == null || article.getSlug().isBlank()) {
            throw new ServiceException("文章访问标识不能为空");
        }

        String operator = currentUsername();
        Date now = new Date();
        String siteUrl = "/blog/" + article.getSlug();

        ContentPublishJob job = new ContentPublishJob();
        job.setArticleId(articleId);
        job.setTargetCode(TARGET_SITE_WENDAO);
        job.setStatus(STATUS_SUCCESS);
        job.setStartedAt(now);
        job.setFinishedAt(now);
        job.setOperatorId(currentUserId());
        job.setOperatorName(operator);
        job.setCreateBy(operator);
        job.setUpdateBy(operator);
        job.setRemark("publish to SITE_WENDAO");
        publishJobMapper.insert(job);

        ContentArticle updateArticle = new ContentArticle();
        updateArticle.setId(articleId);
        updateArticle.setStatus(ARTICLE_STATUS_PUBLISHED);
        updateArticle.setCanonicalUrl(siteUrl);
        updateArticle.setPublishedAt(now);
        updateArticle.setUpdateBy(operator);
        articleMapper.update(updateArticle);

        ContentPublishResult result = new ContentPublishResult();
        result.setJobId(job.getId());
        result.setArticleId(articleId);
        result.setTargetCode(TARGET_SITE_WENDAO);
        result.setSiteUrl(siteUrl);
        result.setPayloadJson("{\"targetCode\":\"SITE_WENDAO\",\"articleId\":" + articleId + "}");
        result.setResultJson("{\"status\":\"SUCCESS\",\"siteUrl\":\"" + siteUrl + "\"}");
        result.setCreateBy(operator);
        result.setUpdateBy(operator);
        result.setRemark("publish to SITE_WENDAO");
        publishResultMapper.insert(result);

        ContentPublishSiteResponse response = new ContentPublishSiteResponse();
        response.setArticleId(articleId);
        response.setArticleStatus(ARTICLE_STATUS_PUBLISHED);
        response.setCanonicalUrl(siteUrl);
        response.setPublishedAt(now);
        response.setJobId(job.getId());
        response.setResultId(result.getId());
        response.setTargetCode(TARGET_SITE_WENDAO);
        response.setSiteUrl(siteUrl);
        return response;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDraftToWeChat(Long articleId, Long accountId, String stylePreset) {
        ContentArticle article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new ServiceException("文章不存在");
        }

        PublishContext context = new PublishContext(TARGET_SITE_WENDAO, accountId, stylePreset);
        // 替换 targetCode 为微信平台编码，使 context 正确
        context.setTargetCode("WECHAT_OFFICIAL_ACCOUNT");

        String mediaId = weChatPublisher.saveDraft(article, context);

        // 回写 media_id 和 account_id 到文章
        ContentArticle update = new ContentArticle();
        update.setId(articleId);
        update.setWechatMediaId(mediaId);
        update.setWechatAccountId(accountId);
        update.setUpdateBy(currentUsername());
        articleMapper.update(update);

        log.info("文章 {} 保存微信草稿成功, mediaId={}, accountId={}", articleId, mediaId, accountId);
    }

    @Override
    public List<ContentPublishJob> selectJobsByArticleId(Long articleId) {
        return publishJobMapper.selectByArticleId(articleId);
    }

    @Override
    public List<ContentPublishResult> selectResultsByArticleId(Long articleId) {
        return publishResultMapper.selectByArticleId(articleId);
    }

    @Override
    public List<ContentPublishJob> selectJobList(ContentPublishJob query) {
        return publishJobMapper.selectList(query == null ? new ContentPublishJob() : query);
    }

    @Override
    public ContentPublishJob selectJobById(Long id) {
        return publishJobMapper.selectById(id);
    }

    @Override
    public List<ContentPublishResult> selectResultList(ContentPublishResult query) {
        return publishResultMapper.selectList(query == null ? new ContentPublishResult() : query);
    }

    @Override
    public ContentPublishResult selectResultById(Long id) {
        return publishResultMapper.selectById(id);
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
}
