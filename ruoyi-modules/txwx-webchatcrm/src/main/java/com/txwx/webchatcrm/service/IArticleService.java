/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the MIT License;
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 
 * the copywrite is belongs to  SpaceMV  team
 */
package com.txwx.webchatcrm.service;

import com.txwx.webchatcrm.domain.po.TxwxArticlePO;
import com.txwx.webchatcrm.domain.vo.ArticleVO;
import com.txwx.webchatcrm.domain.vo.ArticleDetailVO;
import com.txwx.webchatcrm.domain.vo.PublishStatusVO;
import com.txwx.webchatcrm.domain.vo.PublishedArticleListVO;
import com.txwx.webchatcrm.domain.vo.DraftListVO;

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
     * @description: 查询草稿详情
     */
    ArticleDetailVO getDraftDetail(Long id);

    /**
     * @description: 获取草稿列表（从微信官方查询）
     */
    DraftListVO getDraftListFromTencent(Integer pageNum, Integer pageSize, Integer noContent);

    /**
     * @description: 查询发布状态
     */
    PublishStatusVO getPublishStatus(Long id);

    /**
     * @description: 获取已发布的消息列表（从微信官方查询）
     */
    PublishedArticleListVO getPublishedListFromTencent(Integer pageNum, Integer pageSize, Integer noContent);

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

    /**
     * @description: 定时任务：更新发布中文章的状态
     */
    void updatePublishingArticleStatus();
}
