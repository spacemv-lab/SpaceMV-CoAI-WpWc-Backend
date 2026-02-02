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
import com.txwx.web.service.IHomepageConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 首页配置Service业务层处理
 * 
 * @author txwx
 * @date 2025-12-06
 */
@Service
public class HomepageConfigServiceImpl implements IHomepageConfigService 
{
    @Autowired
    private TxwxCarouselImageTempMapper carouselImageTempMapper;
    
    @Autowired
    private TxwxCarouselImageMapper carouselImageMapper;
    
    @Autowired
    private TxwxMainProductTempMapper mainProductTempMapper;
    
    @Autowired
    private TxwxMainProductMapper mainProductMapper;
    
    @Autowired
    private TxwxTypicalCustomerTempMapper typicalCustomerTempMapper;
    
    @Autowired
    private TxwxTypicalCustomerMapper typicalCustomerMapper;

    @Override
    public TxwxHomePage displayConfig() {
        List<TxwxCarouselImage> txwxCarouselImages = carouselImageMapper.selectCarouselImageList(null);
        List<TxwxMainProduct> txwxMainProducts = mainProductMapper.selectMainProductList(null);
        List<TxwxTypicalCustomer> txwxTypicalCustomers = typicalCustomerMapper.selectTypicalCustomerList(null);

        TxwxHomePage txwxHomePage = new TxwxHomePage();
        txwxHomePage.setIsPublish("1");
        txwxHomePage.setCarouselImageLists(txwxCarouselImages);
        txwxHomePage.setMainProducts(txwxMainProducts);
        if(txwxTypicalCustomers != null && !txwxTypicalCustomers.isEmpty()){
            txwxHomePage.setTypicalCustomer(txwxTypicalCustomers.get(0));
        }

        return txwxHomePage;
    }

    @Override
    public TxwxHomePageTemp previewConfig() {
        List<TxwxCarouselImageTemp> txwxCarouselImageTemps = carouselImageTempMapper.selectCarouselImageTempList(null);
        List<TxwxMainProductTemp> txwxMainProductTemps = mainProductTempMapper.selectMainProductTempList(null);
        List<TxwxTypicalCustomerTemp> txwxTypicalCustomerTemps = typicalCustomerTempMapper.selectTypicalCustomerTempList(null);

        TxwxHomePageTemp txwxHomePageTemp = new TxwxHomePageTemp();
        txwxHomePageTemp.setIsPublish("0");
        txwxHomePageTemp.setCarouselImageListsTemp(txwxCarouselImageTemps);
        txwxHomePageTemp.setMainProductsTemp(txwxMainProductTemps);
        if(txwxTypicalCustomerTemps != null && !txwxTypicalCustomerTemps.isEmpty()){
            txwxHomePageTemp.setTypicalCustomerTemp(txwxTypicalCustomerTemps.get(0));
        }

        return txwxHomePageTemp;
    }

    @Override
    @Transactional
    public int saveHomePageTemp(TxwxHomePageTemp txwxHomePageTemp) {
        List<TxwxCarouselImageTemp> carouselImageListsTemp = txwxHomePageTemp.getCarouselImageListsTemp();
        List<TxwxMainProductTemp> mainProductsTemp = txwxHomePageTemp.getMainProductsTemp();
        TxwxTypicalCustomerTemp typicalCustomerTemp = txwxHomePageTemp.getTypicalCustomerTemp();

        //(1)清空临时表
        carouselImageTempMapper.clearCarouselImageTemp();
        mainProductTempMapper.clearMainProductTemp();
        typicalCustomerTempMapper.clearTypicalCustomerTemp();

        String username = SecurityUtils.getLoginUser().getUsername();
        Date nowDate = DateUtils.getNowDate();

        int result = 0;
        //(2)插入数据
        if(carouselImageListsTemp != null && !carouselImageListsTemp.isEmpty()){
            for(TxwxCarouselImageTemp txwxCarouselImageTemps: carouselImageListsTemp){
                txwxCarouselImageTemps.setSavedBy(username);
                txwxCarouselImageTemps.setSavedTime(nowDate);
            }
            result += carouselImageTempMapper.batchInsertCarouselImageTemp(carouselImageListsTemp);
        }

        if(mainProductsTemp != null && !mainProductsTemp.isEmpty()){
            for(TxwxMainProductTemp txwxMainProductTemp: mainProductsTemp){
                txwxMainProductTemp.setSavedBy(username);
                txwxMainProductTemp.setSavedTime(nowDate);
            }
            result += mainProductTempMapper.batchInsertMainProductTemp(mainProductsTemp);
        }

        if(typicalCustomerTemp != null){
            typicalCustomerTemp.setSavedBy(username);
            typicalCustomerTemp.setSavedTime(nowDate);
            result += typicalCustomerTempMapper.insertTypicalCustomerTemp(typicalCustomerTemp);
        }

        return result;
    }


    @Override
    @Transactional
    public int publishHomePage() {
        int result = 0;

        // 1. 清空正式表
        carouselImageMapper.clearCarouselImage();
        mainProductMapper.clearMainProduct();
        typicalCustomerMapper.clearTypicalCustomer();

        // 2. 将临时表数据复制到正式表
        result += carouselImageTempMapper.copyTempToFormal();
        result += mainProductTempMapper.copyTempToFormal();
        result += typicalCustomerTempMapper.copyTempToFormal();

        // 4. 更新本次发布的发布人和发布时间
        String username = SecurityUtils.getLoginUser().getUsername();
        Date nowDate = DateUtils.getNowDate();
        carouselImageMapper.updateByAndTime(username, nowDate);
        mainProductMapper.updateByAndTime(username, nowDate);
        typicalCustomerMapper.updateByAndTime(username, nowDate);

        // 5. 将临时表清空
        carouselImageTempMapper.clearCarouselImageTemp();
        mainProductTempMapper.clearMainProductTemp();
        typicalCustomerTempMapper.clearTypicalCustomerTemp();

        return result;
    }
}