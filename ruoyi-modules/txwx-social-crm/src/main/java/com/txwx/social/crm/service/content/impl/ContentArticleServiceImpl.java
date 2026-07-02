package com.txwx.social.crm.service.content.impl;

import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.crm.common.sanitizer.ArticleHtmlSanitizer;
import com.txwx.social.crm.domain.content.ContentArticle;
import com.txwx.social.crm.domain.content.TagCount;
import com.txwx.social.crm.mapper.content.ContentArticleMapper;
import com.txwx.social.crm.service.content.IContentArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentArticleServiceImpl implements IContentArticleService {

    private final ContentArticleMapper articleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ContentArticle create(ContentArticle article) {
        ensureSlugAvailable(article.getSlug(), null);
        String operator = currentUsername();
        article.setCreateBy(operator);
        article.setUpdateBy(operator);
        if (article.getStatus() == null || article.getStatus().isBlank()) {
            article.setStatus("DRAFT");
        }
        if (article.getVisibility() == null || article.getVisibility().isBlank()) {
            article.setVisibility("PUBLIC");
        }
        if (article.getIsPaid() == null || article.getIsPaid().isBlank()) {
            article.setIsPaid("0");
        }
        sanitizeArticleFields(article);
        articleMapper.insert(article);
        return articleMapper.selectById(article.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ContentArticle update(Long id, ContentArticle article) {
        ContentArticle current = articleMapper.selectById(id);
        if (current == null) {
            throw new ServiceException("文章不存在");
        }
        if (article.getSlug() != null && !article.getSlug().equals(current.getSlug())) {
            ensureSlugAvailable(article.getSlug(), id);
        }
        article.setId(id);
        article.setUpdateBy(currentUsername());
        sanitizeArticleFields(article);
        articleMapper.update(article);
        return articleMapper.selectById(id);
    }

    @Override
    public ContentArticle selectById(Long id) {
        return normalizeArticleHtml(articleMapper.selectById(id));
    }

    @Override
    public ContentArticle selectBySlug(String slug) {
        return normalizeArticleHtml(articleMapper.selectBySlug(slug));
    }

    @Override
    public List<ContentArticle> selectList(ContentArticle query) {
        return articleMapper.selectList(query == null ? new ContentArticle() : query);
    }

    @Override
    public List<ContentArticle> selectPublicList(ContentArticle query) {
        return articleMapper.selectPublicList(query == null ? new ContentArticle() : query);
    }

    @Override
    public ContentArticle selectPublicBySlug(String slug) {
        return articleMapper.selectPublicBySlug(slug);
    }

    @Override
    public List<ContentArticle> selectWendaoSiteList(ContentArticle query) {
        return articleMapper.selectWendaoSiteList(query == null ? new ContentArticle() : query);
    }

    @Override
    public ContentArticle selectWendaoSiteBySlug(String slug) {
        return selectWendaoSiteBySlug(slug, false, "");
    }

    @Override
    public ContentArticle selectWendaoSiteBySlug(String slug, boolean authenticated) {
        return selectWendaoSiteBySlug(slug, authenticated, "");
    }

    @Override
    public ContentArticle selectWendaoSiteBySlug(String slug, boolean authenticated, String plan) {
        ContentArticle article = articleMapper.selectWendaoSiteBySlug(slug);
        if (article == null) return null;

        boolean hasAccess = authenticated || !"1".equals(article.getIsPaid());
        // Future: when PRO subscription is implemented, replace the line above with:
        // boolean hasAccess = authenticated;
        // if ("1".equals(article.getIsPaid()) && !"PRO".equals(plan)) { hasAccess = false; }

        if (!hasAccess) {
            article.setContentHtml(null);
            article.setContentMarkdown(null);
            article.setContentJson(null);
        } else {
            normalizeArticleHtml(article);
        }
        return article;
    }

    @Override
    public List<TagCount> selectWendaoSiteTags() {
        return articleMapper.selectWendaoSiteTags();
    }

    @Override
    public List<ContentArticle> selectWendaoSiteHotList(int limit) {
        return articleMapper.selectWendaoSiteHotList(limit);
    }

    @Override
    public int deleteById(Long id) {
        return articleMapper.deleteById(id, currentUsername());
    }

    private void ensureSlugAvailable(String slug, Long selfId) {
        if (slug == null || slug.isBlank()) {
            throw new ServiceException("文章访问标识不能为空");
        }
        ContentArticle existing = articleMapper.selectBySlug(slug);
        if (existing != null && (selfId == null || !existing.getId().equals(selfId))) {
            throw new ServiceException("文章访问标识已存在");
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

    private ContentArticle normalizeArticleHtml(ContentArticle article) {
        if (article != null) {
            article.setContentHtml(ArticleHtmlSanitizer.normalizeHtml(article.getContentHtml()));
        }
        return article;
    }

    private void sanitizeArticleFields(ContentArticle article) {
        if (article.getContentHtml() != null) {
            article.setContentHtml(ArticleHtmlSanitizer.sanitizeContentHtml(article.getContentHtml()));
        }
        if (article.getTitle() != null) {
            article.setTitle(ArticleHtmlSanitizer.stripAllTags(article.getTitle()));
        }
        if (article.getSummary() != null) {
            article.setSummary(ArticleHtmlSanitizer.stripAllTags(article.getSummary()));
        }
        if (article.getAuthorName() != null) {
            article.setAuthorName(ArticleHtmlSanitizer.stripAllTags(article.getAuthorName()));
        }
    }
}
