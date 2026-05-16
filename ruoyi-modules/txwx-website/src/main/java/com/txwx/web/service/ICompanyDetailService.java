/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.web.service;

import com.txwx.web.domain.*;

import java.util.List;

/**
 * 公司详情页配置Service接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface ICompanyDetailService 
{
    /**
     * @description: 正式环境展示首页配置
     */
    TxwxCompany displayConfig();

    /**
     * @description: 预览首页配置，从临时表中查询数据
     */
    TxwxCompanyTemp previewConfig();

    /**
     * @description: 保存首页配置到临时表
     */
    int saveCompanyTemp(TxwxCompanyTemp txwxHomePageTemp);

    /**
     * @description: 发布首页配置到正式表
     */
    int publishCompany();
}