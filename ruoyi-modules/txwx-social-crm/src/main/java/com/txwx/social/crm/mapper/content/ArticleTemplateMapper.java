package com.txwx.social.crm.mapper.content;

import com.txwx.social.crm.domain.content.ArticleTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章模板 Mapper。
 */
public interface ArticleTemplateMapper {

    int insert(ArticleTemplate template);

    int update(ArticleTemplate template);

    int deleteById(@Param("id") Long id);

    ArticleTemplate selectById(@Param("id") Long id);

    List<ArticleTemplate> selectList(ArticleTemplate query);

    List<ArticleTemplate> selectByPlatform(@Param("platform") String platform);

    ArticleTemplate selectDefault();
}
