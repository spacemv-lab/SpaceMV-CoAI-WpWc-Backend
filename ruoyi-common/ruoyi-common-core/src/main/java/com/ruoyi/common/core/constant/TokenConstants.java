/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.common.core.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Token的Key常量
 * 
 * @author ruoyi
 */
@Component
public class TokenConstants
{
    /**
     * 令牌前缀
     */
    public static final String PREFIX = "Bearer ";

    /**
     * 令牌秘钥, 需要自己替换
     *
     * 注意: @Value 不能直接标注在 static 字段上（Spring 5.x 限制），
     * 因此通过非静态 setter 将配置值写入静态字段。JwtUtils 在每次调用时通过
     * TokenConstants.SECRET 延迟读取，避免类加载时序问题。
     */
    public static String SECRET;

    @Value("${token.secret:your-token-secret}")
    public void setSecret(String secret)
    {
        TokenConstants.SECRET = secret;
    }

}
