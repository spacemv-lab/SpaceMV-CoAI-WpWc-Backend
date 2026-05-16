/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.txwx.social.crm.domain.po.TxwxUserRegisterPO;

import java.util.List;

/**
 * 用户注册扩展表服务接口
 *
 * @author txwx
 * @date 2026-04-20
 */
public interface ITxwxUserRegisterService extends IService<TxwxUserRegisterPO> {

    /**
     * 根据用户ID查询注册信息
     *
     * @param userId 用户ID
     * @return 注册信息
     */
    TxwxUserRegisterPO selectByUserId(Long userId);

    /**
     * 根据注册账号查询注册信息
     *
     * @param registerAccount 注册账号
     * @return 注册信息
     */
    TxwxUserRegisterPO selectByRegisterAccount(String registerAccount);


    /**
     * 根据用户名查询注册信息
     *
     * @param userName 用户名
     * @return 注册信息
     */
    TxwxUserRegisterPO selectByUserName(String userName);

    /**
     * 更新注册用户状态
     *
     * @param registerId 注册ID
     * @param status 状态
     * @param updateBy 更新人
     * @return 结果
     */
    boolean updateStatus(Long registerId, String status, String updateBy);

    /**
     * 查询注册用户列表
     *
     * @param query 查询参数
     * @return 注册用户列表
     */
    List<TxwxUserRegisterPO> selectList(TxwxUserRegisterPO query);
}
