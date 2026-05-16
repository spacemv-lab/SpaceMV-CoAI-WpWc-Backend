package com.txwx.social.dashboard.domain.enums;

public enum SyncStatusEnum {

    UNSYNC(0, "未同步"),
    SYNCING(1, "同步中"),
    SYNCED_SUCCESS(2, "已同步完成"),
    SYNCED_FAIL(3, "同步失败");

    private final Integer code;
    private final String desc;

    SyncStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static SyncStatusEnum getByCode(Integer code) {
        for (SyncStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
