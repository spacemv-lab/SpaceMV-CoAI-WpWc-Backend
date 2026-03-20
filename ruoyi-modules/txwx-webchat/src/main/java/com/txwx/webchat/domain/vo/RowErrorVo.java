package com.txwx.webchat.domain.vo;

import com.txwx.webchat.domain.entity.DwsUsers;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RowErrorVo<T> {

    // 错误行号
    private long rowNo;
    // 错误信息
    private String errorMessage;
    // 错误行数据
    private T rowData;
}
