package com.txwx.web.service;

import com.txwx.web.domain.*;

import java.util.List;

/**
 * 首页配置Service接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface IHomepageConfigService 
{
    /**
     * @description: 正式环境展示首页配置
     */
    TxwxHomePage displayConfig();

    /**
     * @description: 预览首页配置，从临时表中查询数据
     */
    TxwxHomePageTemp previewConfig();

    /**
     * @description: 保存首页配置到临时表
     */
    int saveHomePageTemp(TxwxHomePageTemp txwxHomePageTemp);

    /**
     * @description: 发布首页配置到正式表
     */
    int publishHomePage();
}