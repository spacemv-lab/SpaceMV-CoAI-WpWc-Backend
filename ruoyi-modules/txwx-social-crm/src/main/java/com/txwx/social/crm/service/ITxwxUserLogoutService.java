/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.txwx.social.crm.domain.po.TxwxUserLogoutPO;

import java.util.List;

/**
 * 账号注销申请表服务接口
 *
 * @author txwx
 * @date 2026-04-20
 */
public interface ITxwxUserLogoutService extends IService<TxwxUserLogoutPO> {

    /**
     * 根据用户ID查询最新的注销申请
     *
     * @param userId 用户ID
     * @return 注销申请
     */
    TxwxUserLogoutPO selectByUserId(Long userId);

    /**
     * 更新注销申请状态
     *
     * @param logoutId 注销ID
     * @param status 状态
     * @param updateBy 更新人
     * @return 结果
     */
    boolean updateStatus(Long logoutId, String status, String updateBy);

    /**
     * 查询冷却期结束的注销申请
     *
     * @return 注销申请列表
     */
    List<TxwxUserLogoutPO> selectCoolingCompleted();

    /**
     * 查询注销申请列表
     *
     * @param query 查询参数
     * @return 注销申请列表
     */
    List<TxwxUserLogoutPO> selectList(TxwxUserLogoutPO query);
}
