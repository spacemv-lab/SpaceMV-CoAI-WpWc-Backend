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
import com.txwx.web.domain.TxwxProduct;
import com.txwx.web.domain.TxwxProductTemp;
import com.txwx.web.service.IProductConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 产品配置Controller（支持保存、预览、发布功能）
 * 
 * @author txwx
 * @date 2025-12-06
 */
@RestController
@RequestMapping("/productConfig")
public class ProductConfigController extends BaseController
{
    @Autowired
    private IProductConfigService productConfigService;

    /**
     * 产品配置页面加载：获取发布过或者保存过的配置
     */
    @GetMapping("/load")
    public AjaxResult laod()
    {
        //(1)先从临时表中查询，若临时表有数据代表已保存还未发布
        TxwxProductTemp txwxProductTemp = productConfigService.previewConfig();
        if(txwxProductTemp != null && (txwxProductTemp.getTxwxDeviceProductBasicInfoTemp() == null ||
                txwxProductTemp.getTxwxDeviceProductBasicInfoTemp() == null)){
            //(2)若临时表为空,则代表之前已发布过，则从正式表获取
            TxwxProduct txwxProduct = productConfigService.displayConfig();
            return success(txwxProduct);
        }else{
            return success(txwxProductTemp);
        }
    }

    /**
     * 保存产品配置（到临时表）
     */
    //@RequiresPermissions("website:buttonConfig:save")
    @Log(title = "保存产品配置", businessType = BusinessType.INSERT)
    @PostMapping("/save")
    public AjaxResult saveProductDetailConfig(@Validated @RequestBody TxwxProductTemp txwxProductTemp)
    {
        return toAjax(productConfigService.saveProductTemp(txwxProductTemp));
    }

    /**
     * 预览：获取产品配置（从临时表）
     */
    @GetMapping("/preview")
    public AjaxResult previewProductDetailConfig()
    {
        TxwxProductTemp txwxProductTemp = productConfigService.previewConfig();
        return success(txwxProductTemp);
    }

    /**
     * 展示：获取产品配置（从正式表）
     */
    @GetMapping("/display")
    public AjaxResult displayProductDetailConfig()
    {
        TxwxProduct txwxProduct = productConfigService.displayConfig();
        return success(txwxProduct);
    }

    // ========================= 发布操作 =========================

    /**
     * 发布产品配置
     */
    //@RequiresPermissions("website:buttonConfig:publish")
    @Log(title = "发布产品配置", businessType = BusinessType.UPDATE)
    @PostMapping("/publish")
    public AjaxResult publishProductDetailConfig()
    {
        return toAjax(productConfigService.publishProduct());
    }
}