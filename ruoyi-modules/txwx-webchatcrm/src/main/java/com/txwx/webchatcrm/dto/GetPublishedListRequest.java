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
 * 获取已发布消息列表请求
 */
@Data
public class GetPublishedListRequest {

    /**
     * 从全部素材的该偏移位置开始返回，0表示从第一个素材返回
     */
    private Integer offset;

    /**
     * 返回素材的数量，取值在1到20之间
     */
    private Integer count;

    /**
     * 1表示不返回content字段，0表示正常返回，默认为0
     */
    private Integer no_content;
}
