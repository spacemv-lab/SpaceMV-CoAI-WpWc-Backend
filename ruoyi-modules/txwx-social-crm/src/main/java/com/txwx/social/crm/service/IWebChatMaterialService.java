/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.service;

import com.txwx.social.crm.domain.po.TxwxGraphicInformationImagePO;
import com.txwx.social.crm.domain.vo.WebChatGraphicInformationImageVO;
import com.txwx.social.crm.domain.vo.WebChatMaterialPermanentVO;

import java.util.List;
import java.util.Map;

/**
 * @description: 微信公众号素材相关逻辑接口
 */
public interface IWebChatMaterialService {
    /**
     * @description: 获取永久素材列表
     */
    List<WebChatMaterialPermanentVO> permanentList(List<Long> accountIds);

    /**
     * @description: 分页获取永久素材列表
     */
    Map<String, Object> permanentListByPage(int pageNum, int pageSize, List<Long> accountIds);

    /**
     * @description: 获取永久素材总数
     */
    int getPermanentTotalCount(List<Long> accountIds);

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
    List<TxwxGraphicInformationImagePO> GraphicInformationImageList(List<Long> accountIds);

    /**
     * @description: 分页获取图文消息图片列表
     */
    Map<String, Object> GraphicInformationImageListByPage(int pageNum, int pageSize, List<Long> accountIds);

    /**
     * @description: 获取图文消息图片总数
     */
    int getGraphicInformationImageTotalCount(List<Long> accountIds);

    /**
     * @description: 上传图文消息图片
     */
    WebChatGraphicInformationImageVO GraphicInformationImageAdd(WebChatGraphicInformationImageVO informationImage);

    /**
     * @description: 根据mediaId删除图文消息图片
     */
    void GraphicInformationImageDelete(String mediaId);
}
