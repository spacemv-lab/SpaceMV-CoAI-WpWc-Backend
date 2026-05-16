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
package com.txwx.webchatcrm.domain.vo;

import lombok.Data;
import java.util.List;

/**
 * 发布状态VO
 */
@Data
public class PublishStatusVO {

    /**
     * 发布任务id
     */
    private String publishId;

    /**
     * 发布状态(0:成功,1:发布中,2:原创失败,3:常规失败,4:平台审核不通过,5:成功后用户删除所有文章,6:成功后系统封禁所有文章)
     */
    private Integer publishStatus;

    /**
     * 发布状态描述
     */
    private String publishStatusDesc;

    /**
     * 成功时的图文article_id
     */
    private String articleId;

    /**
     * 文章数量
     */
    private Integer count;

    /**
     * 文章详情列表
     */
    private List<ArticleItem> articleItems;

    /**
     * 失败文章编号
     */
    private List<Integer> failIdx;

    @Data
    public static class ArticleItem {

        /**
         * 文章对应的编号
         */
        private Integer idx;

        /**
         * 图文的永久链接
         */
        private String articleUrl;
    }
}
