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
package com.txwx.webchatcrm.domain.po;

import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 微信公众号文章实体类
 *
 * @author txwx
 * @date 2025-01-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TxwxArticlePO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 微信返回的media_id
     */
    private String mediaId;

    /**
     * 微信返回的article_id(已发布文章)
     */
    private String articleId;

    /**
     * 标题
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * 摘要
     */
    private String digest;

    /**
     * 内容
     */
    private String content;

    /**
     * 封面图片素材id
     */
    private String thumbMediaId;

    /**
     * 是否打开评论，0不打开，1打开
     */
    private Integer needOpenComment;

    /**
     * 是否粉丝才可评论，0所有人可评论，1粉丝才可评论
     */
    private Integer onlyFansCanComment;

    /**
     * 文章类型
     */
    private String articleType;

    /**
     * 状态：0待提交 1待审核 2审核不通过 3审核通过待发布 4已发布
     */
    private String status;

    /**
     * 提交人
     */
    private String submitter;

    /**
     * 提交人的ID
     */
    private Long submitterId;

    /**
     * 审核人
     */
    private String reviewer;

    /**
     * 发布人
     */
    private String publisher;

    /**
     * 发布任务的id
     */
    private String publishId;

    /**
     * 消息的数据ID
     */
    private String msgDataId;

    /**
     * 删除标志(0代表存在 1代表删除)
     */
    private String delFlag;
}
