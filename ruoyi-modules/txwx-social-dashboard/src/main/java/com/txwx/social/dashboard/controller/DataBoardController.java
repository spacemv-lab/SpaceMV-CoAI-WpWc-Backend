package com.txwx.social.dashboard.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.social.dashboard.domain.vo.UserTotalVo;
import com.txwx.social.dashboard.service.IDataBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Operation(summary = "用户总数+总阅读人数+总分享人数")
    @GetMapping("/userTotal")
    public AjaxResult userTotal(@RequestParam(value = "productId") Long productId,
                                @RequestParam(value = "platformId") Long platformId) {
        List<UserTotalVo> res = iDataBoardService.selectUserTotal(productId, platformId);
        return success(res);
    }

    @Operation(summary = "新关注人数趋势图")
    @GetMapping("/netUserTrend")
    public AjaxResult netUserTrend(@RequestParam(value = "filterDimension") Integer filterDimension,
                                   @RequestParam(value = "productId") Long productId,
                                   @RequestParam(value = "platformId") Long platformId) {
        List<Map<String, Object>> res = iDataBoardService.netUserTrend(filterDimension, productId, platformId);
        return success(res);
    }

    @Operation(summary = "累计关注人数趋势图")
    @GetMapping("/accumulatedUserTrend")
    public AjaxResult accumulatedUserTrend(@RequestParam(value = "filterDimension") Integer filterDimension,
                                           @RequestParam(value = "productId") Long productId,
                                           @RequestParam(value = "platformId") Long platformId) {
        List<Map<String, Object>> res = iDataBoardService.accumulatedUserTrend(filterDimension, productId, platformId);
        return success(res);
    }

    @Operation(summary = "阅读流量+分享流量趋势图+阅读人数流量来源")
    @GetMapping("/readFlowTrend")
    public AjaxResult readFlowTrend(@RequestParam(value = "filterDimension") Integer filterDimension,
                                    @RequestParam(value = "productId") Long productId,
                                    @RequestParam(value = "platformId") Long platformId) {
        List<List<Map<String, Object>>> res = iDataBoardService.readFlowTrend(filterDimension, productId, platformId);
        return success(res);
    }

    @Operation(summary = "最佳阅读人数+分享人数")
    @GetMapping("/optimumReaderShareNum")
    public AjaxResult optimumReaderShareNum(@RequestParam(value = "productId") Long productId,
                                            @RequestParam(value = "platformId") Long platformId) {
        List<UserTotalVo> res = iDataBoardService.optimumReaderShareNum(productId, platformId);
        return success(res);
    }

    @Operation(summary = "阅读后关注人数排行榜")
    @GetMapping("/subscribeUserAfterRead")
    public AjaxResult subscribeUserAfterRead(@RequestParam(value = "productId") Long productId,
                                             @RequestParam(value = "platformId") Long platformId) {
        List<Map<String, Object>> res = iDataBoardService.subscribeUserAfterRead(productId, platformId);
        return success(res);
    }
}
