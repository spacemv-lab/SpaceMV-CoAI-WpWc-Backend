package com.txwx.social.dashboard.domain.vo;

import com.txwx.social.dashboard.domain.entity.mysql.MediaPlatform;
import com.txwx.social.dashboard.domain.entity.mysql.MediaProduct;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class MediaProductVo extends MediaProduct {

    @Schema(description = "平台列表")
    private List<UserPlatformVo> userPlatformList;
}
