package com.txwx.webchat.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.annotation.Excel;
import com.txwx.webchat.mapper.IImportBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("dws_users")
public class DwsUsers implements IImportBaseModel {

    @Schema(description = "数据日期")
    @Excel(name = "数据日期")
    @ExcelProperty("数据日期")
    private LocalDate refDate;

    @Excel(name = "新增用户数")
    @ExcelProperty("新增用户数")
    private Integer newUser;

    @Excel(name = "取消用户数")
    @ExcelProperty("取消用户数")
    private Integer cancelUser;

    @Excel(name = "净新增用户数")
    @ExcelProperty("净新增用户数")
    private Integer netNewUser;

    @Excel(name = "累计用户数")
    @ExcelProperty("累计用户数")
    private Integer accumulatedUser;

    public Object[] toObject(){
        return new Object[]{refDate, newUser, cancelUser, netNewUser, accumulatedUser};
    }
}
