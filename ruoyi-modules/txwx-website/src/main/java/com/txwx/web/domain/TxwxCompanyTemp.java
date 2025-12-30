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
