package com.txwx.webchatcrm.domain.vo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description: 图文消息图片,用于前后端交互
 */
@Data
public class WebChatGraphicInformationImageVO {
    String media_id;

    String name;

    String update_time;

    String url;

    MultipartFile file;

    Long productId;

    Long platformId;
}
