/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.annotation;

import java.lang.annotation.*;

/**
 * 图形验证码校验注解
 * <p>
 * 标记在 Controller 方法上，由 {@code CaptchaValidateAspect} 自动拦截，
 * 从请求 DTO 中提取 code 和 uuid 并校验图形验证码。
 * <p>
 * 适用场景：注册、登录、重置密码、解绑等需要人机验证的操作。
 *
 * @author txwx
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CaptchaValidate
{
}
