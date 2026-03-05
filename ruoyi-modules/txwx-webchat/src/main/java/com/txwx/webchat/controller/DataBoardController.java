package com.txwx.webchat.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.webchat.domain.vo.UserTotalVo;
import com.txwx.webchat.service.IDataBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public AjaxResult userTotal() {
        List<UserTotalVo> res = iDataBoardService.selectUserTotal();
        return success(res);
    }

    @Operation(summary = "新关注人数趋势图")
    @GetMapping("/netUserTrend")
    public AjaxResult netUserTrend(Integer filterDimension) {
        List<Map<String, Object>> res = iDataBoardService.netUserTrend(filterDimension);
        return success(res);
    }

    @Operation(summary = "累计关注人数趋势图")
    @GetMapping("/accumulatedUserTrend")
    public AjaxResult accumulatedUserTrend(Integer filterDimension) {
        List<Map<String, Object>> res = iDataBoardService.accumulatedUserTrend(filterDimension);
        return success(res);
    }

    @Operation(summary = "阅读流量+分享流量趋势图+阅读人数流量来源")
    @GetMapping("/readFlowTrend")
    public AjaxResult readFlowTrend(Integer filterDimension) {
        List<List<Map<String, Object>>> res = iDataBoardService.readFlowTrend(filterDimension);
        return success(res);
    }

    @Operation(summary = "最佳阅读人数+分享人数")
    @GetMapping("/optimumReaderShareNum")
    public AjaxResult optimumReaderShareNum() {
        List<UserTotalVo> res = iDataBoardService.optimumReaderShareNum();
        return success(res);
    }

    @Operation(summary = "阅读后关注人数排行榜")
    @GetMapping("/subscribeUserAfterRead")
    public AjaxResult subscribeUserAfterRead() {
        List<Map<String, Object>> res = iDataBoardService.subscribeUserAfterRead();
        return success(res);
    }
}
