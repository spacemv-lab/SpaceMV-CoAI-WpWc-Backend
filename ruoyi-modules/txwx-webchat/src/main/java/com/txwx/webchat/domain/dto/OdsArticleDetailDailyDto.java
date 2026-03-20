package com.txwx.webchat.domain.dto;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.TableName;
import com.txwx.webchat.mapper.IImportBaseModel;
import lombok.Data;

import java.time.LocalDate;

@Data
@ExcelIgnoreUnannotated
public class OdsArticleDetailDailyDto implements IImportBaseModel {

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

    @Override
    public Object[] toObject() {
        return new Object[]{refDate, refDate, title, readUser, shareUser, readSubscribeUser, url};
    }
}
