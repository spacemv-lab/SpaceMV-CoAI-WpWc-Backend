package com.txwx.webchatcrm.domain;

import lombok.Data;

@Data
public class WebChatAccessToken {
    /**
     * @description: 获取到的凭证
     */
    private String access_token;

    /**
     * @description: 凭证有效时间，单位：秒。目前是7200秒之内的值。
     */
    private int expires_in;
}
