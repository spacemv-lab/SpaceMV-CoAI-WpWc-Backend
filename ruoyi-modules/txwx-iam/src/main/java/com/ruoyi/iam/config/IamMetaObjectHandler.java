/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MyBatis-Plus 自动填充处理器
 * <p>
 * 处理 {@code @TableField(fill = FieldFill.INSERT)} 和
 * {@code @TableField(fill = FieldFill.INSERT_UPDATE)} 注解的字段，
 * 在 INSERT / UPDATE 时自动填入当前时间。
 * <p>
 * 覆盖实体：
 * <ul>
 *   <li>{@link com.ruoyi.iam.entity.IamUser} — createTime / updateTime</li>
 *   <li>{@link com.ruoyi.iam.entity.IamUserBackupContact} — createTime / updateTime</li>
 *   <li>{@link com.ruoyi.iam.entity.IamUserChannel} — bindTime</li>
 *   <li>{@link com.ruoyi.iam.entity.IamAuthLog} — authTime</li>
 * </ul>
 *
 * @author txwx
 */
@Slf4j
@Component
public class IamMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("MetaObjectHandler insertFill start");

        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "bindTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "authTime", Date.class, new Date());

        log.debug("MetaObjectHandler insertFill completed");
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("MetaObjectHandler updateFill start");

        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());

        log.debug("MetaObjectHandler updateFill completed");
    }
}
