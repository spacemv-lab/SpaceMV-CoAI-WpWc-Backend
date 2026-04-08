package com.txwx.social.dashboard.domain.vo;

import com.txwx.social.dashboard.domain.entity.mysql.MediaPlatform;
import com.txwx.social.dashboard.domain.entity.mysql.UserPlatform;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserPlatformVo extends UserPlatform {

    @Schema(description = "绑定的平台")
    private MediaPlatform mediaPlatform;
}
