package com.txwx.webchat.domain.vo;

import com.txwx.webchat.domain.entity.DwsUsers;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RowErrorVo<T> {

    private long rowNo;  // 错误行号

    private String errorMessage; // 错误信息

    private T rowData;  // 错误行数据
}
