package com.txwx.webchatcrm.enums;

/**
 * 文章状态枚举
 */
public enum ArticleStatusEnum {

    /**
     * 待提交
     */
    PENDING_SUBMIT("0", "待提交"),

    /**
     * 待审核
     */
    PENDING_REVIEW("1", "待审核"),

    /**
     * 审核不通过
     */
    REVIEW_REJECTED("2", "审核不通过"),

    /**
     * 审核通过待发布
     */
    REVIEW_APPROVED("3", "审核通过待发布"),

    /**
     * 已发布
     */
    PUBLISHED("4", "已发布"),

    /**
     * 已发布
     */
    PUBLISHING("5", "发布中");

    private final String code;
    private final String desc;

    ArticleStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ArticleStatusEnum getByCode(String code) {
        for (ArticleStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
