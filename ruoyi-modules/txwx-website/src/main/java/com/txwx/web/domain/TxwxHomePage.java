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
