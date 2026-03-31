package com.txwx.webchat.domain.enums;

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
