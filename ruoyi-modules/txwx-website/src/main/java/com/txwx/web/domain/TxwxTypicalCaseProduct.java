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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.web.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 典型案例/典型产品正式表 typical_case_product
 * 
 * @author txwx
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TxwxTypicalCaseProduct extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 案例/产品主键 */
    private Long caseProductId;

    /** 产品类型（1-大模型 2-小装置） */
    @NotBlank(message = "产品类型不能为空")
    private String productType;

    /** 案例/产品名称 */
    @NotBlank(message = "案例/产品名称不能为空")
    @Size(min = 0, max = 200, message = "案例/产品名称不能超过200个字符")
    private String productName;

    /** 案例/产品简介 */
    @Size(min = 0, max = 1000, message = "案例/产品简介不能超过1000个字符")
    private String productIntroduction;

    /** 标配功能（存储为逗号分隔的字符串，JSON中返回为数组） */
    @JsonProperty("standardFunctions")
    private List<String> standardFunctions;

    /** 定制服务（存储为逗号分隔的字符串，JSON中返回为数组） */
    @JsonProperty("customServices")
    private List<String> customServices;

    /** 图片地址 */
    @Size(min = 0, max = 500, message = "图片地址不能超过500个字符")
    private String imageUrl;

    /** 详情介绍 */
    private String detailedIntroduction;

    /** 排序序号 */
    private Integer sortOrder;

    // 用于数据库存储的辅助字段（不暴露给前端）
    private String standardFunctionsStr;
    private String customServicesStr;

    public Long getCaseProductId()
    {
        return caseProductId;
    }

    public void setCaseProductId(Long caseProductId)
    {
        this.caseProductId = caseProductId;
    }

    public String getProductType()
    {
        return productType;
    }

    public void setProductType(String productType)
    {
        this.productType = productType;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductIntroduction() {
        return productIntroduction;
    }

    public void setProductIntroduction(String productIntroduction) {
        this.productIntroduction = productIntroduction;
    }

    public List<String> getStandardFunctions()
    {
        return standardFunctions;
    }

    public List<String> getCustomServices()
    {
        return customServices;
    }

    public void setStr(){
        this.standardFunctionsStr = null;
        if (this.standardFunctions != null && !this.standardFunctions.isEmpty()) {
            this.standardFunctionsStr = String.join(",", standardFunctions);
        }

        this.customServicesStr = null;
        if (this.customServices != null && !this.customServices.isEmpty()) {
            this.customServicesStr = String.join(",", customServices);
        }
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public String getDetailedIntroduction()
    {
        return detailedIntroduction;
    }

    public void setDetailedIntroduction(String detailedIntroduction)
    {
        this.detailedIntroduction = detailedIntroduction;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    // Getter和Setter用于数据库存储字段
    public String getStandardFunctionsStr()
    {
        return standardFunctionsStr;
    }

    public String getCustomServicesStr()
    {
        return customServicesStr;
    }

    public void extractStr() {
        if(this.standardFunctionsStr != null && !this.standardFunctionsStr.isEmpty()){
            String[] parts = this.standardFunctionsStr.split(",");

            // 将数组转换为List
            List<String> listTmp = Arrays.asList(parts);
            if(listTmp != null && !listTmp.isEmpty()){
                this.standardFunctions = new ArrayList<>();
                this.standardFunctions.addAll(listTmp);
            }
        }

        if(this.customServicesStr != null && !this.customServicesStr.isEmpty()){
            String[] parts = this.customServicesStr.split(",");

            // 将数组转换为List
            List<String> listTmp = Arrays.asList(parts);
            if(listTmp != null && !listTmp.isEmpty()){
                this.customServices = new ArrayList<>();
                this.customServices.addAll(listTmp);
            }
        }
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("caseProductId", getCaseProductId())
            .append("productType", getProductType())
            .append("caseProductName", getProductName())
            .append("caseProductIntroduction", getProductIntroduction())
            .append("standardFunctionsStr", getStandardFunctionsStr())
            .append("customServicesStr", getCustomServicesStr())
            .append("imageUrl", getImageUrl())
            .append("detailedIntroduction", getDetailedIntroduction())
            .append("sortOrder", getSortOrder())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}