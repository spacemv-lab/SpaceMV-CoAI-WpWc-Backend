package com.txwx.webchatcrm.domain.po;

import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TxwxPermanentMaterialImagePO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 媒体ID
     */
    private String mediaId;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 图片URL
     */
    private String url;

    /**
     * 删除标志(0代表存在 1代表删除)
     */
    private String delFlag;

    private Long productId;

    private Long platformId;
}
