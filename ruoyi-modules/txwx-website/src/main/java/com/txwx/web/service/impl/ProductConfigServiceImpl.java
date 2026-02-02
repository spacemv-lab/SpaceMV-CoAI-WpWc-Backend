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
package com.txwx.web.service.impl;

import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.web.domain.*;
import com.txwx.web.mapper.*;
import com.txwx.web.service.IProductConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 产品配置Service业务层处理
 * 
 * @author txwx
 * @date 2025-12-06
 */
@Service
public class ProductConfigServiceImpl implements IProductConfigService 
{
    @Autowired
    private TxwxProductBasicInfoTempMapper productBasicInfoTempMapper;
    
    @Autowired
    private TxwxProductBasicInfoMapper productBasicInfoMapper;
    
    @Autowired
    private TxwxApplicationScenarioTempMapper applicationScenarioTempMapper;
    
    @Autowired
    private TxwxApplicationScenarioMapper applicationScenarioMapper;
    
    @Autowired
    private TxwxTypicalCaseProductTempMapper typicalCaseProductTempMapper;
    
    @Autowired
    private TxwxTypicalCaseProductMapper typicalCaseProductMapper;

    // ========================= 保存操作 =========================

    @Override
    public TxwxProduct displayConfig() {
        TxwxProductBasicInfo llmProductBasicInfo = productBasicInfoMapper.selectProductBasicInfoByProductType("1");
        TxwxProductBasicInfo deviceProductBasicInfo = productBasicInfoMapper.selectProductBasicInfoByProductType("2");
        List<TxwxApplicationScenario> llmApplicationScenarios = applicationScenarioMapper.selectApplicationScenarioListByProductType("1");
        List<TxwxApplicationScenario> deviceApplicationScenarios = applicationScenarioMapper.selectApplicationScenarioListByProductType("2");
        List<TxwxTypicalCaseProduct> llmTypicalCaseProducts = typicalCaseProductMapper.selectTypicalCaseProductListByProductType("1");
        List<TxwxTypicalCaseProduct> deviceTypicalCaseProducts = typicalCaseProductMapper.selectTypicalCaseProductListByProductType("2");


        TxwxProduct txwxProduct = new TxwxProduct();
        txwxProduct.setIsPublish("1");

        if(llmProductBasicInfo != null){
            txwxProduct.setTxwxLLMProductBasicInfo(llmProductBasicInfo);
        }

        if(deviceProductBasicInfo != null){
            txwxProduct.setTxwxDeviceProductBasicInfo(deviceProductBasicInfo);
        }

        if(llmApplicationScenarios != null && !llmApplicationScenarios.isEmpty()){
            txwxProduct.setTxwxLLMApplicationScenarios(llmApplicationScenarios);
        }

        if(deviceApplicationScenarios != null && !deviceApplicationScenarios.isEmpty()){
            txwxProduct.setTxwxDeviceApplicationScenarios(deviceApplicationScenarios);
        }

        if(llmTypicalCaseProducts != null && !llmTypicalCaseProducts.isEmpty()){
            for(TxwxTypicalCaseProduct typicalCaseProduct : llmTypicalCaseProducts){
                typicalCaseProduct.extractStr();
            }
            txwxProduct.setTxwxLLMTypicalCaseProducts(llmTypicalCaseProducts);
        }

        if(deviceTypicalCaseProducts != null && !deviceTypicalCaseProducts.isEmpty()){
            for(TxwxTypicalCaseProduct typicalCaseProduct : deviceTypicalCaseProducts){
                typicalCaseProduct.extractStr();
            }
            txwxProduct.setTxwxDeviceTypicalCaseProducts(deviceTypicalCaseProducts);
        }

        return txwxProduct;
    }

