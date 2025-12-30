package com.txwx.web.domain;

import java.util.List;

/**
 * 首页配置实体类
 *
 * @author txwx
 */
public class TxwxHomePage {

    /** 是否发布（0-否 1-是） */
    private String isPublish;

    /**
     * @description: 轮播图列表
     * @author txwx
     * @version 1.0
     */
    private List<TxwxCarouselImage> carouselImageLists;

    /**
     * @description: 主要产品
     * @author txwx
     * @version 1.0
     */
    private List<TxwxMainProduct> mainProducts;

    /**
     * @description: 典型客户
     * @author txwx
     * @version 1.0
     */
    private TxwxTypicalCustomer typicalCustomer;

    public String getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(String isPublish) {
        this.isPublish = isPublish;
    }

    public List<TxwxCarouselImage> getCarouselImageLists() {
        return carouselImageLists;
    }

    public void setCarouselImageLists(List<TxwxCarouselImage> carouselImageLists) {
        this.carouselImageLists = carouselImageLists;
    }

    public List<TxwxMainProduct> getMainProducts() {
        return mainProducts;
    }

    public void setMainProducts(List<TxwxMainProduct> mainProducts) {
        this.mainProducts = mainProducts;
    }

    public TxwxTypicalCustomer getTypicalCustomer() {
        return typicalCustomer;
    }

    public void setTypicalCustomer(TxwxTypicalCustomer typicalCustomer) {
        this.typicalCustomer = typicalCustomer;
    }
}
