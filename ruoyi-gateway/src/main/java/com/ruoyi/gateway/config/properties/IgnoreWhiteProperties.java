/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.gateway.config.properties;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 放行白名单配置
 * 
 * @author ruoyi
 */
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "security.ignore")
public class IgnoreWhiteProperties
{
    /**
     * 放行白名单配置（ant 模式，支持 ? * **）
     */
    private List<String> whites = new ArrayList<>();

    /**
     * 按模块前缀放行（如 /txwx-iam，匹配该前缀下所有路径）
     * 比 whites 更宽松：只做 startsWith 匹配，无需写完整 ant 模式
     */
    private List<String> modules = new ArrayList<>();

    public List<String> getWhites()
    {
        return whites;
    }

    public void setWhites(List<String> whites)
    {
        this.whites = whites;
    }

    public List<String> getModules()
    {
        return modules;
    }

    public void setModules(List<String> modules)
    {
        this.modules = modules;
    }
}
