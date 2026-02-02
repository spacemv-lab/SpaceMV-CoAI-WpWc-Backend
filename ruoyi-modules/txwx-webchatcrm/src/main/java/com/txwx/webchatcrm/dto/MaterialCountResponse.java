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
 * 永久素材数量响应
 */
@Data
public class MaterialCountResponse {
    /**
     * 错误码
     */
    private Integer errcode;

    /**
     * 错误描述
     */
    private String errmsg;

    /**
     * 语音总数量
     */
    private Integer voice_count;

    /**
     * 视频总数量
     */
    private Integer video_count;

    /**
     * 图片总数量
     */
    private Integer image_count;

    /**
     * 图文总数量
     */
    private Integer news_count;
}
