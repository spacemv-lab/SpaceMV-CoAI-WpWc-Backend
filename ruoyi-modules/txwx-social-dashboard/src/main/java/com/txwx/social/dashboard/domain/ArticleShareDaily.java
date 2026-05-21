/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.dashboard.domain;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public class ArticleShareDaily {

    @JSONField(name = "ref_date")
    private String refDate;

    @JSONField(name = "msgid")
    private String msgid;

    @JSONField(name = "detail")
    private ArticleShareDetail detail;

    @JSONField(name = "account_id")
    private Long accountId;

    /**
     * @description: 转换为Object数组用于批量插入ClickHouse
     */
    public Object[] toObject(Long accountId) {
        return new Object[]{
            refDate,
            msgid,
            detail != null ? detail.getShareUser() : 0,
            accountId
        };
    }

    /**
     * @description: 分享详情内部类
     */
    @Data
    public static class ArticleShareDetail {
        @JSONField(name = "share_user")
        private Integer shareUser;

        @JSONField(name = "account_id")
        private Long accountId;
    }

}

