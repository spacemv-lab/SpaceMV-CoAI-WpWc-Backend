package com.txwx.social.dashboard.controller;

import com.alibaba.excel.EasyExcel;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.social.dashboard.domain.entity.FlowSource;
import com.txwx.social.dashboard.service.IDwsBizsummaryChannelDailyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/flowSource")
@Tag(name = "01--【微信运营】--流量来源分析")
public class FlowSourceController extends BaseController {

    @Autowired
    private IDwsBizsummaryChannelDailyService iDwsBizsummaryChannelDailyService;

    @PostMapping("/list")
    @Operation(summary = "流量来源分析列表")
    public AjaxResult select(@RequestParam("accountId") Long accountId) {
        List<FlowSource> res = iDwsBizsummaryChannelDailyService.selectSource(accountId);
        return success(res);
    }

    @PostMapping("/exportExcel")
    @Operation(summary = "导出流量来源分析")
    public void exportExcel(HttpServletResponse response, @RequestParam("accountId") Long accountId) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("流量来源分析导出", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        try {
            List<FlowSource> res = iDwsBizsummaryChannelDailyService.selectSource(accountId);
            EasyExcel.write(response.getOutputStream(), FlowSource.class)
                    .sheet("流量来源分析")
                    .doWrite(res);
        } catch (Exception e){
            // 重置 response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().println("{\"status\": 500, \"message\": \"导出失败\"}");
        }
    }
}
