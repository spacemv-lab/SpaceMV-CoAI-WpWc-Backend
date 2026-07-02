package com.txwx.social.crm.service.content;

import com.txwx.social.crm.domain.content.ArticleTemplate;

import java.util.List;

/**
 * 文章模板 Service 接口。
 */
public interface IArticleTemplateService {

    ArticleTemplate create(ArticleTemplate template);

    void update(ArticleTemplate template);

    void deleteById(Long id);

    ArticleTemplate selectById(Long id);

    List<ArticleTemplate> selectList(ArticleTemplate query);

    List<ArticleTemplate> selectByPlatform(String platform);

    ArticleTemplate selectDefault();
}
