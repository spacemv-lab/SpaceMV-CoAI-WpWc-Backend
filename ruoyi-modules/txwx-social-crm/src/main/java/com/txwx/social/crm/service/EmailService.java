/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.service;

/**
 * 短信服务接口（抽象层，避免厂商锁定）
 *
 * @author txwx
 * @date 2026-04-21
 */
public interface EmailService {

    /**
     * 发送邮箱验证码
     *
     * @param email 邮箱
     * @param code 验证码
     * @return true=发送成功，false=发送失败
     */
    boolean sendVerifyCode(String email, String code);

    boolean verifyCode(String email, String code);
}
