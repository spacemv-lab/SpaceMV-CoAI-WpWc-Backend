package com.txwx.social.crm.mapper.content;

import com.txwx.social.crm.domain.content.ContentArticle;
import com.txwx.social.crm.domain.content.TagCount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 内容中心文章Mapper。
 */
public interface ContentArticleMapper {

    int insert(ContentArticle article);

    int update(ContentArticle article);

    ContentArticle selectById(@Param("id") Long id);

    ContentArticle selectBySlug(@Param("slug") String slug);

    List<ContentArticle> selectList(ContentArticle query);

    List<ContentArticle> selectPublicList(ContentArticle query);

    ContentArticle selectPublicBySlug(@Param("slug") String slug);

    List<ContentArticle> selectWendaoSiteList(ContentArticle query);

    ContentArticle selectWendaoSiteBySlug(@Param("slug") String slug);

    List<TagCount> selectWendaoSiteTags();

    List<ContentArticle> selectWendaoSiteHotList(@Param("limit") int limit);

    int deleteById(@Param("id") Long id, @Param("updateBy") String updateBy);
}
