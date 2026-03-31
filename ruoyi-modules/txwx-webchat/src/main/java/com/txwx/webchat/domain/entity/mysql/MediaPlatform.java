package com.txwx.webchat.domain.entity.mysql;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "自媒体-平台")
@TableName("txwx_media_platform")
public class MediaPlatform extends EntityBase{

    @Schema(description = "平台名称")
    private String name;

    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "appId")
    @NotBlank(message = "appId不能为空")
    private String appId;

    @Schema(description = "secret")
    @NotBlank(message = "secret不能为空")
    private String secret;

    @Schema(description = "是否删除")
    private Integer isDelete;

    @Schema(description = "同步状态")
    private Integer syncStatus;

    @Schema(description = "备注")
    private String remark;
}
