package com.txwx.web.domain;

import java.util.List;

/**
 * 首页配置实体类——对应临时表
 *
 * @author txwx
 */
public class TxwxHomePageTemp {

    /** 是否发布（0-否 1-是） */
    private String isPublish;

    /**
     * @description: 轮播图列表
     * @author txwx
     * @version 1.0
     */
    private List<TxwxCarouselImageTemp> carouselImageListsTemp;

    /**
     * @description: 主要产品
     * @author txwx
     * @version 1.0
     */
    private List<TxwxMainProductTemp> mainProductsTemp;

    /**
     * @description: 典型客户
     * @author txwx
     * @version 1.0
     */
    private TxwxTypicalCustomerTemp typicalCustomerTemp;

    public List<TxwxCarouselImageTemp> getCarouselImageListsTemp() {
        return carouselImageListsTemp;
    }

    public String getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(String isPublish) {
        this.isPublish = isPublish;
    }

    public void setCarouselImageListsTemp(List<TxwxCarouselImageTemp> carouselImageListsTemp) {
        this.carouselImageListsTemp = carouselImageListsTemp;
    }

    public List<TxwxMainProductTemp> getMainProductsTemp() {
        return mainProductsTemp;
    }

    public void setMainProductsTemp(List<TxwxMainProductTemp> mainProductsTemp) {
        this.mainProductsTemp = mainProductsTemp;
    }

    public TxwxTypicalCustomerTemp getTypicalCustomerTemp() {
        return typicalCustomerTemp;
    }

    public void setTypicalCustomerTemp(TxwxTypicalCustomerTemp typicalCustomerTemp) {
        this.typicalCustomerTemp = typicalCustomerTemp;
    }
}
