/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.ruoyi.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "数据源配置")
@TableName(value = "data_sources", autoResultMap = true)
public class DataSourcePO extends BaseEntity {

    @TableField(exist = false)
    private String searchValue;
    @TableField(exist = false)
    private String createBy;
    @TableField(exist = false)
    private Date createTime;
    @TableField(exist = false)
    private String updateBy;
    @TableField(exist = false)
    private Date updateTime;
    @TableField(exist = false)
    private String savedBy;
    @TableField(exist = false)
    private Date savedTime;
    @TableField(exist = false)
    private String publishedBy;
    @TableField(exist = false)
    private Date publishedTime;
    @TableField(exist = false)
    private String remark;
    @TableField(exist = false)
    private Map<String, Object> params;

    @Schema(description = "唯一ID")
    private String id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "关联指标slug")
    private String slug;

    @Schema(description = "类型: fred/yahoo/csv/manual")
    private String type;

    @Schema(description = "类型相关配置(JSON)")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object config;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "展示单位")
    private String displayUnit;

    @Schema(description = "API Key引用名")
    private String apiKeyRef;

    @Schema(description = "地区: cn/us/global")
    private String region;

    @Schema(description = "分类: price/employment/manufacturing/trade/monetary/market")
    private String category;

    @Schema(description = "标签")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    @Schema(description = "数据变换: none/yoy")
    private String transform;

    @Schema(description = "Cron表达式")
    private String schedule;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "最近同步时间")
    private Date lastSyncAt;

    @Schema(description = "最近同步状态: never/success/error")
    private String lastSyncStatus;

    @Schema(description = "最近同步消息")
    private String lastSyncMessage;

    @Schema(description = "前台是否可见")
    private Boolean isPublic;

    @Schema(description = "是否仅PRO可见")
    private Boolean proOnly;

    @Schema(description = "更新人")
    private String updatedBy;

    @Schema(description = "更新时间")
    private Date updatedAt;
}
