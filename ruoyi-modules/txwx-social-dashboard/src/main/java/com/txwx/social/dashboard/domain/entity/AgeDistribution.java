package com.txwx.social.dashboard.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.txwx.social.dashboard.mapper.IImportBaseModel;
import lombok.Data;

@Data
@TableName("age_distribution")
public class AgeDistribution implements IImportBaseModel {

    @ExcelProperty("年龄")
    private String age;

    @ExcelProperty("用户数")
    private Long userNumber;

    @ExcelProperty("占比")
    private String proportion;

    @ExcelProperty("产品id")
    private Long productId;

    @ExcelProperty("平台id")
    private Long platformId;

    @Override
    public Object[] toObject() {
        return new Object[]{age, userNumber, proportion, productId, platformId};
    }

    @Override
    public void validate() {
        if (age == null || "".equals(age) || userNumber == null || proportion == null) {
            throw new RuntimeException("每行数据不能为空!");
        }
    }
}
