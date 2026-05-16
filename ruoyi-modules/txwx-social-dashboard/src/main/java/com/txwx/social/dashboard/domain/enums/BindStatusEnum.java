/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.dashboard.domain.enums;

public enum BindStatusEnum {

    UNBIND(0, "未绑定"),
    BINDED(1, "已绑定");

    private final Integer code;
    private final String desc;

    BindStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static BindStatusEnum getByCode(Integer code) {
        for (BindStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
