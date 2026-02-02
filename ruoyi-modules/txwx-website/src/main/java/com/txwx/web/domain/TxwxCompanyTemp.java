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
 * 公司信息设置临时表
 *
 * @author txwx
 */
public class TxwxCompanyTemp {
    /** 是否发布（0-否 1-是） */
    private String isPublish;

    private TxwxCompanyInfoTemp txwxCompanyInfoTemp;

    private List<TxwxFocusTemp> txwxFocusTemp;

    private List<TxwxProductCertTemp> txwxProductCertsTemp;

    private TxwxCompanyProfileTemp txwxCompanyProfileTemp;

    public String getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(String isPublish) {
        this.isPublish = isPublish;
    }

    public TxwxCompanyInfoTemp getTxwxCompanyInfoTemp() {
        return txwxCompanyInfoTemp;
    }

    public void setTxwxCompanyInfoTemp(TxwxCompanyInfoTemp txwxCompanyInfoTemp) {
        this.txwxCompanyInfoTemp = txwxCompanyInfoTemp;
    }

    public List<TxwxFocusTemp> getTxwxFocusTemp() {
        return txwxFocusTemp;
    }

    public void setTxwxFocusTemp(List<TxwxFocusTemp> txwxFocusTemp) {
        this.txwxFocusTemp = txwxFocusTemp;
    }

    public List<TxwxProductCertTemp> getTxwxProductCertsTemp() {
        return txwxProductCertsTemp;
    }

    public void setTxwxProductCertsTemp(List<TxwxProductCertTemp> txwxProductCertsTemp) {
        this.txwxProductCertsTemp = txwxProductCertsTemp;
    }

    public TxwxCompanyProfileTemp getTxwxCompanyProfileTemp() {
        return txwxCompanyProfileTemp;
    }

    public void setTxwxCompanyProfileTemp(TxwxCompanyProfileTemp txwxCompanyProfileTemp) {
        this.txwxCompanyProfileTemp = txwxCompanyProfileTemp;
    }
}
