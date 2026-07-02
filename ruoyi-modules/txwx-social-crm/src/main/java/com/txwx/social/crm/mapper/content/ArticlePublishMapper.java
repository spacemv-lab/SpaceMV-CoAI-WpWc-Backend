package com.txwx.social.crm.mapper.content;

import com.txwx.social.crm.domain.content.ArticlePublish;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章平台发布记录 Mapper。
 * 每条记录 = 一篇文章在某平台上的发布状态。
 */
public interface ArticlePublishMapper {

    int insert(ArticlePublish record);

    int update(ArticlePublish record);

    ArticlePublish selectById(@Param("id") Long id);

    /**
     * 查询某篇文章在指定平台的发布记录。
     */
    ArticlePublish selectByArticleAndPlatform(
            @Param("articleId") Long articleId,
            @Param("platform") String platform);

    /**
     * 查询某篇文章在所有平台的发布记录列表。
     */
    List<ArticlePublish> selectByArticleId(@Param("articleId") Long articleId);

    /**
     * 查询某篇文章已成功发布的平台列表。
     */
    List<ArticlePublish> selectSuccessByArticleId(@Param("articleId") Long articleId);

    /**
     * 分页查询发布记录列表（支持按平台、状态筛选）。
     */
    List<ArticlePublish> selectList(ArticlePublish query);

    /**
     * 标记某平台发布记录为已删除（is_active = '0'）。
     */
    int markDeleted(@Param("id") Long id, @Param("updateBy") String updateBy);

    /**
     * 逻辑删除某篇文章的所有平台发布记录。
     */
    int markDeletedByArticleId(@Param("articleId") Long articleId, @Param("updateBy") String updateBy);

    /**
     * 增加重试计数。
     */
    int incrementRetryCount(@Param("id") Long id);
}
