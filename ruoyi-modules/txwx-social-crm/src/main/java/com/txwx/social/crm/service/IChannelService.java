/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.service;

import com.txwx.social.crm.domain.po.TxwxChannelPO;
import com.txwx.social.crm.domain.po.TxwxProductChannelPO;

import java.util.List;
import java.util.Map;

/**
 * 渠道服务接口
 *
 * @author txwx
 * @date 2026-04-03
 */
public interface IChannelService {

    /**
     * 查询渠道列表
     *
     * @param channel 渠道参数
     * @return 渠道列表
     */
    List<TxwxChannelPO> selectChannelList(TxwxChannelPO channel);

    /**
     * 查询渠道详情
     *
     * @param id 主键ID
     * @return 渠道详情
     */
    TxwxChannelPO selectChannelById(Long id);


    /**
     * 查询渠道详情
     *
     * @param ids 主键ID
     * @return 渠道详情
     */
    List<TxwxChannelPO> selectChannelByIds(List<Long> ids);

    Map<Long, List<TxwxChannelPO>> selectChannelByProductIds(List<Long> productIds);

    Map<Long, List<TxwxChannelPO>> selectChannelByProductIds(Map<Long,List<TxwxProductChannelPO>> p2cIdMap);

    /**
     * 新增渠道
     *
     * @param channel 渠道信息
     * @return 结果
     */
    int insertChannel(TxwxChannelPO channel, TxwxProductChannelPO productChannelPO);

    /**
     * 修改渠道
     *
     * @param channel 渠道信息
     * @return 结果
     */
    int updateChannel(TxwxChannelPO channel);

    /**
     * 删除渠道
     *
     * @param ids 主键ID列表
     * @return 结果
     */
    int deleteChannelByIds(List<Long> ids);

}
