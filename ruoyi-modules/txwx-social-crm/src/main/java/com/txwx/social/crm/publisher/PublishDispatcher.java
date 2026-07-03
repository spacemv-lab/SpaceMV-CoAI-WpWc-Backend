package com.txwx.social.crm.publisher;

import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.crm.domain.content.ArticlePublish;
import com.txwx.social.crm.domain.content.ContentArticle;
import com.txwx.social.crm.domain.content.ContentPublishSiteResponse;
import com.txwx.social.crm.service.content.IArticlePublishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发布调度器。
 * 根据 targetCode 路由到对应的 IContentPublisher 实现类。
 * 新增平台只需新增 @Component 实现类，调度器自动发现。
 *
 * v3 变更：
 * - 发布时自动写入 article_publish 记录（含 contentJson 快照）
 * - 提供 retryPlatform 方法进行单平台重试
 * - 后续新增平台只需新增 @Component 实现类，调度器自动发现
 */
@Slf4j
@Component
public class PublishDispatcher {

    private final Map<String, IContentPublisher> publisherMap;

    private final IArticlePublishService articlePublishService;

    public PublishDispatcher(List<IContentPublisher> publishers,
                             IArticlePublishService articlePublishService) {
        this.articlePublishService = articlePublishService;
        Map<String, IContentPublisher> map = new HashMap<>();
        for (IContentPublisher publisher : publishers) {
            String code = publisher.getTargetCode();
            if (map.containsKey(code)) {
                log.warn("发现重复的发布适配器 targetCode={}，后注册的将覆盖", code);
            }
            map.put(code, publisher);
        }
        this.publisherMap = map;
        log.info("PublishDispatcher 已注册 {} 个发布适配器: {}", publisherMap.size(), publisherMap.keySet());
    }

    /**
     * 调度发布到指定平台。
     * 自动创建/更新 article_publish 记录。
     */
    @Transactional(rollbackFor = Exception.class)
    public ContentPublishSiteResponse dispatch(PublishContext context, ContentArticle article) {
        IContentPublisher publisher = publisherMap.get(context.getTargetCode());
        if (publisher == null) {
            throw new ServiceException("不支持的发布目标: " + context.getTargetCode());
        }

        // 1. 创建或获取 article_publish 记录
        ArticlePublish publishRecord = ensurePublishRecord(article, context);

        // 2. 标记为发布中
        publishRecord.setStatus("PUBLISHING");
        publishRecord.setUpdateBy(currentUsername());
        articlePublishService.update(publishRecord);

        try {
            // 3. 执行发布
            ContentPublishSiteResponse response = publisher.publish(article, context);

            // 4. 发布成功 → 更新状态
            publishRecord.setStatus("SUCCESS");
            publishRecord.setPlatformUrl(response.getSiteUrl());
            publishRecord.setPublishedAt(new Date());
            publishRecord.setErrorCode(null);
            publishRecord.setErrorMessage(null);
            publishRecord.setUpdateBy(currentUsername());
            articlePublishService.update(publishRecord);

            log.info("文章 {} 发布到 {} 成功, url={}", article.getId(), context.getTargetCode(), response.getSiteUrl());
            return response;

        } catch (Exception e) {
            // 5. 发布失败 → 更新状态
            publishRecord.setStatus("FAILED");
            publishRecord.setErrorCode(e.getClass().getSimpleName());
            publishRecord.setErrorMessage(e.getMessage());
            publishRecord.setUpdateBy(currentUsername());
            articlePublishService.update(publishRecord);

            log.error("文章 {} 发布到 {} 失败: {}", article.getId(), context.getTargetCode(), e.getMessage(), e);
            throw new ServiceException("发布到 " + context.getTargetCode() + " 失败: " + e.getMessage());
        }
    }

    /**
     * 单平台重试。
     * 用当前最新的 contentJson 重新发布到指定平台。
     */
    @Transactional(rollbackFor = Exception.class)
    public ContentPublishSiteResponse retryPlatform(PublishContext context, ContentArticle article) {
        // 1. 查找现有发布记录
        ArticlePublish existing = articlePublishService.selectByArticleAndPlatform(
                article.getId(), context.getTargetCode());
        if (existing == null) {
            throw new ServiceException(
                    String.format("文章 %d 在平台 %s 没有发布记录，请先发布", article.getId(), context.getTargetCode()));
        }

        // 2. 如果 context 中没有 stylePreset，从已有记录中读取
        if (context.getStylePreset() == null && existing.getStylePreset() != null) {
            context.setStylePreset(existing.getStylePreset());
        }

        // 3. 更新 snapshot 为当前最新 contentJson
        existing.setContentJsonSnapshot(article.getContentJson());
        existing.setStatus("PENDING");
        existing.setErrorCode(null);
        existing.setErrorMessage(null);
        existing.setUpdateBy(currentUsername());
        articlePublishService.update(existing);

        // 4. 递增重试计数
        articlePublishService.incrementRetryCount(existing.getId());

        // 5. 调用 dispatch 重新发布
        return dispatch(context, article);
    }

    /**
     * 获取所有已注册的平台编码。
     */
    public List<String> getSupportedTargets() {
        return List.copyOf(publisherMap.keySet());
    }

    /**
     * 预览文章在指定平台的渲染效果。
     */
    public String preview(String targetCode, ContentArticle article, String stylePreset) {
        IContentPublisher publisher = publisherMap.get(targetCode);
        if (publisher == null) {
            throw new ServiceException("不支持的预览目标: " + targetCode);
        }
        return publisher.preview(article, stylePreset);
    }

    /**
     * 确保某篇文章在某个平台有 publish 记录，没有则创建。
     */
    private ArticlePublish ensurePublishRecord(ContentArticle article, PublishContext context) {
        ArticlePublish existing = articlePublishService.selectByArticleAndPlatform(
                article.getId(), context.getTargetCode());
        if (existing != null) {
            // 已有记录 → 更新 snapshot + stylePreset（用最新）
            existing.setContentJsonSnapshot(article.getContentJson());
            if (context.getStylePreset() != null) {
                existing.setStylePreset(context.getStylePreset());
            }
            existing.setUpdateBy(currentUsername());
            articlePublishService.update(existing);
            return existing;
        }

        // 没有记录 → 新建
        ArticlePublish record = new ArticlePublish();
        record.setArticleId(article.getId());
        record.setPlatform(context.getTargetCode());
        record.setPlatformTitle(article.getTitle());
        record.setStatus("PENDING");
        record.setContentJsonSnapshot(article.getContentJson());
        record.setStylePreset(context.getStylePreset());
        record.setIsActive("1");
        record.setCreateBy(currentUsername());
        record.setUpdateBy(currentUsername());
        articlePublishService.create(record);
        return record;
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
