package com.txwx.social.dashboard.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.txwx.social.dashboard.mapper.IImportBaseModel;
import lombok.Data;

@Data
@TableName("terrain_distribution")
public class TerrainDistribution implements IImportBaseModel {

    @ExcelProperty("地域")
    private String terrain;

    @ExcelProperty("用户数")
    private Long userNumber;

    @ExcelProperty("占比")
    private String proportion;

    @ExcelProperty("产品ID")
    private Long productId;

    @ExcelProperty("平台ID")
    private Long platformId;

    @Override
    public Object[] toObject() {
        return new Object[]{terrain, userNumber, proportion, productId, platformId};
    }

    @Override
    public void validate() {
        if (terrain == null || "".equals(terrain) || userNumber == null) {
            throw new RuntimeException("每行数据不能为空!");
        }
    }
}
