package com.txwx.social.dashboard.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.txwx.social.dashboard.domain.mapper.IImportBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("dim_terrain_distribution")
public class TerrainDistribution implements IImportBaseModel {

    @ExcelProperty("地域")
    private String terrain;

    @ExcelProperty("用户数")
    private Long userNumber;

    @ExcelProperty("占比")
    private String proportion;

    @Schema(description = "自媒体账号ID")
    @ExcelIgnore
    private Long accountId;

    @Override
    public Object[] toObject(Long accountId) {
        return new Object[]{terrain, userNumber, proportion, accountId};
    }

    @Override
    public void validate() {
        if (terrain == null || terrain.isEmpty() || userNumber == null ) {
            throw new RuntimeException("每行数据不能为空!");
        }
    }
}
