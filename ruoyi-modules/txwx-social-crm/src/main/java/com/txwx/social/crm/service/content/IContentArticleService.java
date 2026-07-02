package com.txwx.social.crm.service.content;

import com.txwx.social.crm.domain.content.ContentArticle;
import com.txwx.social.crm.domain.content.TagCount;

import java.util.List;

public interface IContentArticleService {

    ContentArticle create(ContentArticle article);

    ContentArticle update(Long id, ContentArticle article);

    ContentArticle selectById(Long id);

    ContentArticle selectBySlug(String slug);

    List<ContentArticle> selectList(ContentArticle query);

    List<ContentArticle> selectPublicList(ContentArticle query);

    ContentArticle selectPublicBySlug(String slug);

    List<ContentArticle> selectWendaoSiteList(ContentArticle query);

    ContentArticle selectWendaoSiteBySlug(String slug);

    ContentArticle selectWendaoSiteBySlug(String slug, boolean authenticated);

    ContentArticle selectWendaoSiteBySlug(String slug, boolean authenticated, String plan);

    List<TagCount> selectWendaoSiteTags();

    List<ContentArticle> selectWendaoSiteHotList(int limit);

    int deleteById(Long id);
}
