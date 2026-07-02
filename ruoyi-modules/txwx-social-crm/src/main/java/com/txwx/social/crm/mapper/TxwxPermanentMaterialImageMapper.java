/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.mapper;

import com.txwx.social.crm.domain.po.TxwxPermanentMaterialImagePO;
import com.txwx.social.crm.domain.vo.MediaPlatformVO;
import com.txwx.social.crm.domain.vo.WebChatMaterialPermanentVO;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 微信公众号永久素材Mapper接口
 *
 * @author txwx
 * @date 2025-12-06
 */
public interface TxwxPermanentMaterialImageMapper {

    /**
     * @description: 分页查询图文消息图片列表
     */
    List<WebChatMaterialPermanentVO> selectPermanentMaterialImageListByPage(@Param("offset") int offset, @Param("limit") int limit,
                                                                            @Param("accountIds") List<Long> accountIds);

    /**
     * @description: 查询图文消息图片总数
     */
    int selectPermanentMaterialImageTotalCount(@Param("accountIds") List<Long> accountIds);

    /**
     * @description: 向数据库中插入图文消息图片
     */
    int insertPermanentMaterialImage(TxwxPermanentMaterialImagePO informationImage);

    /**
     * @description: 根据mediaId删除数据库中的图文消息图片
     */
    int deletePermanentMaterialImage(@Param("mediaId") String mediaId);


    TxwxPermanentMaterialImagePO selectPermanentMaterialImageByMediaId(@Param("mediaId") String mediaId);

    TxwxPermanentMaterialImagePO selectPermanentMaterialImageBySourceUrl(@Param("sourceUrl") String sourceUrl, @Param("accountId") Long accountId);
}
