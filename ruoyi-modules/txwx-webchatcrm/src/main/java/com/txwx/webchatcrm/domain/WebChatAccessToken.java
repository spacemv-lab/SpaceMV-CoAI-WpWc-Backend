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
package com.txwx.webchatcrm.domain;

import lombok.Data;

@Data
public class WebChatAccessToken {
    /**
     * @description: 获取到的凭证
     */
    private String access_token;

    /**
     * @description: 凭证有效时间，单位：秒。目前是7200秒之内的值。
     */
    private int expires_in;
}
