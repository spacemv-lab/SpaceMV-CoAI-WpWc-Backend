/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.service;

import com.txwx.social.crm.domain.po.TxwxArticlePO;
import com.txwx.social.crm.domain.vo.*;

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
    List<TxwxArticlePO> getDraftList(ArticleDraftReqVO reqVO);

    /**
     * @description: 查询草稿总数
     */
    int getDraftCount(ArticleDraftReqVO reqVO);

    /**
     * @description: 查询草稿详情
     */
    ArticleDetailVO getDraftDetail(Long id);

    /**
     * @description: 获取草稿列表（从微信官方查询）
     */
    DraftListVO getDraftListFromTencent(Integer pageNum, Integer pageSize, Integer noContent, List<Long> accountIds);

    /**
     * @description: 查询发布状态
     */
    PublishStatusVO getPublishStatus(Long id);

    /**
     * @description: 获取已发布的消息列表（从微信官方查询）
     */
    PublishedArticleListVO getPublishedListFromTencent(Integer pageNum, Integer pageSize, Integer noContent,List<Long> accountIds);

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
    List<TxwxArticlePO> getPublishedList(Integer pageNum, Integer pageSize, List<Long> accountIds);

    /**
     * @description: 查询已发布文章总数
     */
    int getPublishedCount(List<Long> accountIds);

    /**
     * @description: 删除已发布文章
     */
    void deletePublishedArticle(Long id);

    /**
     * @description: 定时任务：更新发布中文章的状态
     */
    void updatePublishingArticleStatus();
}
