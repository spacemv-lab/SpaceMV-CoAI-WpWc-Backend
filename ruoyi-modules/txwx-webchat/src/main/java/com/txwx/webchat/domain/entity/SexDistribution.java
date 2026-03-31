package com.txwx.webchat.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.txwx.webchat.mapper.IImportBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("sex_distribution")
public class SexDistribution implements IImportBaseModel {

    @Schema(description = "性别")
    @ExcelProperty("性别")
    private String sex;

    @Schema(description = "用户数")
    @ExcelProperty("用户数")
    private Long userNumber;

    @Schema(description = "占比")
    @ExcelProperty("占比")
    private String proportion;

    @Schema(description = "产品ID")
    @ExcelProperty("产品ID")
    private Long productId;

    @Schema(description = "平台ID")
    @ExcelProperty("平台ID")
    private Long platformId;

    @Override
    public Object[] toObject() {
        return new Object[]{sex, userNumber, proportion, productId, platformId};
    }

    @Override
    public void validate() {
        if (sex == null || "".equals(sex) || userNumber == null) {
            throw new RuntimeException("每行数据不能为空!");
        }
    }
}
