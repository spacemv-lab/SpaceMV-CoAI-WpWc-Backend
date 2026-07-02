package com.txwx.social.crm.service.content;

import com.txwx.social.crm.domain.content.ArticlePublish;

import java.util.List;

/**
 * 文章平台发布记录 Service 接口。
 * 管理各平台发布状态、快照、重试。
 */
public interface IArticlePublishService {

    /**
     * 创建发布记录（首次发布时调用）。
     */
    ArticlePublish create(ArticlePublish record);

    /**
     * 更新发布记录（状态变更/重试时调用）。
     */
    void update(ArticlePublish record);

    /**
     * 根据 ID 查询。
     */
    ArticlePublish selectById(Long id);

    /**
     * 查询某篇文章在指定平台的发布记录。
     */
    ArticlePublish selectByArticleAndPlatform(Long articleId, String platform);

    /**
     * 查询某篇文章在所有平台的发布记录。
     */
    List<ArticlePublish> selectByArticleId(Long articleId);

    /**
     * 查询某篇文章已成功发布的平台。
     */
    List<ArticlePublish> selectSuccessByArticleId(Long articleId);

    /**
     * 分页查询。
     */
    List<ArticlePublish> selectList(ArticlePublish query);

    /**
     * 标记某平台发布记录为已删除。
     */
    void markDeleted(Long id, String updateBy);

    /**
     * 标记某篇文章的所有平台记录为已删除。
     */
    void markDeletedByArticleId(Long articleId, String updateBy);

    /**
     * 递增重试计数。
     */
    void incrementRetryCount(Long id);
}
