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
 * 文章DTO(用于调用微信API)
 */
@Data
public class ArticleDTO {

    /**
     * media_id(更新草稿时需要)
     */
    private String media_id;

    /**
     * 图文素材集合
     */
    private List<ArticleItemDTO> articles;
}
