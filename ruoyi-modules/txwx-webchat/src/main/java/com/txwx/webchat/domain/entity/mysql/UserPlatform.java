package com.txwx.webchat.domain.entity.mysql;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("txwx_media_user_platform")
public class UserPlatform extends EntityBase{

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "平台id")
    private Long platformId;

    @Schema(description = "平台appid")
    private String platformAppId;

    private String remark;
}
