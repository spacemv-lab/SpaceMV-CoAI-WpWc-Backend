package com.txwx.social.dashboard.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.txwx.social.dashboard.domain.mapper.IImportBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("dim_age_distribution")
public class AgeDistribution implements IImportBaseModel {

    @ExcelProperty("年龄")
    private String age;

    @ExcelProperty("用户数")
    private Long userNumber;

    @ExcelProperty("占比")
    private String proportion;

    @Schema(description = "自媒体账号ID，不暴露给用户")
    @ExcelIgnore
    private Long accountId;

    @Override
    public Object[] toObject(Long accountId) {
        return new Object[]{age, userNumber, proportion, accountId};
    }

    @Override
    public void validate() {
        if (age == null || age.isEmpty() || userNumber == null || proportion == null) {
            throw new RuntimeException("每行数据不能为空!");
        }
    }
}
