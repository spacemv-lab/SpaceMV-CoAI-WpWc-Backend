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
 * 产品设置临时表
 *
 * @author txwx
 */
public class TxwxProductTemp {
    /** 是否发布（0-否 1-是） */
    private String isPublish;

    private TxwxProductBasicInfoTemp txwxLLMProductBasicInfoTemp;

    private TxwxProductBasicInfoTemp txwxDeviceProductBasicInfoTemp;

    private List<TxwxApplicationScenarioTemp> txwxLLMApplicationScenariosTemp;

    private List<TxwxApplicationScenarioTemp> txwxDeviceApplicationScenariosTemp;

    private List<TxwxTypicalCaseProductTemp> txwxLLMTypicalCaseProductsTemp;

    private List<TxwxTypicalCaseProductTemp> txwxDeviceTypicalCaseProductsTemp;

    public String getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(String isPublish) {
        this.isPublish = isPublish;
    }

    public TxwxProductBasicInfoTemp getTxwxLLMProductBasicInfoTemp() {
        return txwxLLMProductBasicInfoTemp;
    }

    public void setTxwxLLMProductBasicInfoTemp(TxwxProductBasicInfoTemp txwxLLMProductBasicInfoTemp) {
        this.txwxLLMProductBasicInfoTemp = txwxLLMProductBasicInfoTemp;
    }

    public TxwxProductBasicInfoTemp getTxwxDeviceProductBasicInfoTemp() {
        return txwxDeviceProductBasicInfoTemp;
    }

    public void setTxwxDeviceProductBasicInfoTemp(TxwxProductBasicInfoTemp txwxDeviceProductBasicInfoTemp) {
        this.txwxDeviceProductBasicInfoTemp = txwxDeviceProductBasicInfoTemp;
    }

    public List<TxwxApplicationScenarioTemp> getTxwxLLMApplicationScenariosTemp() {
        return txwxLLMApplicationScenariosTemp;
    }

    public void setTxwxLLMApplicationScenariosTemp(List<TxwxApplicationScenarioTemp> txwxLLMApplicationScenariosTemp) {
        this.txwxLLMApplicationScenariosTemp = txwxLLMApplicationScenariosTemp;
    }

    public List<TxwxApplicationScenarioTemp> getTxwxDeviceApplicationScenariosTemp() {
        return txwxDeviceApplicationScenariosTemp;
    }

    public void setTxwxDeviceApplicationScenariosTemp(List<TxwxApplicationScenarioTemp> txwxDeviceApplicationScenariosTemp) {
        this.txwxDeviceApplicationScenariosTemp = txwxDeviceApplicationScenariosTemp;
    }

    public List<TxwxTypicalCaseProductTemp> getTxwxLLMTypicalCaseProductsTemp() {
        return txwxLLMTypicalCaseProductsTemp;
    }

    public void setTxwxLLMTypicalCaseProductsTemp(List<TxwxTypicalCaseProductTemp> txwxLLMTypicalCaseProductsTemp) {
        this.txwxLLMTypicalCaseProductsTemp = txwxLLMTypicalCaseProductsTemp;
    }

    public List<TxwxTypicalCaseProductTemp> getTxwxDeviceTypicalCaseProductsTemp() {
        return txwxDeviceTypicalCaseProductsTemp;
    }

    public void setTxwxDeviceTypicalCaseProductsTemp(List<TxwxTypicalCaseProductTemp> txwxDeviceTypicalCaseProductsTemp) {
        this.txwxDeviceTypicalCaseProductsTemp = txwxDeviceTypicalCaseProductsTemp;
    }
}
