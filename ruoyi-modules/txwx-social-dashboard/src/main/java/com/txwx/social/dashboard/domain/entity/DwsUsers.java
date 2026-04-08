package com.txwx.social.dashboard.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.annotation.Excel;
import com.txwx.social.dashboard.mapper.IImportBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

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

    @ExcelProperty("产品ID")
    private Long productId;

    @ExcelProperty("平台ID")
    private Long platformId;

    public Object[] toObject(){
        return new Object[]{refDate, newUser, cancelUser, netNewUser, accumulatedUser, productId, platformId};
    }
}
