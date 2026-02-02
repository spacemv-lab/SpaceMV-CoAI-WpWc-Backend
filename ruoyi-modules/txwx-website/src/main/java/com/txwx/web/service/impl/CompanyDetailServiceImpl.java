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
import com.txwx.web.service.ICompanyDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 公司详情页配置Service业务层处理
 * 
 * @author txwx
 * @date 2025-12-06
 */
@Service
public class CompanyDetailServiceImpl implements ICompanyDetailService 
{
    @Autowired
    private TxwxCompanyInfoTempMapper companyInfoTempMapper;
    
    @Autowired
    private TxwxCompanyInfoMapper companyInfoMapper;
    
    @Autowired
    private TxwxFocusTempMapper focusTempMapper;
    
    @Autowired
    private TxwxFocusMapper focusMapper;
    
    @Autowired
    private TxwxProductCertTempMapper productCertTempMapper;
    
    @Autowired
    private TxwxProductCertMapper productCertMapper;
    
    @Autowired
    private TxwxCompanyProfileTempMapper companyProfileTempMapper;
    
    @Autowired
    private TxwxCompanyProfileMapper companyProfileMapper;

    @Override
    public TxwxCompany displayConfig() {
        TxwxCompanyInfo txwxCompanyInfo = companyInfoMapper.selectCompanyInfo();
        List<TxwxFocus> txwxFoci = focusMapper.selectFocusList();
        List<TxwxProductCert> txwxProductCerts = productCertMapper.selectProductCertList();
        TxwxCompanyProfile txwxCompanyProfile = companyProfileMapper.selectCompanyProfile();

        TxwxCompany txwxCompany = new TxwxCompany();
        txwxCompany.setIsPublish("1");
        txwxCompany.setTxwxCompanyInfo(txwxCompanyInfo);
        txwxCompany.setTxwxFocus(txwxFoci);
        txwxCompany.setTxwxProductCerts(txwxProductCerts);
        txwxCompany.setTxwxCompanyProfile(txwxCompanyProfile);

        return txwxCompany;
    }

    @Override
    public TxwxCompanyTemp previewConfig() {
        TxwxCompanyInfoTemp txwxCompanyInfo = companyInfoTempMapper.selectCompanyInfoTemp();
        List<TxwxFocusTemp> txwxFoci = focusTempMapper.selectFocusTempList();
        List<TxwxProductCertTemp> txwxProductCerts = productCertTempMapper.selectProductCertTempList();
        TxwxCompanyProfileTemp txwxCompanyProfile = companyProfileTempMapper.selectCompanyProfileTemp();

        TxwxCompanyTemp txwxCompanyTemp = new TxwxCompanyTemp();
        txwxCompanyTemp.setIsPublish("0");
        txwxCompanyTemp.setTxwxCompanyInfoTemp(txwxCompanyInfo);
        txwxCompanyTemp.setTxwxFocusTemp(txwxFoci);
        txwxCompanyTemp.setTxwxProductCertsTemp(txwxProductCerts);
        txwxCompanyTemp.setTxwxCompanyProfileTemp(txwxCompanyProfile);

        return txwxCompanyTemp;
    }

    @Override
    @Transactional
    public int saveCompanyTemp(TxwxCompanyTemp txwxCompanyTemp) {
        TxwxCompanyInfoTemp txwxCompanyInfoTemp = txwxCompanyTemp.getTxwxCompanyInfoTemp();
        List<TxwxFocusTemp> txwxFocusTemp = txwxCompanyTemp.getTxwxFocusTemp();
        List<TxwxProductCertTemp> txwxProductCertsTemp = txwxCompanyTemp.getTxwxProductCertsTemp();
        TxwxCompanyProfileTemp txwxCompanyProfileTemp = txwxCompanyTemp.getTxwxCompanyProfileTemp();

        //(1)清空临时表
        companyInfoTempMapper.clearCompanyInfoTemp();
        focusTempMapper.clearFocusTemp();
        productCertTempMapper.clearProductCertTemp();
        companyProfileTempMapper.clearCompanyProfileTemp();

        String username = SecurityUtils.getLoginUser().getUsername();
        Date nowDate = DateUtils.getNowDate();

        int result = 0;
        //(2)插入数据
        if(txwxCompanyInfoTemp != null){
            txwxCompanyInfoTemp.setSavedBy(username);
            txwxCompanyInfoTemp.setSavedTime(nowDate);
            result += companyInfoTempMapper.insertCompanyInfoTemp(txwxCompanyInfoTemp);
        }

        if(txwxFocusTemp != null && !txwxFocusTemp.isEmpty()){
            for(TxwxFocusTemp txwxFocusTemp1: txwxFocusTemp){
                txwxFocusTemp1.setSavedBy(username);
                txwxFocusTemp1.setSavedTime(nowDate);
            }
            result += focusTempMapper.batchInsertFocusTemp(txwxFocusTemp);
        }

        if(txwxProductCertsTemp != null && !txwxProductCertsTemp.isEmpty()){
            for(TxwxProductCertTemp txwxProductCertTemp: txwxProductCertsTemp){
                txwxProductCertTemp.setSavedBy(username);
                txwxProductCertTemp.setSavedTime(nowDate);
            }
            result += productCertTempMapper.batchInsertProductCertTemp(txwxProductCertsTemp);
        }

        if(txwxCompanyProfileTemp != null){
            txwxCompanyProfileTemp.setSavedBy(username);
            txwxCompanyProfileTemp.setSavedTime(nowDate);
            result += companyProfileTempMapper.insertCompanyProfileTemp(txwxCompanyProfileTemp);
        }

        return result;
    }

    @Override
    @Transactional
    public int publishCompany() {
        int result = 0;

        // 1. 清空正式表
        companyInfoMapper.clearCompanyInfo();
        focusMapper.clearFocus();
        productCertMapper.clearProductCert();
        companyProfileMapper.clearCompanyProfile();

        // 2. 将临时表数据复制到正式表
        result += companyInfoTempMapper.copyTempToFormal();
        result += focusTempMapper.copyTempToFormal();
        result += productCertTempMapper.copyTempToFormal();
        result += companyProfileTempMapper.copyTempToFormal();

        // 4. 更新本次发布的发布人和发布时间
        String username = SecurityUtils.getLoginUser().getUsername();
        Date nowDate = DateUtils.getNowDate();
        companyInfoMapper.updateByAndTime(username, nowDate);
        focusMapper.updateByAndTime(username, nowDate);
        productCertMapper.updateByAndTime(username, nowDate);
        companyProfileMapper.updateByAndTime(username, nowDate);

        // 5. 将临时表清空
        companyInfoTempMapper.clearCompanyInfoTemp();
        focusTempMapper.clearFocusTemp();
        productCertTempMapper.clearProductCertTemp();
        companyProfileTempMapper.clearCompanyProfileTemp();

        return result;
    }
}