    @Override
    public TxwxProductTemp previewConfig() {
        TxwxProductBasicInfoTemp llmProductBasicInfoTemp = productBasicInfoTempMapper.selectProductBasicInfoTempByProductType("1");
        TxwxProductBasicInfoTemp deviceProductBasicInfoTemp = productBasicInfoTempMapper.selectProductBasicInfoTempByProductType("2");
        List<TxwxApplicationScenarioTemp> llmApplicationScenarioTemps = applicationScenarioTempMapper.selectApplicationScenarioTempListByProductType("1");
        List<TxwxApplicationScenarioTemp> deviceApplicationScenarioTemps = applicationScenarioTempMapper.selectApplicationScenarioTempListByProductType("2");
        List<TxwxTypicalCaseProductTemp> llmTypicalCaseProductTemps = typicalCaseProductTempMapper.selectTypicalCaseProductTempListByProductType("1");
        List<TxwxTypicalCaseProductTemp> deviceTypicalCaseProductTemps = typicalCaseProductTempMapper.selectTypicalCaseProductTempListByProductType("2");

        TxwxProductTemp txwxProductTemp = new TxwxProductTemp();
        txwxProductTemp.setIsPublish("0");

        if(llmProductBasicInfoTemp != null){
            txwxProductTemp.setTxwxLLMProductBasicInfoTemp(llmProductBasicInfoTemp);
        }

        if(deviceProductBasicInfoTemp != null){
            txwxProductTemp.setTxwxDeviceProductBasicInfoTemp(deviceProductBasicInfoTemp);
        }

        if(llmApplicationScenarioTemps != null && !llmApplicationScenarioTemps.isEmpty()){
            txwxProductTemp.setTxwxLLMApplicationScenariosTemp(llmApplicationScenarioTemps);
        }

        if(deviceApplicationScenarioTemps != null && !deviceApplicationScenarioTemps.isEmpty()){
            txwxProductTemp.setTxwxDeviceApplicationScenariosTemp(deviceApplicationScenarioTemps);
        }

        if(llmTypicalCaseProductTemps != null && !llmTypicalCaseProductTemps.isEmpty()){
            for(TxwxTypicalCaseProductTemp typicalCaseProductTemp : llmTypicalCaseProductTemps){
                typicalCaseProductTemp.extractStr();
            }
            txwxProductTemp.setTxwxLLMTypicalCaseProductsTemp(llmTypicalCaseProductTemps);
        }

        if(deviceTypicalCaseProductTemps != null && !deviceTypicalCaseProductTemps.isEmpty()){
            for(TxwxTypicalCaseProductTemp typicalCaseProductTemp : deviceTypicalCaseProductTemps){
                typicalCaseProductTemp.extractStr();
            }
            txwxProductTemp.setTxwxDeviceTypicalCaseProductsTemp(deviceTypicalCaseProductTemps);
        }

        return txwxProductTemp;
    }

