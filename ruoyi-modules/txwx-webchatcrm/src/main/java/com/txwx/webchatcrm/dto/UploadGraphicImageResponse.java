package com.txwx.webchatcrm.dto;

import lombok.Data;

/**
 * 上传图文消息图片响应
 */
@Data
public class UploadGraphicImageResponse {
    /**
     * 图片URL
     */
    private String url;

    /**
     * 错误码
     */
    private Integer errcode;

    /**
     * 错误描述
     */
    private String errmsg;
}
