/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.mapper;

import com.txwx.social.crm.domain.po.TxwxProductChannelPO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 产品渠道关联Mapper
 *
 * @author txwx
 * @date 2026-04-03
 */
@Mapper
public interface TxwxProductChannelMapper {

    /**
     * 查询产品渠道关联列表
     *
     * @param productChannel 产品渠道关联参数
     * @return 产品渠道关联列表
     */
    List<TxwxProductChannelPO> selectProductChannelList(TxwxProductChannelPO productChannel);

    /**
     * 查询产品渠道关联详情
     *
     * @param id 主键ID
     * @return 产品渠道关联详情
     */
    TxwxProductChannelPO selectProductChannelById(@Param("id") Long id);

    /**
     * 新增产品渠道关联
     *
     * @param productChannel 产品渠道关联信息
     * @return 结果
     */
    int insertProductChannel(TxwxProductChannelPO productChannel);

    /**
     * 修改产品渠道关联
     *
     * @param productChannel 产品渠道关联信息
     * @return 结果
     */
    int updateProductChannel(TxwxProductChannelPO productChannel);

    /**
     * 删除产品渠道关联
     *
     * @param ids 主键ID列表
     * @return 结果
     */
    int deleteProductChannelByIds(@Param("ids") List<Long> ids);

    /**
     * 删除产品渠道关联
     *
     * @param cids 主键ID列表
     * @return 结果
     */
    int deleteProductChannelBycIds(@Param("cids") List<Long> cids);

    /**
     * 删除产品渠道关联
     *
     * @param pids 主键ID列表
     * @return 结果
     */
    int deleteProductChannelBypIds(@Param("pids") List<Long> pids);

    /**
     * 根据产品ID查询渠道ID列表
     *
     * @param productId 产品ID
     * @return 渠道ID列表
     */
    List<Long> selectChannelIdsByProductId(@Param("productId") Long productId);

    /**
     * 根据渠道ID查询产品ID列表
     *
     * @param channelId 渠道ID
     * @return 产品ID列表
     */
    List<Long> selectProductIdsByChannelId(@Param("channelId") Long channelId);


   List<TxwxProductChannelPO> selectChannelIdsByProductIds(@Param("productIds") List<Long> productIds);
}
