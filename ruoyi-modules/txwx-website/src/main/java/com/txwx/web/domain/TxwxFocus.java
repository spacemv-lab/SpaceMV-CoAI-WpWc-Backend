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

/**
 * 关注配置正式表 focus
 * 
 * @author txwx
 */
public class TxwxFocus extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long focusId;

    /** 名称 */
    @NotBlank(message = "关注名称不能为空")
    @Size(min = 0, max = 100, message = "关注名称不能超过100个字符")
    private String focusName;

    /** 图片地址 */
    @NotBlank(message = "图片地址不能为空")
    @Size(min = 0, max = 500, message = "图片地址不能超过500个字符")
    private String imageUrl;

    /** 排序序号 */
    private Integer sortOrder;

    /** 跳转链接 */
    @Size(min = 0, max = 500, message = "跳转链接不能超过500个字符")
    private String jumpUrl;

    public Long getFocusId()
    {
        return focusId;
    }

    public void setFocusId(Long focusId)
    {
        this.focusId = focusId;
    }

    public String getFocusName()
    {
        return focusName;
    }

    public void setFocusName(String focusName)
    {
        this.focusName = focusName;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
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
            .append("focusId", getFocusId())
            .append("focusName", getFocusName())
            .append("imageUrl", getImageUrl())
            .append("sortOrder", getSortOrder())
            .append("jumpUrl", getJumpUrl())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}