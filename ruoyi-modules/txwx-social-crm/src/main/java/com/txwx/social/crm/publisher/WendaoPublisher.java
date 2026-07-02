package com.txwx.social.crm.publisher;

import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.crm.domain.content.ContentArticle;
import com.txwx.social.crm.domain.content.ContentPublishSiteResponse;
import com.txwx.social.crm.mapper.content.ContentArticleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * SpaceMV 问道网站发布适配器。
 * v3：不再直接操作 content_publish_job / content_publish_result，
 * 发布状态由 PublishDispatcher 统一写入 article_publish 表。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WendaoPublisher implements IContentPublisher {

    private static final String ARTICLE_STATUS_PUBLISHED = "PUBLISHED";

    private final ContentArticleMapper articleMapper;

    @Override
    public String getTargetCode() {
        return TargetCodes.SITE_WENDAO;
    }

    @Override
    public ContentPublishSiteResponse publish(ContentArticle article, PublishContext context) {
        if (article.getSlug() == null || article.getSlug().isBlank()) {
            throw new ServiceException("文章访问标识不能为空");
        }

        String operator = currentUsername();
        Date now = new Date();
        String siteUrl = "/blog/" + article.getSlug();

        // 更新文章状态为 PUBLISHED
        updateArticleStatus(article, siteUrl, operator, now);

        log.info("文章 {} 发布到 SITE_WENDAO 成功, siteUrl={}", article.getId(), siteUrl);
        return buildResponse(article, siteUrl, now);
    }

    @Override
    public String preview(ContentArticle article, String stylePreset) {
        return article.getContentHtml();
    }

    private void updateArticleStatus(ContentArticle article, String siteUrl, String operator, Date now) {
        ContentArticle update = new ContentArticle();
        update.setId(article.getId());
        update.setStatus(ARTICLE_STATUS_PUBLISHED);
        update.setCanonicalUrl(siteUrl);
        update.setPublishedAt(now);
        update.setUpdateBy(operator);
        articleMapper.update(update);
    }

    private ContentPublishSiteResponse buildResponse(ContentArticle article, String siteUrl, Date now) {
        ContentPublishSiteResponse response = new ContentPublishSiteResponse();
        response.setArticleId(article.getId());
        response.setArticleStatus(ARTICLE_STATUS_PUBLISHED);
        response.setCanonicalUrl(siteUrl);
        response.setPublishedAt(now);
        response.setTargetCode(TargetCodes.SITE_WENDAO);
        response.setSiteUrl(siteUrl);
        return response;
    }

    private String currentUsername() {
        try {
            String username = SecurityUtils.getUsername();
            return username == null || username.isBlank() ? "system" : username;
        } catch (Exception ignored) {
            return "system";
        }
    }
}
