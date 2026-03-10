package com.txwx.webchat.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportResultVo {

    private Integer count;

    private List<RowErrorVo> errors;

    public static ImportResultVo of(Integer count, List<RowErrorVo> errors) {
        ImportResultVo result = new ImportResultVo();
        result.setCount(count);
        result.setErrors(errors);
        return result;
    }
}
