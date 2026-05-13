package com.txwx.social.dashboard.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.txwx.social.dashboard.mapper.IImportBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("dim_sex_distribution")
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

    @Schema(description = "自媒体账号ID")
    @ExcelIgnore
    private Long accountId;

    @Override
    public Object[] toObject(Long accountId) {
        return new Object[]{sex, userNumber, proportion, accountId};
    }

    @Override
    public String getRefDateStr() {
        return null; // SexDistribution无ref_date字段，跳过去重检查
    }

    @Override
    public void validate() {
        if (sex == null || sex.isEmpty() || userNumber == null) {
            throw new RuntimeException("每行数据不能为空!");
        }
    }
}
