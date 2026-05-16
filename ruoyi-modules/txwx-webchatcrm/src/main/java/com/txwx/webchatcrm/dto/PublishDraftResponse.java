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
 * 发布草稿响应
 */
@Data
public class PublishDraftResponse {
    /**
     * 错误码
     */
    private Integer errcode;

    /**
     * 错误信息
     */
    private String errmsg;

    /**
     * 发布任务的id
     */
    private String publish_id;

    /**
     * 消息的数据ID
     */
    private String msg_data_id;
}
