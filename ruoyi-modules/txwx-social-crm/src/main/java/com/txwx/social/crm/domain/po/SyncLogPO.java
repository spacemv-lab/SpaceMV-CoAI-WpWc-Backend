/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "采集日志")
@TableName("sync_logs")
public class SyncLogPO {

    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Integer id;

    @Schema(description = "数据源ID")
    private String sourceId;

    @Schema(description = "状态: success/error")
    private String status;

    @Schema(description = "触发方式: manual/scheduled")
    private String triggerType;

    @Schema(description = "采集数据条数")
    private Integer dataPoints;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "开始时间")
    private Date startedAt;

    @Schema(description = "结束时间")
    private Date finishedAt;

    @Schema(description = "操作人")
    private String createdBy;
}
