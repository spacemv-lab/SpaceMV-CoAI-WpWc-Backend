/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.web.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 产品认证临时表 product_cert_temp
 * 
 * @author txwx
 */
public class TxwxProductCertTemp extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long certId;

    /** 产品认证名称 */
    @NotBlank(message = "产品认证名称不能为空")
    @Size(min = 0, max = 200, message = "产品认证名称不能超过200个字符")
    private String certName;

    /** 图片地址 */
    @NotBlank(message = "图片地址不能为空")
    @Size(min = 0, max = 500, message = "图片地址不能超过500个字符")
    private String imageUrl;

    /** 排序序号 */
    private Integer sortOrder;

    /** 跳转链接 */
    @Size(min = 0, max = 500, message = "跳转链接不能超过500个字符")
    private String jumpUrl;

    public Long getCertId()
    {
        return certId;
    }

    public void setCertId(Long certId)
    {
        this.certId = certId;
    }

    public String getCertName()
    {
        return certName;
    }

    public void setCertName(String certName)
    {
        this.certName = certName;
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
            .append("certId", getCertId())
            .append("certName", getCertName())
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