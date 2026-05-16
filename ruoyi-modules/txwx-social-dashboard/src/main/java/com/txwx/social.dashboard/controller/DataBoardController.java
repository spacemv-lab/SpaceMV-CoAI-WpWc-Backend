/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.dashboard.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.txwx.social.dashboard.domain.vo.UserTotalVo;
import com.txwx.social.dashboard.service.IDataBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dataBoard")
@Tag(name = "00--【数据看板】")
public class DataBoardController extends BaseController {


    @Autowired
    private IDataBoardService iDataBoardService;

    @PreAuthorize("@ss.hasPermi('media:mediaProductData:api')")
    @Operation(summary = "用户总数+总阅读人数+总分享人数")
    @GetMapping("/userTotal")
    public AjaxResult userTotal(@RequestParam(value = "accountId") Long accountId) {
        List<UserTotalVo> res = iDataBoardService.selectUserTotal(accountId);
        return success(res);
    }

    @PreAuthorize("@ss.hasPermi('media:mediaProductData:api')")
    @Operation(summary = "新关注人数趋势图")
    @GetMapping("/netUserTrend")
    public AjaxResult netUserTrend(@RequestParam(value = "filterDimension") Integer filterDimension,
                                   @RequestParam(value = "accountId") Long accountId) {
        List<Map<String, Object>> res = iDataBoardService.netUserTrend(filterDimension, accountId);
        return success(res);
    }

    @PreAuthorize("@ss.hasPermi('media:mediaProductData:api')")
    @Operation(summary = "累计关注人数趋势图")
    @GetMapping("/accumulatedUserTrend")
    public AjaxResult accumulatedUserTrend(@RequestParam(value = "filterDimension") Integer filterDimension,
                                           @RequestParam(value = "accountId") Long accountId) {
        List<Map<String, Object>> res = iDataBoardService.accumulatedUserTrend(filterDimension, accountId);
        return success(res);
    }

    @PreAuthorize("@ss.hasPermi('media:mediaProductData:api')")
    @Operation(summary = "阅读流量+分享流量趋势图+阅读人数流量来源")
    @GetMapping("/readFlowTrend")
    public AjaxResult readFlowTrend(@RequestParam(value = "filterDimension") Integer filterDimension,
                                    @RequestParam(value = "accountId") Long accountId) {
        List<List<Map<String, Object>>> res = iDataBoardService.readFlowTrend(filterDimension, accountId);
        return success(res);
    }

    @PreAuthorize("@ss.hasPermi('media:mediaProductData:api')")
    @Operation(summary = "最佳阅读人数+分享人数")
    @GetMapping("/optimumReaderShareNum")
    public AjaxResult optimumReaderShareNum(@RequestParam(value = "accountId") Long accountId) {
        List<UserTotalVo> res = iDataBoardService.optimumReaderShareNum(accountId);
        return success(res);
    }

    @PreAuthorize("@ss.hasPermi('media:mediaProductData:api')")
    @Operation(summary = "阅读后关注人数排行榜")
    @GetMapping("/subscribeUserAfterRead")
    public AjaxResult subscribeUserAfterRead(@RequestParam(value = "accountId") Long accountId) {
        List<Map<String, Object>> res = iDataBoardService.subscribeUserAfterRead(accountId);
        return success(res);
    }
}
