package com.txwx.social.dashboard.domain.dto;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.txwx.social.dashboard.mapper.IImportBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@ExcelIgnoreUnannotated
public class OdsArticleDetailDailyDTO implements IImportBaseModel {

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
        return new Object[]{refDate, refDate, title, readUser, shareUser, readSubscribeUser, url, accountId};
    }
}
