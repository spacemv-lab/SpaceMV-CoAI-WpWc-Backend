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
package com.txwx.webchatcrm.mapper;

import com.txwx.webchatcrm.domain.po.TxwxArticlePO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 微信公众号文章Mapper接口
 *
 * @author txwx
 * @date 2025-01-13
 */
public interface TxwxArticleMapper {

    /**
     * @description: 新增文章
     */
    int insertArticle(TxwxArticlePO article);

    /**
     * @description: 更新文章
     */
    int updateArticle(TxwxArticlePO article);

    /**
     * @description: 根据ID查询文章
     */
    TxwxArticlePO selectArticleById(@Param("id") Long id);

    /**
     * @description: 根据mediaId查询文章
     */
    TxwxArticlePO selectArticleByMediaId(@Param("mediaId") String mediaId);

    /**
     * @description: 查询草稿列表(支持状态、提交人、审核人过滤)
     */
    List<TxwxArticlePO> selectDraftList(@Param("status") String status,
                                   @Param("submitter") String submitter,
                                   @Param("reviewer") String reviewer,
                                   @Param("offset") Integer offset,
                                   @Param("limit") Integer limit);

    /**
     * @description: 查询草稿总数
     */
    int selectDraftCount(@Param("status") String status,
                        @Param("submitter") String submitter,
                        @Param("reviewer") String reviewer);

    /**
     * @description: 查询已发布文章列表
     */
    List<TxwxArticlePO> selectPublishedList(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * @description: 查询已发布文章总数
     */
    int selectPublishedCount();

    /**
     * @description: 删除文章(逻辑删除)
     */
    int deleteArticle(@Param("id") Long id);

    /**
     * @description: 根据mediaId删除文章
     */
    int deleteArticleByMediaId(@Param("mediaId") String mediaId);

    /**
     * @description: 查询发布中的文章列表
     */
    List<TxwxArticlePO> selectPublishingArticles();

    /**
     * @description: 批量更新文章状态
     */
    int batchUpdateStatus(@Param("articles") List<TxwxArticlePO> articles);
}
