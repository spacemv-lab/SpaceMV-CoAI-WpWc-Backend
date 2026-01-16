package com.txwx.webchatcrm.service;

import com.txwx.webchatcrm.domain.po.TxwxArticlePO;
import com.txwx.webchatcrm.domain.vo.ArticleVO;

import java.util.List;

/**
 * 微信公众号文章服务接口
 *
 * @author txwx
 * @date 2025-01-13
 */
public interface IArticleService {

    /**
     * @description: 新增草稿
     */
    void addDraft(ArticleVO articleVO);

    /**
     * @description: 查询草稿列表
     */
    List<TxwxArticlePO> getDraftList(String status, String submitter, String reviewer, Integer pageNum, Integer pageSize);

    /**
     * @description: 查询草稿总数
     */
    int getDraftCount(String status, String submitter, String reviewer);

    /**
     * @description: 更新草稿
     */
    void updateDraft(Long id, ArticleVO articleVO);

    /**
     * @description: 删除草稿
     */
    void deleteDraft(Long id);

    /**
     * @description: 提交审核
     */
    void submitForReview(Long id);

    /**
     * @description: 审核草稿
     */
    void reviewDraft(Long id, String reviewResult);

    /**
     * @description: 发布草稿
     */
    void publishDraft(Long id);

    /**
     * @description: 查询已发布文章列表
     */
    List<TxwxArticlePO> getPublishedList(Integer pageNum, Integer pageSize);

    /**
     * @description: 查询已发布文章总数
     */
    int getPublishedCount();

    /**
     * @description: 删除已发布文章
     */
    void deletePublishedArticle(Long id);
}
