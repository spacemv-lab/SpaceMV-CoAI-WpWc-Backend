package com.txwx.web.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 下拉按钮 DropButton
 *
 * @author txwx
 */
public class DropButton extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 下拉按钮主键 */
    private Long dropButtonId;

    /** 按钮主键（关联txwx_page_button或txwx_page_button_temp） */
    private Long buttonId;

    /** 按钮文案 */
    private String text;

    /** 跳转地址 */
    private String link;

    /** 形态（1-complete 新窗口打开链接；2-completeSelf 当前窗口打开链接；3-completeNav nav路由；4-developing 正在开发中） */
    private String state;

    /** 排序序号 */
    private Integer sortOrder;

    public Long getDropButtonId()
    {
        return dropButtonId;
    }

    public void setDropButtonId(Long dropButtonId)
    {
        this.dropButtonId = dropButtonId;
    }

    public Long getButtonId()
    {
        return buttonId;
    }

    public void setButtonId(Long buttonId)
    {
        this.buttonId = buttonId;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getLink()
    {
        return link;
    }

    public void setLink(String link)
    {
        this.link = link;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
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
            .append("dropButtonId", getDropButtonId())
            .append("buttonId", getButtonId())
            .append("text", getText())
            .append("link", getLink())
            .append("state", getState())
            .append("sortOrder", getSortOrder())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
