package com.txwx.social.crm.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description: 永久素材,用于前后端交互
 */
@Data
@Schema(description = "永久素材VO")
public class WebChatMaterialPermanentVO {

    @Schema(description = "媒体ID")
    private String mediaId;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "更新时间")
    private String updateTime;

    @Schema(description = "URL")
    private String url;

    @Schema(description = "文件")
    private MultipartFile file;

    @Schema(description = "账号ID")
    private Long accountId;
}
