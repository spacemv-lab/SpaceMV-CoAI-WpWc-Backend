/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the MIT License;
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 
 * the copywrite is belongs to  SpaceMV  team
 */
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
     * 发布中
     */
    PUBLISHING("5", "发布中"),

    /**
     * 原创失败
     */
    FAIL_ORIGINAL("6", "原创失败"),

    /**
     * 常规失败
     */
    FAIL_ROUTINE("7", "常规失败"),

    /**
     * 平台审核不通过
     */
    TENGSEN_REVIEW_APPROVED("8", "平台审核不通过"),

    /**
     * 成功后用户删除所有文章
     */
    DELETE_ALL("9", "成功后用户删除所有文章"),

    /**
     * 成功后系统封禁所有文章
     */
    BAN_ALL("10", "成功后系统封禁所有文章");

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
