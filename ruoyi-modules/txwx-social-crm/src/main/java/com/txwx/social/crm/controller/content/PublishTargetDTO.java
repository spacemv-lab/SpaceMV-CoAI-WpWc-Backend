package com.txwx.social.crm.controller.content;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PublishTargetDTO {

    @NotBlank(message = "发布目标编码不能为空")
    private String targetCode;

    private Long accountId;

    /** 样式预设名，可为 null（使用默认） */
    private String stylePreset;
}
