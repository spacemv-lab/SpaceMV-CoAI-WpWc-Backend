package com.txwx.social.dashboard.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.txwx.social.dashboard.mapper.IImportBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("ods_article_summary_daily")
public class OdsArticleSummaryDaily implements IImportBaseModel {

    @Schema(description = "日期")
    @ExcelProperty("日期")
    @DateTimeFormat("yyyyMMdd")
    private String ref_date;

    @Schema(description = "阅读人数")
    @ExcelProperty("阅读人数")
    private Long read_user_total;

    @Schema(description = "分享人数")
    @ExcelProperty("分享人数")
    private Long share_user;

    @Schema(description = "收藏人数")
    @ExcelProperty("收藏人数")
    private Long collection_user;

    @Schema(description = "公众号消息阅读人数")
    @ExcelProperty("公众号消息阅读人数")
    private Long read_user_source_msg;

    @Schema(description = "聊天会话阅读人数")
    @ExcelProperty("聊天会话阅读人数")
    private Long read_user_source_chat;

    @Schema(description = "朋友圈阅读人数")
    @ExcelProperty("朋友圈阅读人数")
    private Long read_user_source_moments;

    @Schema(description = "公众号主页阅读人数")
    @ExcelProperty("公众号主页阅读人数")
    private Long read_user_source_homepage;

    @Schema(description = "其他阅读人数")
    @ExcelProperty("其他阅读人数")
    private Long read_user_source_other;

    @Schema(description = "推荐阅读人数")
    @ExcelProperty("推荐阅读人数")
    private Long read_user_source_recommend;

    @Schema(description = "搜一搜阅读人数")
    @ExcelProperty("搜一搜阅读人数")
    private Long read_user_source_search;

    @Schema(description = "自媒体账号ID")
    @ExcelIgnore
    private Long accountId;

    @Override
    public Object[] toObject(Long accountId) {
        return new Object[]{
                ref_date,
                read_user_total,
                share_user,
                collection_user,
                read_user_source_msg,
                read_user_source_chat,
                read_user_source_moments,
                read_user_source_homepage,
                read_user_source_other,
                read_user_source_recommend,
                read_user_source_search,
            accountId
        };
    }

    @Override
    public void validate() {
        if (ref_date == null
                || read_user_total == null || share_user == null
                || read_user_source_msg == null
                || read_user_source_chat == null
                || read_user_source_moments == null
                || collection_user == null
                || read_user_source_homepage == null
                || read_user_source_other == null
                || read_user_source_recommend == null
                || read_user_source_search == null) {
            throw new ServiceException("存在为空列");
        }
        LocalDate targetDate = LocalDate.of(2025, 11, 1);
        // 将字符串转换为LocalDate
        LocalDate date = LocalDate.parse(ref_date,
                DateTimeFormatter.ofPattern("yyyyMMdd"));
        if (!date.isBefore(targetDate)) {
            throw new ServiceException("传入数据日期需要在2025-11-01之前");
        }
    }
}
