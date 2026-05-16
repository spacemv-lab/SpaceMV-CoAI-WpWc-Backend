/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.service;

import com.txwx.social.api.domain.dto.AccountDTO;
import com.txwx.social.crm.domain.po.TxwxAccountPO;

import java.util.List;
import java.util.Map;

/**
 * 账号服务接口
 *
 * @author txwx
 * @date 2026-04-03
 */
public interface IAccountService {

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
    TxwxAccountPO selectAccountById(Long id);

    /**
     * 新增账号
     *
     * @param account 账号信息
     * @return 结果
     */
    int insertAccount(TxwxAccountPO account);

    int insertAccountDTO(AccountDTO accountDTO);
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
    int deleteAccountByIds(List<Long> ids);

    /**
     * 根据产品ID查询账号列表
     *
     * @param productId 产品ID
     * @return 账号列表
     */
    List<TxwxAccountPO> selectAccountByProductId(Long productId);

    /**
     * 根据产品ID查询账号列表
     *
     * @param productIds 产品ID
     * @return 账号列表
     */
    Map<Long, List<TxwxAccountPO>> selectAccountByProductId(List<Long> productIds);

    /**
     * 根据渠道ID查询账号列表
     *
     * @param channelId 渠道ID
     * @return 账号列表
     */
    List<TxwxAccountPO> selectAccountByChannelId(Long channelId);

    Map<Long, List<TxwxAccountPO>> selectAccountByChannelIds(List<Long> cids);

    int deleteByProductIds(List<Long> productIds);

    List<TxwxAccountPO> selectAccountByQuery(TxwxAccountPO query);
}
