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
package com.txwx.webchatcrm.service;

import com.txwx.webchatcrm.domain.po.TxwxGraphicInformationImagePO;
import com.txwx.webchatcrm.domain.vo.WebChatGraphicInformationImageVO;
import com.txwx.webchatcrm.domain.vo.WebChatMaterialPermanentVO;

import java.util.List;
import java.util.Map;

/**
 * @description: 微信公众号素材相关逻辑接口
 */
public interface IWebChatMaterialService {
    /**
     * @description: 获取永久素材列表
     */
    List<WebChatMaterialPermanentVO> permanentList();

    /**
     * @description: 分页获取永久素材列表
     */
    Map<String, Object> permanentListByPage(int pageNum, int pageSize);

    /**
     * @description: 获取永久素材总数
     */
    int getPermanentTotalCount();

    /**
     * @description: 上传永久素材
     */
    WebChatMaterialPermanentVO permanentAdd(WebChatMaterialPermanentVO webChatMaterialPermanent);

    /**
     * @description: 根据mediaId删除永久素材
     */
    void permanentDelete(String mediaId);

    /**
     * @description: 获取图文消息图片列表
     */
    List<TxwxGraphicInformationImagePO> GraphicInformationImageList();

    /**
     * @description: 分页获取图文消息图片列表
     */
    Map<String, Object> GraphicInformationImageListByPage(int pageNum, int pageSize);

    /**
     * @description: 获取图文消息图片总数
     */
    int getGraphicInformationImageTotalCount();

    /**
     * @description: 上传图文消息图片
     */
    WebChatGraphicInformationImageVO GraphicInformationImageAdd(WebChatGraphicInformationImageVO informationImage);

    /**
     * @description: 根据mediaId删除图文消息图片
     */
    void GraphicInformationImageDelete(String mediaId);
}
