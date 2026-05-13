package com.txwx.social.dashboard.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportResultVo {

    private Integer count;

    private Integer skippedCount; // 跳过的数量（因ref_date重复）

    private List<RowErrorVo> errors;

    public static ImportResultVo of(Integer count, List<RowErrorVo> errors) {
        ImportResultVo result = new ImportResultVo();
        result.setCount(count);
        result.setSkippedCount(0);
        result.setErrors(errors);
        return result;
    }

    public static ImportResultVo of(Integer count, Integer skippedCount, List<RowErrorVo> errors) {
        ImportResultVo result = new ImportResultVo();
        result.setCount(count);
        result.setSkippedCount(skippedCount);
        result.setErrors(errors);
        return result;
    }
}
