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
 * 文章VO
 */
@Data
public class ArticleVO {

    /**
     * 文章ID
     */
    private Long id;

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
     * 要更新的文章在图文消息中的位置（多图文消息时，此字段才有意义），第一篇为0
     */
    private Integer index;
}
