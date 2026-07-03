/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.dto;

/**
 * 图形验证码请求标记接口
 * <p>
 * 实现了该接口的 DTO 表示其包含 {@code code} 和 {@code uuid} 字段，
 * {@code CaptchaValidateAspect} 通过该接口自动提取并校验验证码。
 *
 * @author txwx
 */
public interface CaptchaRequest
{
    /**
     * 获取用户输入的验证码
     */
    String getCode();

    /**
     * 获取验证码 UUID（由 /code 接口返回）
     */
    String getUuid();
}
