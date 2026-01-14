package com.txwx.webchatcrm.dto;

import lombok.Data;

/**
 * 永久素材项
 */
@Data
public class MaterialItem {
    /**
     * 消息
     */
    private String media_id;

    /**
     * 图片的名字
     */
    private String name;

    /**
     * 更新日期
     */
    private String update_time;

    /**
     * 图片的URL
     */
    private String url;
}
