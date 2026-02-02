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
import org.springframework.web.multipart.MultipartFile;

/**
 * @description: 永久素材,用于前后端交互
 */
@Data
public class WebChatMaterialPermanentVO {
    String media_id;

    String name;

    String update_time;

    String url;

    MultipartFile file;
}
