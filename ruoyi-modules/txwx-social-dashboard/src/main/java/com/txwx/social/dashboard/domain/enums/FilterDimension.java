/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.dashboard.domain.enums;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public enum FilterDimension {

    WEEK(0, "本周"),
    MONTH(1, "本月"),
    HALF_YEAR(2, "半年"),
    YEAR(3, "本年"),
    ALL(4, "所有");

    public int code;
    public String desc;

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

    public static LocalDate calculateStartTimeEnhanced(Integer filterDimension) {
        LocalDate endTime = LocalDate.now();
        return switch (filterDimension) {
            case 0 -> endTime.minusWeeks(1);           // 周// 两周
            case 1 -> endTime.minusMonths(1);          // 月// 季度
            case 2 -> endTime.minusMonths(6);          // 半年
            case 3 -> endTime.minusYears(1);   // 年// 30天
            case 4 -> null;
            default -> endTime.minusWeeks(1);
        };
    }

    public static Integer calcMinusDays(Integer filterDimension) {
        Integer days = 7;
        switch (filterDimension) {
            case 1: // 30 天
                days = 30;
                break;
            case 2: // 半年（180天）
                days = 180;
                break;
            case 3:
                // 1年
                days = 365;
                break;
            case 4:
                days = null;
                break;
            default:
                days = 7;
                break;
        }
        return days;
    }
}
