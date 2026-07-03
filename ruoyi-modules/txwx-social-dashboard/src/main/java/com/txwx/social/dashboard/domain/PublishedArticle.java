/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.dashboard.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * 已发布文章实体类（用于存储到ClickHouse）
 */
@Data
@EqualsAndHashCode
@ToString
public class PublishedArticle {

    /**
     * 文章mid（从url中解析得到）
     */
    private String mid;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 所属自媒体账号id
     */
    private Long account_id;

    private String author;

    private String url;

    /**
     * 转换为Object数组，用于批量插入ClickHouse
     */
    public Object[] toObject(Long account_id) {
        // 将Unix时间戳转换为LocalDate
        LocalDate createDate = null;
        if (createTime != null) {
            createDate = Instant.ofEpochSecond(createTime)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        return new Object[]{mid, title, createDate, author, url, account_id};
    }

}
