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
package com.txwx.webchatcrm.dto;

import lombok.Data;
import java.util.List;

/**
 * 查询发布状态响应
 */
@Data
public class GetPublishStatusResponse {

    /**
     * 发布任务id
     */
    private String publish_id;

    /**
     * 发布状态(0:成功,1:发布中,2:原创失败,3:常规失败,4:平台审核不通过,5:成功后用户删除所有文章,6:成功后系统封禁所有文章)
     */
    private Integer publish_status;

    /**
     * 成功时的图文article_id
     */
    private String article_id;

    /**
     * 文章详情
     */
    private ArticleDetail article_detail;

    /**
     * 失败文章编号
     */
    private List<Integer> fail_idx;

    /**
     * 错误码
     */
    private Integer errcode;

    /**
     * 错误信息
     */
    private String errmsg;

    @Data
    public static class ArticleDetail {

        /**
         * 文章数量
         */
        private Integer count;

        /**
         * 文章详情列表
         */
        private List<ArticleItem> item;
    }

    @Data
    public static class ArticleItem {

        /**
         * 文章对应的编号
         */
        private Integer idx;

        /**
         * 图文的永久链接
         */
        private String article_url;
    }
}
