/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.mapper;

import com.txwx.social.crm.domain.po.TxwxGraphicInformationImagePO;
import com.txwx.social.crm.domain.vo.WebChatGraphicInformationImageVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 微信公众号图文消息图片Mapper接口
 *
 * @author txwx
 * @date 2025-12-06
 */
public interface TxwxGraphicInformationImageMapper {

    /**
     * @description: 从数据库中查询图文消息图片列表
     */
    List<TxwxGraphicInformationImagePO> selectGraphicInformationImageList(@Param("accountIds") List<Long> accountIds);

    /**
     * @description: 分页查询图文消息图片列表
     */
    List<WebChatGraphicInformationImageVO> selectGraphicInformationImageListByPage(@Param("offset") int offset, @Param("limit") int limit,
                                                                                   @Param("accountIds") List<Long> accountIds);

    /**
     * @description: 查询图文消息图片总数
     */
    int selectGraphicInformationImageTotalCount(@Param("accountIds") List<Long> accountIds);

    /**
     * @description: 向数据库中插入图文消息图片
     */
    int insertGraphicInformationImage(TxwxGraphicInformationImagePO informationImage);

    /**
     * @description: 根据mediaId删除数据库中的图文消息图片
     */
    int deleteGraphicInformationImage(@Param("mediaId") String mediaId);
}
