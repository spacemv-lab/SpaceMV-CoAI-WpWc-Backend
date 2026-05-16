/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.common.core.constant;

import org.springframework.beans.factory.annotation.Value;

/**
 * Token的Key常量
 * 
 * @author ruoyi
 */
public class TokenConstants
{
    /**
     * 令牌前缀
     */
    public static final String PREFIX = "Bearer ";

    /**
     * 令牌秘钥, 需要自己替换
     */
    @Value("${token.secret:your-token-secret}")
    public static String SECRET;

}
