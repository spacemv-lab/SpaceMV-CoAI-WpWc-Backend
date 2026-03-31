package com.txwx.webchat.domain.entity.mysql;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public abstract class EntityBase implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "ID")
    private Long id;

    @TableField(value = "uuid", fill = FieldFill.INSERT)
    @Schema(description = "uuid")
    private String uuid;

    @TableField(value = "create_by", fill = FieldFill.INSERT)
    @Schema(description = "创建人ID")
    private Long createBy;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;

    @TableField(value = "create_by_name", fill = FieldFill.INSERT)
    @Schema(description = "创建人名称")
    private String createByName;

    @TableField(value = "modify_by", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "修改人ID")
    private Long modifyBy;

    @TableField(value = "modify_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "修改时间")
    private Date modifyTime;

    @TableField(value = "modify_by_name", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "修改人名称")
    private String modifyByName;
}
