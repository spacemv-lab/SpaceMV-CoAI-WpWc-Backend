/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.service;

/**
 * 验证码服务接口
 *
 * @author txwx
 * @date 2026-04-20
 */
public interface ITxwxVerifyCodeService {

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @return 结果
     */
    String sendSmsCode(String phone);

    /**
     * 发送邮箱验证码
     *
     * @param email 邮箱
     * @return 结果
     */
    String sendEmailCode(String email);

    /**
     * 校验验证码
     *
     * @param account 手机号/邮箱
     * @param code 验证码
     * @return 是否正确
     */
    boolean verifyCode(String account, String code);

}
