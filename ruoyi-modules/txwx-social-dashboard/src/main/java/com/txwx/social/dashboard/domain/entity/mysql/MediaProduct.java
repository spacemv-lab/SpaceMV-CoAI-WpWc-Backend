package com.txwx.social.dashboard.domain.entity.mysql;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "自媒体-产品")
@TableName("txwx_media_product")
public class MediaProduct extends EntityBase{

    @Schema(description = "产品名称")
    @NotBlank(message = "产品名称不能为空")
    private String name;

    @Schema(description = "产品描述")
    private String description;

    private Integer isDelete;
}
