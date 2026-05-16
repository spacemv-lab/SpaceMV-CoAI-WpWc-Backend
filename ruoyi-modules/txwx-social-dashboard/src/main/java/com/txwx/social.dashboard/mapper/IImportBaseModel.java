package com.txwx.social.dashboard.mapper;

public interface IImportBaseModel {
    Object[] toObject(Long accountId);
    default void validate() {}
}
