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

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.web.domain.BaseEntity;

import java.util.Date;

/**
 * 页面按钮配置临时表 page_button_temp
 * 
 * @author txwx
 */
public class TxwxPageButtonTemp extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 按钮主键 */
    private Long buttonId;

    /** 页面标识 */
    private String pageCode;

    /** 按钮类型（1-文字按钮 2-图片按钮） */
    private String buttonType;

    /** 按钮文案 */
    private String buttonText;

    /** 背景色 */
    private String backgroundColor;

    /** 跳转地址 */
    private String jumpUrl;

    /** 图片URL */
    private String imageUrl;

    /** 是否对外展示（0-否 1-是） */
    private String isShow;

    /** 是否发布（0-否 1-是） */
    private String isPublish;

    /** 形态（0-nav路由 1-新窗口打开链接 2-当前窗口打开链接 3-正在开发中） */
    private String state;

    /** 排序序号 */
    private Integer sortOrder;

    public Long getButtonId()
    {
        return buttonId;
    }

    public void setButtonId(Long buttonId)
    {
        this.buttonId = buttonId;
    }

    @NotBlank(message = "页面标识不能为空")
    @Size(min = 0, max = 100, message = "页面标识不能超过100个字符")
    public String getPageCode()
    {
        return pageCode;
    }

    public void setPageCode(String pageCode)
    {
        this.pageCode = pageCode;
    }

    @NotBlank(message = "按钮类型不能为空")
    public String getButtonType()
    {
        return buttonType;
    }

    public void setButtonType(String buttonType)
    {
        this.buttonType = buttonType;
    }

    public String getButtonText()
    {
        return buttonText;
    }

    public void setButtonText(String buttonText)
    {
        this.buttonText = buttonText;
    }

    public String getBackgroundColor()
    {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }

    public String getJumpUrl()
    {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl)
    {
        this.jumpUrl = jumpUrl;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(String isPublish) {
        this.isPublish = isPublish;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("buttonId", getButtonId())
            .append("pageCode", getPageCode())
            .append("buttonType", getButtonType())
            .append("buttonText", getButtonText())
            .append("backgroundColor", getBackgroundColor())
            .append("jumpUrl", getJumpUrl())
            .append("imageUrl", getImageUrl())
            .append("isShow", getIsShow())
            .append("isPublish", getIsPublish())
            .append("state", getState())
            .append("sortOrder", getSortOrder())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("savedBy", getSavedBy())
            .append("savedTime", getSavedTime())
            .append("publishedBy", getPublishedBy())
            .append("publishedTime", getPublishedTime())
            .toString();
    }
}