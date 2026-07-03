/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.util;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 用户名生成器
 * 规则：spmv_ + 8位随机小写字母+数字
 * 示例：spmv_a1b2c3d4
 *
 * @author txwx
 */
public class UsernameGenerator
{
    private static final String PREFIX = "spmv_";
    private static final int RANDOM_LENGTH = 8;
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * 生成用户名（不含唯一性检查）
     */
    public static String generate()
    {
        StringBuilder sb = new StringBuilder(PREFIX.length() + RANDOM_LENGTH);
        sb.append(PREFIX);
        for (int i = 0; i < RANDOM_LENGTH; i++)
        {
            int index = SECURE_RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
}
