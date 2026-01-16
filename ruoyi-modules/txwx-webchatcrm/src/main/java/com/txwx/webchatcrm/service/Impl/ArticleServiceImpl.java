package com.txwx.webchatcrm.service.Impl;

import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.webchatcrm.domain.po.TxwxArticlePO;
import com.txwx.webchatcrm.domain.vo.ArticleVO;
import com.txwx.webchatcrm.dto.*;
import com.txwx.webchatcrm.enums.ArticleStatusEnum;
import com.txwx.webchatcrm.mapper.TxwxArticleMapper;
import com.txwx.webchatcrm.service.IArticleService;
import com.txwx.webchatcrm.util.WebChatUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 微信公众号文章服务实现类
 *
 * @author txwx
 * @date 2025-01-13
 */
@Service
public class ArticleServiceImpl implements IArticleService {

    @Autowired
    private TxwxArticleMapper txwxArticleMapper;

    @Value("${weixin.appId}")
    private String appId;

    @Value("${weixin.secret}")
    private String secret;

    @Override
    @Transactional
    public void addDraft(ArticleVO articleVO) {
        try {
            // 1. 调用微信API新增草稿
            String accessToken = WebChatUtil.getAccessToken(appId, secret);

            ArticleItemDTO articleItem = new ArticleItemDTO();
            articleItem.setArticle_type(articleVO.getArticleType() != null ? articleVO.getArticleType() : "news");
            articleItem.setTitle(articleVO.getTitle());
            articleItem.setAuthor(articleVO.getAuthor());
            articleItem.setDigest(articleVO.getDigest());
            articleItem.setContent(articleVO.getContent());
            articleItem.setThumb_media_id(articleVO.getThumbMediaId());
            articleItem.setNeed_open_comment(articleVO.getNeedOpenComment() != null ? articleVO.getNeedOpenComment() : 0);
            articleItem.setOnly_fans_can_comment(articleVO.getOnlyFansCanComment() != null ? articleVO.getOnlyFansCanComment() : 0);

            ArticleDTO articleDTO = new ArticleDTO();
            articleDTO.setArticles(new ArrayList<>());
            articleDTO.getArticles().add(articleItem);

            AddDraftResponse response = WebChatUtil.addDraft(accessToken, articleDTO);

            /**
             * @description: 以当前登录用户作为提交人
             */
            String username = SecurityUtils.getLoginUser().getUsername();
            //String username = "admin";

            // 2. 保存到数据库
            TxwxArticlePO article = new TxwxArticlePO();
            article.setMediaId(response.getMedia_id());
            article.setTitle(articleVO.getTitle());
            article.setAuthor(articleVO.getAuthor());
            article.setDigest(articleVO.getDigest());
            article.setContent(articleVO.getContent());
            article.setThumbMediaId(articleVO.getThumbMediaId());
            article.setNeedOpenComment(articleVO.getNeedOpenComment() != null ? articleVO.getNeedOpenComment() : 0);
            article.setOnlyFansCanComment(articleVO.getOnlyFansCanComment() != null ? articleVO.getOnlyFansCanComment() : 0);
            article.setArticleType(articleVO.getArticleType() != null ? articleVO.getArticleType() : "news");
            article.setStatus(ArticleStatusEnum.PENDING_SUBMIT.getCode());
            article.setSubmitter(username);
            article.setCreateTime(new Date());
            article.setUpdateTime(new Date());

            txwxArticleMapper.insertArticle(article);
        } catch (Exception e) {
            throw new RuntimeException("新增草稿失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TxwxArticlePO> getDraftList(String status, String submitter, String reviewer, Integer pageNum, Integer pageSize) {
        try {
            // 计算分页偏移量
            int offset = (pageNum - 1) * pageSize;
            return txwxArticleMapper.selectDraftList(status, submitter, reviewer, offset, pageSize);
        } catch (Exception e) {
            throw new RuntimeException("查询草稿列表失败: " + e.getMessage(), e);
        }
    }

    @Override
    public int getDraftCount(String status, String submitter, String reviewer) {
        try {
            return txwxArticleMapper.selectDraftCount(status, submitter, reviewer);
        } catch (Exception e) {
            throw new RuntimeException("查询草稿总数失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void updateDraft(Long id, ArticleVO articleVO) {
        try {
            // 1. 查询原文章
            TxwxArticlePO existingArticle = txwxArticleMapper.selectArticleById(id);
            if (existingArticle == null) {
                throw new RuntimeException("草稿不存在");
            }

            // 2. 更新微信草稿
            String accessToken = WebChatUtil.getAccessToken(appId, secret);

            ArticleItemDTO articleItem = new ArticleItemDTO();
            articleItem.setArticle_type(articleVO.getArticleType() != null ? articleVO.getArticleType() : "news");
            articleItem.setTitle(articleVO.getTitle());
            articleItem.setAuthor(articleVO.getAuthor());
            articleItem.setDigest(articleVO.getDigest());
            articleItem.setContent(articleVO.getContent());
            articleItem.setThumb_media_id(articleVO.getThumbMediaId());
            articleItem.setNeed_open_comment(articleVO.getNeedOpenComment() != null ? articleVO.getNeedOpenComment() : 0);
            articleItem.setOnly_fans_can_comment(articleVO.getOnlyFansCanComment() != null ? articleVO.getOnlyFansCanComment() : 0);

            ArticleUpdateDTO articleDTO = new ArticleUpdateDTO();
            articleDTO.setMedia_id(existingArticle.getMediaId());
            articleDTO.setIndex(articleVO.getIndex());
            articleDTO.setArticles(articleItem);

            WebChatUtil.updateDraft(accessToken, articleDTO);

            // 3. 更新数据库
            existingArticle.setTitle(articleVO.getTitle());
            existingArticle.setAuthor(articleVO.getAuthor());
            existingArticle.setDigest(articleVO.getDigest());
            existingArticle.setContent(articleVO.getContent());
            existingArticle.setThumbMediaId(articleVO.getThumbMediaId());
            existingArticle.setNeedOpenComment(articleVO.getNeedOpenComment() != null ? articleVO.getNeedOpenComment() : 0);
            existingArticle.setOnlyFansCanComment(articleVO.getOnlyFansCanComment() != null ? articleVO.getOnlyFansCanComment() : 0);
            existingArticle.setArticleType(articleVO.getArticleType() != null ? articleVO.getArticleType() : "news");
            existingArticle.setUpdateTime(new Date());

            txwxArticleMapper.updateArticle(existingArticle);
        } catch (Exception e) {
            throw new RuntimeException("更新草稿失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteDraft(Long id) {
        try {
            // 1. 查询文章
            TxwxArticlePO article = txwxArticleMapper.selectArticleById(id);
            if (article == null) {
                throw new RuntimeException("草稿不存在");
            }

            // 2. 调用微信API删除草稿
            if (StringUtils.isNotEmpty(article.getMediaId())) {
                String accessToken = WebChatUtil.getAccessToken(appId, secret);
                WebChatUtil.deleteDraft(accessToken, article.getMediaId());
            }

            // 3. 删除数据库记录
            txwxArticleMapper.deleteArticle(id);
        } catch (Exception e) {
            throw new RuntimeException("删除草稿失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void submitForReview(Long id) {
        try {
            // 1. 查询文章
            TxwxArticlePO article = txwxArticleMapper.selectArticleById(id);
            if (article == null) {
                throw new RuntimeException("草稿不存在");
            }

            // 2. 更新状态为待审核
            article.setStatus(ArticleStatusEnum.PENDING_REVIEW.getCode());
            article.setUpdateTime(new Date());

            txwxArticleMapper.updateArticle(article);
        } catch (Exception e) {
            throw new RuntimeException("提交审核失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void reviewDraft(Long id, String reviewResult) {
        try {
            // 1. 查询文章
            TxwxArticlePO article = txwxArticleMapper.selectArticleById(id);
            if (article == null) {
                throw new RuntimeException("草稿不存在");
            }

            /**
             * @description: 以当前登录用户作为审核人
             */
            String username = SecurityUtils.getLoginUser().getUsername();
            //String username = "admin";

            // 2. 更新状态
            if ("pass".equalsIgnoreCase(reviewResult)) {
                article.setStatus(ArticleStatusEnum.REVIEW_APPROVED.getCode());
            } else {
                article.setStatus(ArticleStatusEnum.REVIEW_REJECTED.getCode());
            }
            article.setReviewer(username);
            article.setUpdateTime(new Date());

            txwxArticleMapper.updateArticle(article);
        } catch (Exception e) {
            throw new RuntimeException("审核草稿失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void publishDraft(Long id) {
        try {
            // 1. 查询文章
            TxwxArticlePO article = txwxArticleMapper.selectArticleById(id);
            if (article == null) {
                throw new RuntimeException("草稿不存在");
            }

            // 2. 调用微信API发布
            String accessToken = WebChatUtil.getAccessToken(appId, secret);
            PublishDraftResponse response = WebChatUtil.publishDraft(accessToken, article.getMediaId());

            /**
             * @description: 以当前登录用户作为发布人
             */
            String username = SecurityUtils.getLoginUser().getUsername();
            //String username = "admin";

            // 3. 更新数据库
            article.setPublishId(response.getPublish_id());
            article.setMsgDataId(response.getMsg_data_id());
            article.setStatus(ArticleStatusEnum.PUBLISHED.getCode());
            article.setPublisher(username);
            article.setUpdateTime(new Date());

            txwxArticleMapper.updateArticle(article);
        } catch (Exception e) {
            throw new RuntimeException("发布草稿失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TxwxArticlePO> getPublishedList(Integer pageNum, Integer pageSize) {
        try {
            // 计算分页偏移量
            int offset = (pageNum - 1) * pageSize;
            return txwxArticleMapper.selectPublishedList(offset, pageSize);
        } catch (Exception e) {
            throw new RuntimeException("查询已发布文章列表失败: " + e.getMessage(), e);
        }
    }

    @Override
    public int getPublishedCount() {
        try {
            return txwxArticleMapper.selectPublishedCount();
        } catch (Exception e) {
            throw new RuntimeException("查询已发布文章总数失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deletePublishedArticle(Long id) {
        try {
            // 1. 查询文章
            TxwxArticlePO article = txwxArticleMapper.selectArticleById(id);
            if (article == null) {
                throw new RuntimeException("文章不存在");
            }

            // 2. 调用微信API删除已发布文章
            if (StringUtils.isNotEmpty(article.getArticleId())) {
                String accessToken = WebChatUtil.getAccessToken(appId, secret);
                WebChatUtil.deletePublishedArticle(accessToken, article.getArticleId());
            }

            // 3. 删除数据库记录
            txwxArticleMapper.deleteArticle(id);
        } catch (Exception e) {
            throw new RuntimeException("删除已发布文章失败: " + e.getMessage(), e);
        }
    }
}
