package com.txwx.webchat.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.txwx.webchat.mapper.IImportBaseModel;
import lombok.Data;

@Data
@TableName("channel_composition")
public class ChannelComposition implements IImportBaseModel {

    @ExcelProperty("渠道构成")
    private String channel;

    @ExcelProperty("人数")
    private Long userNumber;

    @ExcelProperty("占比")
    private String proportion;

    @Override
    public Object[] toObject() {
        return new Object[]{channel, userNumber, proportion};
    }

    @Override
    public void validate() {
        if (channel == null || "".equals(channel) || userNumber == null || proportion == null) {
            throw new RuntimeException("每行数据不能为空!");
        }
    }
}
