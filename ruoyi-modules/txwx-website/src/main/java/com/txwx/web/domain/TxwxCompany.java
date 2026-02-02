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
 * 公司信息设置正式表
 *
 * @author txwx
 */
public class TxwxCompany {

    /** 是否发布（0-否 1-是） */
    private String isPublish;

    private TxwxCompanyInfo txwxCompanyInfo;

    private List<TxwxFocus> txwxFocus;

    private List<TxwxProductCert> txwxProductCerts;

    private TxwxCompanyProfile txwxCompanyProfile;

    public String getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(String isPublish) {
        this.isPublish = isPublish;
    }

    public TxwxCompanyInfo getTxwxCompanyInfo() {
        return txwxCompanyInfo;
    }

    public void setTxwxCompanyInfo(TxwxCompanyInfo txwxCompanyInfo) {
        this.txwxCompanyInfo = txwxCompanyInfo;
    }

    public List<TxwxFocus> getTxwxFocus() {
        return txwxFocus;
    }

    public void setTxwxFocus(List<TxwxFocus> txwxFocus) {
        this.txwxFocus = txwxFocus;
    }

    public List<TxwxProductCert> getTxwxProductCerts() {
        return txwxProductCerts;
    }

    public void setTxwxProductCerts(List<TxwxProductCert> txwxProductCerts) {
        this.txwxProductCerts = txwxProductCerts;
    }

    public TxwxCompanyProfile getTxwxCompanyProfile() {
        return txwxCompanyProfile;
    }

    public void setTxwxCompanyProfile(TxwxCompanyProfile txwxCompanyProfile) {
        this.txwxCompanyProfile = txwxCompanyProfile;
    }
}
