/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.dashboard.mapper;

public interface IImportBaseModel {
    Object[] toObject(Long accountId);
    
    default void validate() {}
    
    /**
     * 获取ref_date字符串表示，用于去重判断
     * @return ref_date字符串，格式：yyyyMMdd
     */
    String getRefDateStr();
}
