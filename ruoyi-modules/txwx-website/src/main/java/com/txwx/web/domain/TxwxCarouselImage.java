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
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 首页轮播图配置表 carousel_image
 * 
 * @author txwx
 */
public class TxwxCarouselImage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 轮播图主键 */
    private Long carouselId;

    /** 图片地址 */
    @NotBlank(message = "图片地址不能为空")
    private String imageUrl;

    /** 是否显示（0-否 1-是） */
    @NotBlank(message = "是否显示不能为空")
    private String isShow;

    /** 排序序号 */
    private Integer sortOrder;

    /** 标题 */
    private String title;

    /** 描述 */
    private String description;

    /** 跳转链接 */
    private String jumpUrl;

    public Long getCarouselId()
    {
        return carouselId;
    }

    public void setCarouselId(Long carouselId)
    {
        this.carouselId = carouselId;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public String getIsShow()
    {
        return isShow;
    }

    public void setIsShow(String isShow)
    {
        this.isShow = isShow;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getJumpUrl()
    {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl)
    {
        this.jumpUrl = jumpUrl;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("carouselId", getCarouselId())
            .append("imageUrl", getImageUrl())
            .append("isShow", getIsShow())
            .append("sortOrder", getSortOrder())
            .append("title", getTitle())
            .append("description", getDescription())
            .append("jumpUrl", getJumpUrl())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}