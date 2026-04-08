package com.txwx.social.dashboard.domain.dto;

import lombok.Data;

@Data
public class GetPublishedListRequest {
    /**
     * 从全部素材的该偏移位置开始返回，0表示从第一个素材返回
     */
    private Integer offset;

    /**
     * 返回素材的数量，取值在1到20之间
     */
    private Integer count;

    /**
     * 1表示不返回content字段，0表示正常返回，默认为0
     */
    private Integer no_content;
}
