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
 * 上传永久素材响应
 */
@Data
public class UploadMaterialResponse {
    /**
     * 新增的永久素材media_id
     */
    private String media_id;

    /**
     * 图片URL
     */
    private String url;

    /**
     * 错误码
     */
    private int errcode;

    /**
     * 错误信息
     */
    private String errmsg;
}
