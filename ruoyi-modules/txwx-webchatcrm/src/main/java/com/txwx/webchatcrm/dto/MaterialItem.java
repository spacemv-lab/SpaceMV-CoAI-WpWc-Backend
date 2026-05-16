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

/**
 * 永久素材项
 */
@Data
public class MaterialItem {
    /**
     * 消息
     */
    private String media_id;

    /**
     * 图片的名字
     */
    private String name;

    /**
     * 更新日期
     */
    private String update_time;

    /**
     * 图片的URL
     */
    private String url;
}
