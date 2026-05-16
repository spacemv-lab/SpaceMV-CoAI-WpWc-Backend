package com.txwx.social.crm.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class MediaPlatformVO {

    @Schema(description = "ID")
    private Long id;

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

    @Schema(description = "uuid")
    private String uuid;

    @Schema(description = "创建人ID")
    private Long createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "创建人名称")
    private String createByName;

    @Schema(description = "修改人ID")
    private Long modifyBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "修改时间")
    private Date modifyTime;

    @Schema(description = "修改人名称")
    private String modifyByName;
}
