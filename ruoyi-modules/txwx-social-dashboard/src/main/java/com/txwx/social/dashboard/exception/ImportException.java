/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.dashboard.exception;

import com.txwx.social.dashboard.domain.vo.RowErrorVo;
import lombok.Getter;

import java.util.List;

/**
 * 数据导入异常
 * 当 Excel 导入发生数据校验错误或格式错误时抛出
 */
@Getter
public class ImportException extends RuntimeException {

    private final List<RowErrorVo> errors;

    public ImportException(String message, List<RowErrorVo> errors) {
        super(message);
        this.errors = errors;
    }

    public ImportException(List<RowErrorVo> errors) {
        super("导入存在 " + errors.size() + " 条错误数据");
        this.errors = errors;
    }
}