    @Override
    public int saveProductTemp(TxwxProductTemp txwxProductTemp) {
        TxwxProductBasicInfoTemp txwxLLMProductBasicInfoTemp = txwxProductTemp.getTxwxLLMProductBasicInfoTemp();
        TxwxProductBasicInfoTemp txwxDeviceProductBasicInfoTemp = txwxProductTemp.getTxwxDeviceProductBasicInfoTemp();
        List<TxwxApplicationScenarioTemp> txwxLLMApplicationScenariosTemp = txwxProductTemp.getTxwxLLMApplicationScenariosTemp();
        List<TxwxApplicationScenarioTemp> txwxDeviceApplicationScenariosTemp = txwxProductTemp.getTxwxDeviceApplicationScenariosTemp();
        List<TxwxTypicalCaseProductTemp> txwxLLMTypicalCaseProductsTemp = txwxProductTemp.getTxwxLLMTypicalCaseProductsTemp();
        List<TxwxTypicalCaseProductTemp> txwxDeviceTypicalCaseProductsTemp = txwxProductTemp.getTxwxDeviceTypicalCaseProductsTemp();

        //(1)清空临时表
        productBasicInfoTempMapper.clearProductBasicInfoTemp();
        applicationScenarioTempMapper.clearApplicationScenarioTemp();
        typicalCaseProductTempMapper.clearTypicalCaseProductTemp();

        String username = SecurityUtils.getLoginUser().getUsername();
        Date nowDate = DateUtils.getNowDate();

        int result = 0;
        //(2)插入数据
        if(txwxLLMProductBasicInfoTemp != null){
            txwxLLMProductBasicInfoTemp.setSavedBy(username);
            txwxLLMProductBasicInfoTemp.setSavedTime(nowDate);
            result += productBasicInfoTempMapper.insertProductBasicInfoTemp(txwxLLMProductBasicInfoTemp);
        }

        if(txwxDeviceProductBasicInfoTemp != null){
            txwxDeviceProductBasicInfoTemp.setSavedBy(username);
            txwxDeviceProductBasicInfoTemp.setSavedTime(nowDate);
            result += productBasicInfoTempMapper.insertProductBasicInfoTemp(txwxDeviceProductBasicInfoTemp);
        }

        if(txwxLLMApplicationScenariosTemp != null && !txwxLLMApplicationScenariosTemp.isEmpty()){
            for(TxwxApplicationScenarioTemp txwxApplicationScenarioTemp: txwxLLMApplicationScenariosTemp){
                txwxApplicationScenarioTemp.setSavedBy(username);
                txwxApplicationScenarioTemp.setSavedTime(nowDate);
            }
            result += applicationScenarioTempMapper.batchInsertApplicationScenarioTemp(txwxLLMApplicationScenariosTemp);
        }

        if(txwxDeviceApplicationScenariosTemp != null && !txwxDeviceApplicationScenariosTemp.isEmpty()){
            for(TxwxApplicationScenarioTemp txwxApplicationScenarioTemp: txwxDeviceApplicationScenariosTemp){
                txwxApplicationScenarioTemp.setSavedBy(username);
                txwxApplicationScenarioTemp.setSavedTime(nowDate);
            }
            result += applicationScenarioTempMapper.batchInsertApplicationScenarioTemp(txwxDeviceApplicationScenariosTemp);
        }

        if(txwxLLMTypicalCaseProductsTemp != null && !txwxLLMTypicalCaseProductsTemp.isEmpty()){
            for(TxwxTypicalCaseProductTemp txwxTypicalCaseProductTemp: txwxLLMTypicalCaseProductsTemp){
                txwxTypicalCaseProductTemp.setSavedBy(username);
                txwxTypicalCaseProductTemp.setSavedTime(nowDate);
                txwxTypicalCaseProductTemp.setStr();
            }
            result += typicalCaseProductTempMapper.batchInsertTypicalCaseProductTemp(txwxLLMTypicalCaseProductsTemp);
        }

        if(txwxDeviceTypicalCaseProductsTemp != null && !txwxDeviceTypicalCaseProductsTemp.isEmpty()){
            for(TxwxTypicalCaseProductTemp txwxTypicalCaseProductTemp: txwxDeviceTypicalCaseProductsTemp){
                txwxTypicalCaseProductTemp.setSavedBy(username);
                txwxTypicalCaseProductTemp.setSavedTime(nowDate);
                txwxTypicalCaseProductTemp.setStr();
            }
            result += typicalCaseProductTempMapper.batchInsertTypicalCaseProductTemp(txwxDeviceTypicalCaseProductsTemp);
        }

        return result;
    }

    @Override
    public int publishProduct() {
        int result = 0;

        // 1. 清空正式表
        productBasicInfoMapper.clearProductBasicInfo();
        applicationScenarioMapper.clearApplicationScenario();
        typicalCaseProductMapper.clearTypicalCaseProduct();

        // 2. 将临时表数据复制到正式表
        result += productBasicInfoTempMapper.copyTempToFormal();
        result += applicationScenarioTempMapper.copyTempToFormal();
        result += typicalCaseProductTempMapper.copyTempToFormal();

        // 4. 更新本次发布的发布人和发布时间
        String username = SecurityUtils.getLoginUser().getUsername();
        Date nowDate = DateUtils.getNowDate();
        productBasicInfoMapper.updateByAndTime(username, nowDate);
        applicationScenarioMapper.updateByAndTime(username, nowDate);
        typicalCaseProductMapper.updateByAndTime(username, nowDate);

        // 5. 将临时表清空
        productBasicInfoTempMapper.clearProductBasicInfoTemp();
        applicationScenarioTempMapper.clearApplicationScenarioTemp();
        typicalCaseProductTempMapper.clearTypicalCaseProductTemp();

        return result;
    }
}