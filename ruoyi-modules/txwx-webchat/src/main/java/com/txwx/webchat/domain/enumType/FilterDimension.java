package com.txwx.webchat.domain.enumType;

import org.apache.commons.collections4.list.AbstractLinkedList;

public enum FilterDimension {

    WEEK(0, "本周"),
    MONTH(1, "本月"),
    HALF_YEAR(2, "半年"),
    YEAR(3, "本年"),
    ALL(4, "所有");

    private int code;
    private String desc;

    FilterDimension(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static String getDescByCode(int code) {
        for (FilterDimension filterDimension : FilterDimension.values()) {
            if (filterDimension.getCode() == code) {
                return filterDimension.getDesc();
            }
        }
        return "";
    }
}
