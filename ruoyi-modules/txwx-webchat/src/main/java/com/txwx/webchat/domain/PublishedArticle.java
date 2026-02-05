package com.txwx.webchat.domain;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * 已发布文章实体类（用于存储到ClickHouse）
 */
@Data
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
     * 转换为Object数组，用于批量插入ClickHouse
     */
    public Object[] toObject() {
        // 将Unix时间戳转换为LocalDate
        LocalDate createDate = null;
        if (createTime != null) {
            createDate = Instant.ofEpochSecond(createTime)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        return new Object[]{mid, title, createDate};
    }
}
