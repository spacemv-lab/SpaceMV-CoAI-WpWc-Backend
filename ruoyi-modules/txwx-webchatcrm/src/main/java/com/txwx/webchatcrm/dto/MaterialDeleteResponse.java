package com.txwx.webchatcrm.dto;

import lombok.Data;

/**
 * 删除永久素材响应
 */
@Data
public class MaterialDeleteResponse {
    /**
     * 错误码
     */
    private Integer errcode;

    /**
     * 错误描述
     */
    private String errmsg;
}
