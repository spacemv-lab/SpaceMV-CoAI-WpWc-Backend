package com.txwx.social.crm.publisher;

import lombok.Data;

/**
 * 发布上下文。
 * 携带发布目标、账号、样式预设等信息。
 */
@Data
public class PublishContext {

    /** 目标平台编码，如 WECHAT_OFFICIAL_ACCOUNT */
    private String targetCode;

    /** 自媒体账号 ID */
    private Long accountId;

    /** 样式预设名，如 gewu-wechat-blue, gewu-fast-black */
    private String stylePreset;

    public PublishContext(String targetCode, Long accountId) {
        this.targetCode = targetCode;
        this.accountId = accountId;
    }

    public PublishContext(String targetCode, Long accountId, String stylePreset) {
        this.targetCode = targetCode;
        this.accountId = accountId;
        this.stylePreset = stylePreset;
    }
}
