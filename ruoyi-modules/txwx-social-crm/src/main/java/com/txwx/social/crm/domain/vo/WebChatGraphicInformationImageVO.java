package com.txwx.social.crm.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description: 图文消息图片,用于前后端交互
 */
@Data
@Schema(description = "图文消息图片VO")
public class WebChatGraphicInformationImageVO {

    @Schema(description = "媒体ID")
    String mediaId;

    @Schema(description = "名称")
    String name;

    @Schema(description = "更新时间")
    String updateTime;

    @Schema(description = "URL")
    String url;

    @Schema(description = "文件")
    MultipartFile file;

    @Schema(description = "账号信息")
    Long accountId;
}
