/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.domain.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Schema(description = "文章草稿查询请求")
public class ArticleDraftReqVO {

    @Schema(description = "状态")
    private String status;
    @Schema(description = "提交人")
    private String submitter;
    @Schema(description = "审核人")
    private String reviewer;
    @Schema(description = "页码")
    private Integer pageNum;
    @Schema(description = "每页数量")
    private Integer pageSize;
    @NotEmpty(message = "账号列表不能为空")
    @Schema(description = "账号id")
    private List<Long> accountIds;
}
