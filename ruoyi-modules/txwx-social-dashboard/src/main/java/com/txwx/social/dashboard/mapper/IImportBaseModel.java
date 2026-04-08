package com.txwx.social.dashboard.mapper;

public interface IImportBaseModel {
    Object[] toObject();
    default void validate() {}
}
