package com.txwx.webchat.domain.vo;

import com.txwx.webchat.domain.entity.mysql.MediaPlatform;
import com.txwx.webchat.domain.entity.mysql.UserPlatform;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserPlatformVo extends UserPlatform {

    @Schema(description = "绑定的平台")
    private MediaPlatform mediaPlatform;
}
