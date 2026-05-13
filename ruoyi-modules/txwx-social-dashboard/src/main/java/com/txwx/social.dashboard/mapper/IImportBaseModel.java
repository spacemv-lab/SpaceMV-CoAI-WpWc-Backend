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
