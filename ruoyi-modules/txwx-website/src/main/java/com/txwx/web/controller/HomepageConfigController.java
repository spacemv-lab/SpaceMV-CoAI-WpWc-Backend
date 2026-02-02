/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the MIT License;
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 
 * the copywrite is belongs to  SpaceMV  team
 */
package com.txwx.web.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.web.domain.*;
import com.txwx.web.service.IHomepageConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 首页配置Controller（支持保存、预览、发布功能）
 * 
 * @author txwx
 * @date 2025-12-06
 */
@RestController
@RequestMapping("/homepageConfig")
public class HomepageConfigController extends BaseController
{
    @Autowired
    private IHomepageConfigService homepageConfigService;

    /**
     * 配置页面加载：获取发布过或者保存过的配置
     */
    @GetMapping("/load")
    public AjaxResult laod()
    {
        //(1)先从临时表中查询，若临时表有数据代表已保存还未发布
        TxwxHomePageTemp txwxHomePageTemp = homepageConfigService.previewConfig();
        if(txwxHomePageTemp != null && txwxHomePageTemp.getCarouselImageListsTemp().isEmpty()
                && txwxHomePageTemp.getMainProductsTemp().isEmpty()){
            //(2)若临时表为空,则代表之前已发布过，则从正式表获取
            TxwxHomePage txwxHomePage = homepageConfigService.displayConfig();
            return success(txwxHomePage);
        }else{
            return success(txwxHomePageTemp);
        }
    }

    /**
     * 保存首页配置（到临时表）
     */
    //@RequiresPermissions("website:buttonConfig:save")
    @Log(title = "保存首页按钮配置", businessType = BusinessType.INSERT)
    @PostMapping("/save")
    public AjaxResult saveHomePageConfig(@Validated @RequestBody TxwxHomePageTemp txwxHomePageTemp)
    {
        return toAjax(homepageConfigService.saveHomePageTemp(txwxHomePageTemp));
    }

    /**
     * 预览：获取首页配置（从临时表）
     */
    @GetMapping("/preview")
    public AjaxResult previewHomePageConfig()
    {
        TxwxHomePageTemp txwxHomePageTemp = homepageConfigService.previewConfig();
        return success(txwxHomePageTemp);
    }

    /**
     * 展示：获取首页配置（从正式表）
     */
    @GetMapping("/display")
    public AjaxResult displayHomePageConfig()
    {
        TxwxHomePage txwxHomePage = homepageConfigService.displayConfig();
        return success(txwxHomePage);
    }

    // ========================= 发布操作 =========================

    /**
     * 发布页面按钮配置
     */
    //@RequiresPermissions("website:buttonConfig:publish")
    @Log(title = "发布首页配置", businessType = BusinessType.UPDATE)
    @PostMapping("/publish")
    public AjaxResult publishHomePageConfig()
    {
        return toAjax(homepageConfigService.publishHomePage());
    }
}