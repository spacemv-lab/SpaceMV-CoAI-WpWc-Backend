package com.txwx.social.crm.mapper;

import com.txwx.social.crm.domain.po.TxwxAccountPO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 账号Mapper
 *
 * @author txwx
 * @date 2026-04-03
 */
@Mapper
public interface TxwxAccountMapper {

    /**
     * 查询账号列表
     *
     * @param account 账号参数
     * @return 账号列表
     */
    List<TxwxAccountPO> selectAccountList(TxwxAccountPO account);

    /**
     * 查询账号详情
     *
     * @param id 主键ID
     * @return 账号详情
     */
    TxwxAccountPO selectAccountById(@Param("id") Long id);

    /**
     * 新增账号
     *
     * @param account 账号信息
     * @return 结果
     */
    int insertAccount(TxwxAccountPO account);

    /**
     * 修改账号
     *
     * @param account 账号信息
     * @return 结果
     */
    int updateAccount(TxwxAccountPO account);

    /**
     * 删除账号
     *
     * @param ids 主键ID列表
     * @return 结果
     */
    int deleteAccountByIds(@Param("ids") List<Long> ids);

    /**
     * 根据产品ID查询账号列表
     *
     * @param productId 产品ID
     * @return 账号列表
     */
    List<TxwxAccountPO> selectAccountByProductId(@Param("productId") Long productId);

    /**
     * 根据渠道ID查询账号列表
     *
     * @param channelId 渠道ID
     * @return 账号列表
     */
    List<TxwxAccountPO> selectAccountByChannelId(@Param("channelId") Long channelId);


    List<TxwxAccountPO> selectAccountByProductIds(@Param("productIds") List<Long> productIds);

    List<TxwxAccountPO> selectAccountByChannelIds(@Param("channelIds") List<Long> cids);

    List<TxwxAccountPO> selectAccountByChannelIdsAndProductIds(@Param("channelIds") List<Long> cids, @Param("productIds") List<Long> productIds);
}
