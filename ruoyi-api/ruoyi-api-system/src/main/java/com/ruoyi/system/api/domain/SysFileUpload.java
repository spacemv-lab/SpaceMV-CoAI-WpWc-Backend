package com.ruoyi.system.api.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SysFileUpload {
    /**
     * code码
     */
    private int code;

    /**
     * 文件地址
     */
    private String url;

    /**
     * 结果信息
     */
    private String result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("code", getCode())
                .append("url", getUrl())
                .append("result", getResult())
                .toString();
    }
}
