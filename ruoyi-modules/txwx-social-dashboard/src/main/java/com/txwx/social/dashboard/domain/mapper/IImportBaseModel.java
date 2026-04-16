package com.txwx.social.dashboard.domain.mapper;

public interface IImportBaseModel {
    Object[] toObject(Long accountId);
    default void validate() {}
}
