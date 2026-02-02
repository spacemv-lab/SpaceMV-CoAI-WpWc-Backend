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
 * 公司基本信息正式表 company_info
 * 
 * @author txwx
 */
public class TxwxCompanyInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 公司名称 */
    @NotBlank(message = "公司名称不能为空")
    @Size(min = 0, max = 200, message = "公司名称不能超过200个字符")
    private String companyName;

    /** 版权信息 */
    @Size(min = 0, max = 500, message = "版权信息不能超过500个字符")
    private String copyrightInfo;

    /** 版本号 */
    @Size(min = 0, max = 50, message = "版本号不能超过50个字符")
    private String versionNumber;

    /** 公司地址 */
    @Size(min = 0, max = 500, message = "公司地址不能超过500个字符")
    private String companyAddress;

    /** 网安备案信息 */
    @Size(min = 0, max = 100, message = "网安备案信息不能超过100个字符")
    private String securityRecord;

    /** ICP备案信息 */
    @Size(min = 0, max = 100, message = "ICP备案信息不能超过100个字符")
    private String icpRecord;

    /** 商务合作途径 */
    @Size(min = 0, max = 500, message = "商务合作途径不能超过500个字符")
    private String businessCooperation;

    /** 简历投递途径 */
    @Size(min = 0, max = 500, message = "简历投递途径不能超过500个字符")
    private String resumeDelivery;

    /** 公司Logo URL */
    @Size(min = 0, max = 500, message = "公司Logo URL不能超过500个字符")
    private String logoUrl;

    /** 公司地址图片 URL */
    @Size(min = 0, max = 500, message = "公司地址图片 URL不能超过500个字符")
    private String addrUrl;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }

    public String getCopyrightInfo()
    {
        return copyrightInfo;
    }

    public void setCopyrightInfo(String copyrightInfo)
    {
        this.copyrightInfo = copyrightInfo;
    }

    public String getVersionNumber()
    {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber)
    {
        this.versionNumber = versionNumber;
    }

    public String getCompanyAddress()
    {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress)
    {
        this.companyAddress = companyAddress;
    }

    public String getSecurityRecord()
    {
        return securityRecord;
    }

    public void setSecurityRecord(String securityRecord)
    {
        this.securityRecord = securityRecord;
    }

    public String getIcpRecord()
    {
        return icpRecord;
    }

    public void setIcpRecord(String icpRecord)
    {
        this.icpRecord = icpRecord;
    }

    public String getBusinessCooperation()
    {
        return businessCooperation;
    }

    public void setBusinessCooperation(String businessCooperation)
    {
        this.businessCooperation = businessCooperation;
    }

    public String getResumeDelivery()
    {
        return resumeDelivery;
    }

    public void setResumeDelivery(String resumeDelivery)
    {
        this.resumeDelivery = resumeDelivery;
    }

    public String getLogoUrl()
    {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl)
    {
        this.logoUrl = logoUrl;
    }

    public String getAddrUrl() {
        return addrUrl;
    }

    public void setAddrUrl(String addrUrl) {
        this.addrUrl = addrUrl;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("companyName", getCompanyName())
            .append("copyrightInfo", getCopyrightInfo())
            .append("versionNumber", getVersionNumber())
            .append("companyAddress", getCompanyAddress())
            .append("securityRecord", getSecurityRecord())
            .append("icpRecord", getIcpRecord())
            .append("businessCooperation", getBusinessCooperation())
            .append("resumeDelivery", getResumeDelivery())
            .append("logoUrl", getLogoUrl())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}