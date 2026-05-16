/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.dashboard.domain.dto;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.ruoyi.common.core.exception.ServiceException;
import com.txwx.social.dashboard.mapper.IImportBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@ExcelIgnoreUnannotated
public class OdsArticleDetailDailyDTO implements IImportBaseModel {

    @ExcelProperty("统计时间")
    @DateTimeFormat("yyyyMMdd")
    private LocalDate statDate;

    @ExcelProperty("发表时间")
    @DateTimeFormat("yyyyMMdd")
    private LocalDate refDate;

    private String msgid;

    private Integer publishType;

    private String author;

    @ExcelProperty("总阅读人数")
    private Long readUser;

    private Long readUserSourceAll;

    private Long readUserSourceMsg;

    private Long readUserSourceChat;

    private Long readUserSourceMoments;

    private Long readUserSourceHomepage;

    private Long readUserSourceOther;

    private Long readUserSourceRecommend;

    private Long readUserSourceSearch;

    @ExcelProperty("总分享人数")
    private Long shareUser;

    private Long zaikanUser;

    private Long likeUser;

    private Long commentCount;

    private Long collectionUser;

    private Long praiseMoney;

    @ExcelProperty("阅读后关注人数")
    private Long readSubscribeUser;

    private Float readDeliveryRate;

    private Float readFinishRate;

    private Float readAvgActivetime;

    @ExcelProperty("内容标题")
    private String title;

    @ExcelProperty("内容url")
    private String url;

    @Schema(description = "自媒体账号ID")
    private Long accountId;

    @Override
    public Object[] toObject(Long accountId) {
        return new Object[]{statDate, refDate, title, readUser, shareUser, readSubscribeUser, url, accountId};
    }

    @Override
    public void validate() {
        if (statDate == null || refDate == null
                || readUser == null || shareUser == null
                || readSubscribeUser == null
                || title == null
                || url == null) {
            throw new ServiceException("每行数据不能为空!");
        }
        LocalDate targetDate = LocalDate.of(2025, 11, 1);
        if (!statDate.isBefore(targetDate)) {
            throw new ServiceException("上传统计日期不得晚于或等于2025-11-01");
        }
    }

    @Override
    public String getRefDateStr() {
        return refDate.format(DateTimeFormatter.ISO_DATE);
    }
}
