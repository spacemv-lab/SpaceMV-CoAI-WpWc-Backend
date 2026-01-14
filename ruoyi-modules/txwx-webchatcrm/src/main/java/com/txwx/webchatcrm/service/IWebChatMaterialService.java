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
