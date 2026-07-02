package com.txwx.social.crm.service.content.impl;

import com.ruoyi.common.core.exception.ServiceException;
import com.txwx.social.crm.domain.content.ArticlePublish;
import com.txwx.social.crm.mapper.content.ArticlePublishMapper;
import com.txwx.social.crm.service.content.IArticlePublishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文章平台发布记录 Service 实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticlePublishServiceImpl implements IArticlePublishService {

    private final ArticlePublishMapper articlePublishMapper;

    @Override
    public ArticlePublish create(ArticlePublish record) {
        // 确保同一篇文章同一个平台不重复创建
        ArticlePublish existing = articlePublishMapper.selectByArticleAndPlatform(
                record.getArticleId(), record.getPlatform());
        if (existing != null) {
            throw new ServiceException(
                    String.format("文章 %d 在平台 %s 已有发布记录 (id=%d)，请使用更新操作",
                            record.getArticleId(), record.getPlatform(), existing.getId()));
        }
        articlePublishMapper.insert(record);
        return record;
    }

    @Override
    public void update(ArticlePublish record) {
        if (record.getId() == null) {
            if (record.getArticleId() == null || record.getPlatform() == null) {
                throw new ServiceException("更新发布记录时 articleId 和 platform 不能为空");
            }
            // 通过 articleId + platform 更新
            ArticlePublish existing = articlePublishMapper.selectByArticleAndPlatform(
                    record.getArticleId(), record.getPlatform());
            if (existing == null) {
                // 不存在则创建（首次重试时可能旧记录已被逻辑删除）
                this.create(record);
                return;
            }
            record.setId(existing.getId());
        }
        articlePublishMapper.update(record);
    }

    @Override
    public ArticlePublish selectById(Long id) {
        return articlePublishMapper.selectById(id);
    }

    @Override
    public ArticlePublish selectByArticleAndPlatform(Long articleId, String platform) {
        return articlePublishMapper.selectByArticleAndPlatform(articleId, platform);
    }

    @Override
    public List<ArticlePublish> selectByArticleId(Long articleId) {
        return articlePublishMapper.selectByArticleId(articleId);
    }

    @Override
    public List<ArticlePublish> selectSuccessByArticleId(Long articleId) {
        return articlePublishMapper.selectSuccessByArticleId(articleId);
    }

    @Override
    public List<ArticlePublish> selectList(ArticlePublish query) {
        return articlePublishMapper.selectList(query);
    }

    @Override
    public void markDeleted(Long id, String updateBy) {
        articlePublishMapper.markDeleted(id, updateBy);
    }

    @Override
    public void markDeletedByArticleId(Long articleId, String updateBy) {
        articlePublishMapper.markDeletedByArticleId(articleId, updateBy);
    }

    @Override
    public void incrementRetryCount(Long id) {
        articlePublishMapper.incrementRetryCount(id);
    }
}
