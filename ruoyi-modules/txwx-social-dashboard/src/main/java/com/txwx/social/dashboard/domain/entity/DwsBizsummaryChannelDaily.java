package com.txwx.social.dashboard.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.TableName;
import com.txwx.social.dashboard.mapper.IImportBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("dws_bizsummary_channel_daily")
public class DwsBizsummaryChannelDaily implements IImportBaseModel {

    @Schema(description = "日期")
    @ExcelProperty("日期")
    @DateTimeFormat("yyyyMMdd")
    private String refDate;

    @Schema(description = "阅读人数")
    @ExcelProperty("阅读人数")
    private Long readUserCnt;

    @Schema(description = "分享人数")
    @ExcelProperty("分享人数")
    private Long shareUser;

    @Schema(description = "阅读原文次数")
    @ExcelProperty("阅读原文次数")
    private Long redirectOriPageCount;

    @Schema(description = "阅读原文人数")
    @ExcelProperty("阅读原文人数")
    private Long redirectOriPageUser;

    @Schema(description = "收藏次数")
    @ExcelProperty("收藏次数")
    private Long collectionCount;

    @Schema(description = "收藏人数")
    @ExcelProperty("收藏人数")
    private Long collectionUser;

    @Schema(description = "群发篇数")
    @ExcelProperty("群发篇数")
    private Long sendPageCount;

    @Schema(description = "渠道")
    @ExcelProperty("渠道")
    private String channel;

    @Schema(description = "自媒体账号ID")
    @ExcelIgnore
    private Long accountId;

    @Override
    public Object[] toObject(Long accountId) {
        return new Object[]{
            refDate,
            readUserCnt,
            shareUser,
            redirectOriPageCount,
            redirectOriPageUser,
            collectionCount,
            collectionUser,
            sendPageCount,
            channel,
            accountId
        };
    }
}
