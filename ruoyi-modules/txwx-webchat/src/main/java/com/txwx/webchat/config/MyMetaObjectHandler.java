package com.txwx.webchat.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ruoyi.common.core.utils.uuid.UUID;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Object uuid = metaObject.getValue("uuid");
        if (uuid == null || "".equals(uuid)) {
            strictInsertFill(metaObject, "uuid", String.class, UUID.randomUUID().toString());
        }
        setFieldValByName("createBy", SecurityUtils.getLoginUser().getUserid(), metaObject);
        setFieldValByName("createTime", new Date(), metaObject);
        setFieldValByName("createByName", SecurityUtils.getLoginUser().getUsername(), metaObject);
        setFieldValByName("modifyBy",  SecurityUtils.getLoginUser().getUserid(), metaObject);
        setFieldValByName("modifyTime", new Date(), metaObject);
        setFieldValByName("modifyByName", SecurityUtils.getLoginUser().getUsername(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        setFieldValByName("modifyBy",  SecurityUtils.getLoginUser().getUserid(), metaObject);
        setFieldValByName("modifyTime", new Date(), metaObject);
        setFieldValByName("modifyByName", SecurityUtils.getLoginUser().getUsername(), metaObject);
    }
}
