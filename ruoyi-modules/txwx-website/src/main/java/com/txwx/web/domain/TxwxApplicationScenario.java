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

/**
 * 应用场景正式表 application_scenario
 * 
 * @author txwx
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TxwxApplicationScenario extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 应用场景主键 */
    private Long scenarioId;

    /** 产品类型（1-大模型 2-小装置） */
    @NotBlank(message = "产品类型不能为空")
    private String productType;

    /** 应用场景名称 */
    @NotBlank(message = "应用场景名称不能为空")
    @Size(min = 0, max = 200, message = "应用场景名称不能超过200个字符")
    private String scenarioName;

    /** 应用场景简介 */
    @Size(min = 0, max = 1000, message = "应用场景简介不能超过1000个字符")
    private String scenarioIntroduction;

    /** 图标地址 */
    @Size(min = 0, max = 500, message = "图标地址不能超过500个字符")
    private String iconUrl;

    /** 背景图片地址 */
    @Size(min = 0, max = 500, message = "背景图片地址不能超过500个字符")
    private String backgroundImageUrl;

    /** 详情介绍 */
    private String detailedIntroduction;

    /** 排序序号 */
    private Integer sortOrder;

    public Long getScenarioId()
    {
        return scenarioId;
    }

    public void setScenarioId(Long scenarioId)
    {
        this.scenarioId = scenarioId;
    }

    public String getProductType()
    {
        return productType;
    }

    public void setProductType(String productType)
    {
        this.productType = productType;
    }

    public String getScenarioName()
    {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName)
    {
        this.scenarioName = scenarioName;
    }

    public String getScenarioIntroduction()
    {
        return scenarioIntroduction;
    }

    public void setScenarioIntroduction(String scenarioIntroduction)
    {
        this.scenarioIntroduction = scenarioIntroduction;
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl)
    {
        this.iconUrl = iconUrl;
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

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("scenarioId", getScenarioId())
            .append("productType", getProductType())
            .append("scenarioName", getScenarioName())
            .append("scenarioIntroduction", getScenarioIntroduction())
            .append("iconUrl", getIconUrl())
            .append("backgroundImageUrl", getBackgroundImageUrl())
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