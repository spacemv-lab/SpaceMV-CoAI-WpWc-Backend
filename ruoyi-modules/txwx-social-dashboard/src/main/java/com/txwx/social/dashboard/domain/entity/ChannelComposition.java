package com.txwx.social.dashboard.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.txwx.social.dashboard.domain.mapper.IImportBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("dim_channel_composition")
public class ChannelComposition implements IImportBaseModel {

    @ExcelProperty("渠道构成")
    private String channel;

    @ExcelProperty("人数")
    private Long userNumber;

    @ExcelProperty("占比")
    private String proportion;

    @Schema(description = "自媒体账号ID")
    @ExcelIgnore
    private Long accountId;

    @Override
    public Object[] toObject(Long accountId) {
        return new Object[]{channel, userNumber, proportion, accountId};
    }

    @Override
    public void validate() {
        if (channel == null || channel.isEmpty() || userNumber == null || proportion == null) {
            throw new RuntimeException("每行数据不能为空!");
        }
    }
}
