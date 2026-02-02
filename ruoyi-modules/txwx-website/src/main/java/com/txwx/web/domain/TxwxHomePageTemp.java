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
