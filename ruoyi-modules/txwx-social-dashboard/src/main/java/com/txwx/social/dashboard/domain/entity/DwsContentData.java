package com.txwx.social.dashboard.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.TableName;
import com.txwx.social.dashboard.domain.mapper.IImportBaseModel;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("dws_content_data")
@ExcelIgnoreUnannotated
public class DwsContentData implements IImportBaseModel {

    @ExcelProperty("内容标题")
    private String title;

    @ExcelProperty("发表日期")
    @DateTimeFormat("yyyyMMdd")
    private LocalDate createTime;

    private LocalDate latestStatDate;

    private String msgid;

    private String author;

    @ExcelProperty("阅读人数")
    private Long readUserTotal;

    private Long readUserSourceAll;

    private Long readUserSourceMsg;

    private Long readUserSourceChat;

    private Long readUserSourceMoments;

    private Long readUserSourceHomepage;

    private Long readUserSourceOther;

    private Long readUserSourceRecommend;

    private Long readUserSourceSearch;

    @ExcelProperty("分享人数")
    private Long shareUser;

    @ExcelProperty("阅读后关注人数")
    private Long readSubscribeUser;

    @ExcelProperty("内容url")
    private String url;

    private Long accountId;

    @Override
    public Object[] toObject(Long accountId) {
        return new Object[]{createTime, title, readUserTotal, shareUser, readSubscribeUser, url, accountId};
    }
}
