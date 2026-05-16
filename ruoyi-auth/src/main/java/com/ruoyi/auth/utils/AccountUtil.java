/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.auth.utils;

import com.ruoyi.common.core.utils.StringUtils;

public class AccountUtil {

    // 手机号正则：1开头，第二位3-9，后面9位数字
    public static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    // 邮箱正则：支持大多数常见邮箱格式
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // 更严格的邮箱正则（推荐使用）
    public static final String EMAIL_REGEX_STRICT = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // 支持中文邮箱的正则
    public static final String EMAIL_REGEX_WITH_CHINESE = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";

    /**
     * 判断是否为手机号
     * 简单判断：11位数字，1开头，第二位3-9
     */
    public static boolean isPhone(String account) {
        if (StringUtils.isEmpty(account)) {
            return false;
        }
        return account.matches(PHONE_REGEX);
    }

    /**
     * 判断是否为邮箱（通用版本）
     * 支持格式：xxx@xxx.xxx
     */
    public static boolean isEmail(String account) {
        if (StringUtils.isEmpty(account)) {
            return false;
        }
        return account.matches(EMAIL_REGEX);
    }

    /**
     * 判断是否为邮箱（严格版本）
     * 1. 用户名部分：字母数字、下划线、点、中划线、加号、百分号
     * 2. @符号
     * 3. 域名部分：字母数字、点、中划线
     * 4. 顶级域名：至少2个字母
     */
    public static boolean isEmailStrict(String account) {
        if (StringUtils.isEmpty(account)) {
            return false;
        }
        return account.matches(EMAIL_REGEX_STRICT);
    }

    /**
     * 判断是否为邮箱（支持中文）
     * 支持中文用户名，如：张三@qq.com
     */
    public static boolean isEmailWithChinese(String account) {
        if (StringUtils.isEmpty(account)) {
            return false;
        }
        return account.matches(EMAIL_REGEX_WITH_CHINESE);
    }

    /**
     * 判断账号类型
     *
     * @param account 账号
     * @return 1-手机号，2-邮箱，0-未知
     */
    public static int getAccountType(String account) {
        if (StringUtils.isEmpty(account)) {
            return 0;
        }

        if (isPhone(account)) {
            return 1; // 手机号
        }

        if (isEmail(account)) {
            return 2; // 邮箱
        }

        return 0; // 未知
    }
}
