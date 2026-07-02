package com.txwx.social.crm.service.content.impl;

import com.txwx.social.crm.domain.content.ArticleTemplate;
import com.txwx.social.crm.mapper.content.ArticleTemplateMapper;
import com.txwx.social.crm.service.content.IArticleTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文章模板 Service 实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleTemplateServiceImpl implements IArticleTemplateService {

    private final ArticleTemplateMapper articleTemplateMapper;

    @Override
    public ArticleTemplate create(ArticleTemplate template) {
        articleTemplateMapper.insert(template);
        return template;
    }

    @Override
    public void update(ArticleTemplate template) {
        articleTemplateMapper.update(template);
    }

    @Override
    public void deleteById(Long id) {
        articleTemplateMapper.deleteById(id);
    }

    @Override
    public ArticleTemplate selectById(Long id) {
        return articleTemplateMapper.selectById(id);
    }

    @Override
    public List<ArticleTemplate> selectList(ArticleTemplate query) {
        return articleTemplateMapper.selectList(query);
    }

    @Override
    public List<ArticleTemplate> selectByPlatform(String platform) {
        return articleTemplateMapper.selectByPlatform(platform);
    }

    @Override
    public ArticleTemplate selectDefault() {
        return articleTemplateMapper.selectDefault();
    }
}
