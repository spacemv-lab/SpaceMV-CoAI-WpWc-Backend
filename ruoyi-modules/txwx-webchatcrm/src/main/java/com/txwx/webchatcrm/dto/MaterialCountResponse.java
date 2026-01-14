package com.txwx.webchatcrm.dto;

import lombok.Data;

/**
 * 永久素材数量响应
 */
@Data
public class MaterialCountResponse {
    /**
     * 错误码
     */
    private Integer errcode;

    /**
     * 错误描述
     */
    private String errmsg;

    /**
     * 语音总数量
     */
    private Integer voice_count;

    /**
     * 视频总数量
     */
    private Integer video_count;

    /**
     * 图片总数量
     */
    private Integer image_count;

    /**
     * 图文总数量
     */
    private Integer news_count;
}
