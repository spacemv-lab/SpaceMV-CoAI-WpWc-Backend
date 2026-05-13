package com.txwx.social.dashboard.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.annotation.Excel;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.txwx.social.dashboard.mapper.IImportBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@TableName("dws_users")
public class DwsUsers implements IImportBaseModel {

    @Schema(description = "时间")
    @Excel(name = "时间")
    @ExcelProperty("时间")
    @DateTimeFormat("yyyyMMdd")
    private LocalDate refDate;

    @Excel(name = "新关注人数")
    @ExcelProperty("新关注人数")
    private Long newUser;

    @Excel(name = "取消关注人数")
    @ExcelProperty("取消关注人数")
    private Long cancelUser;

    @Excel(name = "净增关注人数")
    @ExcelProperty("净增关注人数")
    private Long netNewUser;

    @Excel(name = "累积关注人数")
    @ExcelProperty("累积关注人数")
    private Long accumulatedUser;

    @Schema(description = "自媒体账号ID")
    @ExcelIgnore
    private Long accountId;

    public Object[] toObject(Long accountId){
        return new Object[]{refDate, newUser, cancelUser, netNewUser, accumulatedUser, accountId};
    }

    @Override
    public void validate() {
        if (refDate == null
                || newUser == null || cancelUser == null
                || netNewUser == null
                || accumulatedUser == null) {
            throw new ServiceException("存在为空列");
        }
        LocalDate targetDate = LocalDate.of(2025, 11, 1);
        if (!refDate.isBefore(targetDate)) {
            throw new ServiceException("传入数据日期需要在2025-11-01之前");
        }
    }

    @Override
    public String getRefDateStr() {
        return refDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}
