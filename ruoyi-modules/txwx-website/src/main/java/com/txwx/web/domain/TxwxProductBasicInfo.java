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

/**
 * 产品基础信息正式表 product_basic_info
 * 
 * @author txwx
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TxwxProductBasicInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 产品类型（1-大模型 2-小装置） */
    @NotBlank(message = "产品类型不能为空")
    private String productType;

    /** 产品名称 */
    @NotBlank(message = "产品名称不能为空")
    @Size(min = 0, max = 200, message = "产品名称不能超过200个字符")
    private String productName;

    /** 产品简介 */
    @Size(min = 0, max = 1000, message = "产品简介不能超过1000个字符")
    private String productIntroduction;

    /** 背景图片地址 */
    @Size(min = 0, max = 500, message = "背景图片地址不能超过500个字符")
    private String backgroundImageUrl;

    /** 详细介绍 */
    private String detailedIntroduction;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getProductType()
    {
        return productType;
    }

    public void setProductType(String productType)
    {
        this.productType = productType;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public String getProductIntroduction()
    {
        return productIntroduction;
    }

    public void setProductIntroduction(String productIntroduction)
    {
        this.productIntroduction = productIntroduction;
    }

    public String getBackgroundImageUrl()
    {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl)
    {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public String getDetailedIntroduction()
    {
        return detailedIntroduction;
    }

    public void setDetailedIntroduction(String detailedIntroduction)
    {
        this.detailedIntroduction = detailedIntroduction;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("productType", getProductType())
            .append("productName", getProductName())
            .append("productIntroduction", getProductIntroduction())
            .append("backgroundImageUrl", getBackgroundImageUrl())
            .append("detailedIntroduction", getDetailedIntroduction())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}