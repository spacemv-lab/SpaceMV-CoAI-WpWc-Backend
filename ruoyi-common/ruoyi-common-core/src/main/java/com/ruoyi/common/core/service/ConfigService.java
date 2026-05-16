/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.common.core.service;

/**
 * 参数配置服务（跨模块通用接口）
 * <p>
 * 定义在 common-core 中，避免 common 模块依赖具体业务模块（如 ruoyi-system）。
 * 由具体业务模块（如 ruoyi-system）实现，从数据库 sys_config 表或缓存中读取参数。
 *
 * @author txwx
 */
public interface ConfigService
{
    /**
     * 根据键名查询参数配置
     *
     * @param configKey 参数键名
     * @param defaultValue 未找到时的默认值
     * @return 参数键值
     */
    String selectConfigByKey(String configKey, String defaultValue);
}
