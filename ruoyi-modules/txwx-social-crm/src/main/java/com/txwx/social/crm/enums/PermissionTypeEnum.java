/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.enums;

/**
 * 用户协作类型枚举
 */
public enum PermissionTypeEnum {

    /**
     * 产品级
     */
    PRODUCT_LEVEL(1, "产品级"),

    /**
     * 平台级
     */
    CHANNEL_LEVEL(2, "平台级"),

    /**
     * 账号级
     */
    ACCOUNT_LEVEL(3, "账号级");


    private final Integer code;
    private final String desc;

    PermissionTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static PermissionTypeEnum getByCode(Integer code) {
        for (PermissionTypeEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
