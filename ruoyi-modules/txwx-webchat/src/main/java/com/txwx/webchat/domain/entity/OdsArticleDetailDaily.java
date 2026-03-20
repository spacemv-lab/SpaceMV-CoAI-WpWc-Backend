package com.txwx.webchat.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.TableName;
import com.txwx.webchat.mapper.IImportBaseModel;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("ods_article_detail_daily")
@ExcelIgnoreUnannotated
public class OdsArticleDetailDaily {

    @DateTimeFormat("yyyyMMdd")
    private LocalDate statDate;

    @DateTimeFormat("yyyyMMdd")
    private LocalDate refDate;

    private String msgid;

    private Integer publishType;

    private Long readUser;

    private Long readUserSourceAll;

    private Long readUserSourceMsg;

    private Long readUserSourceChat;

    private Long readUserSourceMoments;

    private Long readUserSourceHomepage;

    private Long readUserSourceOther;

    private Long readUserSourceRecommend;

    private Long readUserSourceSearch;

    private Long shareUser;

    private Long zaikanUser;

    private Long likeUser;

    private Long commentCount;

    private Long collectionUser;

    private Long praiseMoney;

    private Long readSubscribeUser;

    private Float readDeliveryRate;

    private Float readFinishRate;

    private Float readAvgActivetime;

    private String title;

    private String url;
}
