package com.txwx.social.crm.service;

import com.txwx.social.crm.domain.po.TxwxProductChannelPO;

import java.util.List;
import java.util.Map;

/**
 * 产品渠道关联服务接口
 *
 * @author txwx
 * @date 2026-04-03
 */
public interface IProductChannelService {

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
    TxwxProductChannelPO selectProductChannelById(Long id);

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
    int deleteProductChannelByChannelIds(List<Long> ids);

    int deleteProductChannelByProductIds(List<Long> ids);

    /**
     * 根据产品ID查询渠道ID列表
     *
     * @param productId 产品ID
     * @return 渠道ID列表
     */
    List<Long> selectChannelIdsByProductId(Long productId);

    Map<Long, List<TxwxProductChannelPO>> selectChannelIdsByProductIds(List<Long> productIds);

    List<TxwxProductChannelPO> selectChannelByProductIds(List<Long> productIds);

    /**
     * 根据渠道ID查询产品ID列表
     *
     * @param channelId 渠道ID
     * @return 产品ID列表
     */
    List<Long> selectProductIdsByChannelId(Long channelId);
}
