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

import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.web.domain.TxwxPageButton;
import com.txwx.web.domain.TxwxPageButtonTemp;
import com.txwx.web.service.IPageButtonConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 页面按钮配置Controller（支持保存、预览、发布功能）
 * 
 * @author txwx
 * @date 2025-12-06
 */
@RestController
@RequestMapping("/buttonConfig")
public class PageButtonConfigController extends BaseController
{
    @Autowired
    private IPageButtonConfigService pageButtonConfigService;

    /**
     * 配置页面加载：获取发布过或者保存过的配置
     */
    @GetMapping("/load/{pageCode}")
    public AjaxResult laod(@PathVariable String pageCode)
    {
        //(1)先从临时表中查询，若临时表有数据代表已保存还未发布
        List<TxwxPageButtonTemp> list = pageButtonConfigService.previewPageButtonConfig(pageCode);
        if(list != null && list.isEmpty()){
            //(2)若临时表为空,则代表之前已发布过，则从正式表获取
            List<TxwxPageButton> listDisplay = pageButtonConfigService.displayPageButtonConfig(pageCode);
            return success(listDisplay);
        }else{
            return success(list);
        }
    }

    /**
     * 保存页面按钮配置（到临时表）
     */
    //@RequiresPermissions("website:buttonConfig:save")
    @Log(title = "页面按钮配置", businessType = BusinessType.INSERT)
    @PostMapping("/save/{pageCode}")
    public AjaxResult savePageButtonConfig(@PathVariable String pageCode, @Validated @RequestBody List<TxwxPageButtonTemp> pageButtonTempList)
    {
        // 设置创建者和创建时间
        String username = SecurityUtils.getLoginUser().getUsername();
        if (pageButtonTempList != null && !pageButtonTempList.isEmpty())
        {
            for (TxwxPageButtonTemp pageButtonTemp : pageButtonTempList)
            {
                pageButtonTemp.setSavedBy(username);
                pageButtonTemp.setPageCode(pageCode);
                pageButtonTemp.setSavedTime(DateUtils.getNowDate());
            }
        }
        
        return toAjax(pageButtonConfigService.savePageButtonConfig(pageCode, pageButtonTempList));
    }

    /**
     * 预览：获取页面按钮配置（从临时表）
     */
    @GetMapping("/preview/{pageCode}")
    public AjaxResult previewPageButtonConfig(@PathVariable String pageCode)
    {
        List<TxwxPageButtonTemp> list = pageButtonConfigService.previewPageButtonConfig(pageCode);
        return success(list);
    }

    /**
     * 展示：获取页面按钮配置（从正式表）
     */
    @GetMapping("/display/{pageCode}")
    public AjaxResult displayPageButtonConfig(@PathVariable String pageCode)
    {
        List<TxwxPageButton> list = pageButtonConfigService.displayPageButtonConfig(pageCode);
        return success(list);
    }

    // ========================= 发布操作 =========================

    /**
     * 发布页面按钮配置
     */
    //@RequiresPermissions("website:buttonConfig:publish")
    @Log(title = "页面按钮配置", businessType = BusinessType.UPDATE)
    @PostMapping("/publish/{pageCode}")
    public AjaxResult publishPageButtonConfig(@PathVariable String pageCode)
    {
        return toAjax(pageButtonConfigService.publishPageButtonConfig(pageCode));
    }
}