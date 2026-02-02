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
 * 获取草稿列表响应DTO
 */
@Data
public class GetDraftListResponse {

    /**
     * 错误码
     */
    private Integer errcode;

    /**
     * 错误信息
     */
    private String errmsg;

    /**
     * 草稿素材的总数
     */
    private Integer total_count;

    /**
     * 本次调用获取的素材的数量
     */
    private Integer item_count;

    /**
     * 素材列表
     */
    private List<DraftItem> item;

    @Data
    public static class DraftItem {

        /**
         * 图文消息的id
         */
        private String media_id;

        /**
         * 图文消息内容
         */
        private Content content;

        /**
         * 图文消息更新时间
         */
        private Long update_time;
    }

    @Data
    public static class Content {

        /**
         * 图文内容列表
         */
        private List<NewsItem> news_item;
    }

    @Data
    public static class NewsItem {

        /**
         * 文章类型，分别有图文消息（news）、图片消息（newspic），不填默认为图文消息（news）
         */
        private String article_type;

        /**
         * 标题
         */
        private String title;

        /**
         * 作者
         */
        private String author;

        /**
         * 图文消息的摘要，仅有单图文消息才有摘要，多图文此处为空
         */
        private String digest;

        /**
         * 图文消息的具体内容，支持HTML标签
         */
        private String content;

        /**
         * 图文消息的原文地址，即点击"阅读原文"后的URL
         */
        private String content_source_url;

        /**
         * 是否显示封面，0为false，即不显示，1为true，即显示
         */
        private String show_cover_pic;

        /**
         * 图文消息的封面图片素材id
         */
        private String thumb_media_id;

        /**
         * 图文消息的封面图片URL
         */
        private String thumb_url;

        /**
         * 是否打开评论，0不打开(默认)，1打开
         */
        private Integer need_open_comment;

        /**
         * 是否粉丝才可评论，0所有人可评论(默认)，1粉丝才可评论
         */
        private Integer only_fans_can_comment;

        /**
         * 草稿的临时链接
         */
        private String url;
    }
}
