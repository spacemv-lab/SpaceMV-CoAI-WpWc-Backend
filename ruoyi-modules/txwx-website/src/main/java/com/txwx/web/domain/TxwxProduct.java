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
package com.txwx.web.domain;

import java.util.List;

/**
 * 产品设置正式表
 *
 * @author txwx
 */
public class TxwxProduct {

    /** 是否发布（0-否 1-是） */
    private String isPublish;

    private TxwxProductBasicInfo txwxLLMProductBasicInfo;

    private TxwxProductBasicInfo txwxDeviceProductBasicInfo;

    private List<TxwxApplicationScenario> txwxLLMApplicationScenarios;

    private List<TxwxApplicationScenario> txwxDeviceApplicationScenarios;

    private List<TxwxTypicalCaseProduct> txwxLLMTypicalCaseProducts;

    private List<TxwxTypicalCaseProduct> txwxDeviceTypicalCaseProducts;

    public String getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(String isPublish) {
        this.isPublish = isPublish;
    }

    public TxwxProductBasicInfo getTxwxLLMProductBasicInfo() {
        return txwxLLMProductBasicInfo;
    }

    public void setTxwxLLMProductBasicInfo(TxwxProductBasicInfo txwxLLMProductBasicInfo) {
        this.txwxLLMProductBasicInfo = txwxLLMProductBasicInfo;
    }

    public TxwxProductBasicInfo getTxwxDeviceProductBasicInfo() {
        return txwxDeviceProductBasicInfo;
    }

    public void setTxwxDeviceProductBasicInfo(TxwxProductBasicInfo txwxDeviceProductBasicInfo) {
        this.txwxDeviceProductBasicInfo = txwxDeviceProductBasicInfo;
    }

    public List<TxwxApplicationScenario> getTxwxLLMApplicationScenarios() {
        return txwxLLMApplicationScenarios;
    }

    public void setTxwxLLMApplicationScenarios(List<TxwxApplicationScenario> txwxLLMApplicationScenarios) {
        this.txwxLLMApplicationScenarios = txwxLLMApplicationScenarios;
    }

    public List<TxwxApplicationScenario> getTxwxDeviceApplicationScenarios() {
        return txwxDeviceApplicationScenarios;
    }

    public void setTxwxDeviceApplicationScenarios(List<TxwxApplicationScenario> txwxDeviceApplicationScenarios) {
        this.txwxDeviceApplicationScenarios = txwxDeviceApplicationScenarios;
    }

    public List<TxwxTypicalCaseProduct> getTxwxLLMTypicalCaseProducts() {
        return txwxLLMTypicalCaseProducts;
    }

    public void setTxwxLLMTypicalCaseProducts(List<TxwxTypicalCaseProduct> txwxLLMTypicalCaseProducts) {
        this.txwxLLMTypicalCaseProducts = txwxLLMTypicalCaseProducts;
    }

    public List<TxwxTypicalCaseProduct> getTxwxDeviceTypicalCaseProducts() {
        return txwxDeviceTypicalCaseProducts;
    }

    public void setTxwxDeviceTypicalCaseProducts(List<TxwxTypicalCaseProduct> txwxDeviceTypicalCaseProducts) {
        this.txwxDeviceTypicalCaseProducts = txwxDeviceTypicalCaseProducts;
    }
}
