package com.txwx.social.crm.mapper;

import com.txwx.social.crm.domain.po.TxwxChannelPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 渠道Mapper
 *
 * @author txwx
 * @date 2026-04-03
 */
@Mapper
public interface TxwxChannelMapper {

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
    TxwxChannelPO selectChannelById(@Param("id") Long id);

    /**
     * 新增渠道
     *
     * @param channel 渠道信息
     * @return 结果
     */
    int insertChannel(TxwxChannelPO channel);

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
    int deleteChannelByIds(@Param("ids") List<Long> ids);

    List<TxwxChannelPO> selectChannelByIds(@Param("ids") List<Long> ids);
}
