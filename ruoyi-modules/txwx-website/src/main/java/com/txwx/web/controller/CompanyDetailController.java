/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.web.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.txwx.web.domain.TxwxCompany;
import com.txwx.web.domain.TxwxCompanyTemp;
import com.txwx.web.service.ICompanyDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 公司详情页配置Controller
 * 
 * @author txwx
 * @date 2025-12-06
 */
@RestController
@RequestMapping("/companyDetail")
public class CompanyDetailController extends BaseController
{
    @Autowired
    private ICompanyDetailService companyDetailService;


    /**
     * 公司详情页配置页面加载：获取发布过或者保存过的配置
     */
    @GetMapping("/load")
    public AjaxResult laod()
    {
        //(1)先从临时表中查询，若临时表有数据代表已保存还未发布
        TxwxCompanyTemp txwxCompanyTemp = companyDetailService.previewConfig();
        if(txwxCompanyTemp != null && txwxCompanyTemp.getTxwxCompanyInfoTemp() == null
                && txwxCompanyTemp.getTxwxFocusTemp().isEmpty() && txwxCompanyTemp.getTxwxProductCertsTemp().isEmpty()){
            //(2)若临时表为空,则代表之前已发布过，则从正式表获取
            TxwxCompany txwxCompany = companyDetailService.displayConfig();
            return success(txwxCompany);
        }else{
            return success(txwxCompanyTemp);
        }
    }

    /**
     * 保存公司详情页配置（到临时表）
     */
    //@RequiresPermissions("website:buttonConfig:save")
    @Log(title = "保存公司详情页配置", businessType = BusinessType.INSERT)
    @PostMapping("/save")
    public AjaxResult saveCompanyDetailConfig(@Validated @RequestBody TxwxCompanyTemp txwxCompanyTemp)
    {
        return toAjax(companyDetailService.saveCompanyTemp(txwxCompanyTemp));
    }

    /**
     * 预览：获取公司详情页配置（从临时表）
     */
    @GetMapping("/preview")
    public AjaxResult previewCompanyDetailConfig()
    {
        TxwxCompanyTemp txwxCompanyTemp = companyDetailService.previewConfig();
        return success(txwxCompanyTemp);
    }

    /**
     * 展示：获取公司详情页配置（从正式表）
     */
    @GetMapping("/display")
    public AjaxResult displayCompanyDetailConfig()
    {
        TxwxCompany txwxCompany = companyDetailService.displayConfig();
        return success(txwxCompany);
    }

    // ========================= 发布操作 =========================

    /**
     * 发布公司详情页配置
     */
    //@RequiresPermissions("website:buttonConfig:publish")
    @Log(title = "发布公司详情页配置", businessType = BusinessType.UPDATE)
    @PostMapping("/publish")
    public AjaxResult publishCompanyDetailConfig()
    {
        return toAjax(companyDetailService.publishCompany());
    }
}