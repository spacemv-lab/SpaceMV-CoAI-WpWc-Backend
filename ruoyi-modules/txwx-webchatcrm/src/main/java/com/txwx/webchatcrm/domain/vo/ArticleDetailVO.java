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

/**
 * 草稿详情VO
 */
@Data
public class ArticleDetailVO {

    /**
     * 文章类型
     */
    private String articleType;

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
     * 图文消息的原文地址
     */
    private String contentSourceUrl;

    /**
     * 图文消息封面序号
     */
    private Integer showCoverPic;

    /**
     * 图文消息的封面图片素材id
     */
    private String thumbMediaId;

    /**
     * 图文消息封面链接
     */
    private String thumbUrl;

    /**
     * 草稿的临时链接
     */
    private String url;

    /**
     * 是否打开评论，0不打开(默认)，1打开
     */
    private Integer needOpenComment;

    /**
     * 是否粉丝才可评论，0所有人可评论(默认)，1粉丝才可评论
     */
    private Integer onlyFansCanComment;
}
