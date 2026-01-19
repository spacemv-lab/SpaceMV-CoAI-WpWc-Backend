package com.txwx.webchatcrm.dto;

import lombok.Data;

/**
 * 获取草稿详情请求
 */
@Data
public class GetDraftDetailRequest {

    /**
     * 要获取的草稿的media_id
     */
    private String media_id;
}
