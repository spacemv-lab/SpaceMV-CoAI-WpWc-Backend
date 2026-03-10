package com.txwx.webchat.mapper;

public interface IImportBaseModel {
    Object[] toObject();
    default void validate() {}
}
