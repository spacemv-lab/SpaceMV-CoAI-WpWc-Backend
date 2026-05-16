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
 * 永久素材列表响应
 */
@Data
public class MaterialListResponse {
    /**
     * 素材总数
     */
    private Integer total_count;

    /**
     * 本次调用获取的素材数量
     */
    private Integer item_count;

    /**
     * 素材列表
     */
    private List<MaterialItem> item;
}
