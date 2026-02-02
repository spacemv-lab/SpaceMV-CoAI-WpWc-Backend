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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 公司简介正式表 company_profile
 * 
 * @author txwx
 */
public class TxwxCompanyProfile extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 公司简介一（富文本） */
    private String profileOne;

    /** 公司简介二（富文本） */
    private String profileTwo;

    /** 公司简介三（富文本） */
    private String profileThree;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getProfileOne()
    {
        return profileOne;
    }

    public void setProfileOne(String profileOne)
    {
        this.profileOne = profileOne;
    }

    public String getProfileTwo()
    {
        return profileTwo;
    }

    public void setProfileTwo(String profileTwo)
    {
        this.profileTwo = profileTwo;
    }

    public String getProfileThree()
    {
        return profileThree;
    }

    public void setProfileThree(String profileThree)
    {
        this.profileThree = profileThree;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("profileOne", getProfileOne())
            .append("profileTwo", getProfileTwo())
            .append("profileThree", getProfileThree())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}