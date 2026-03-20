package com.txwx.webchat.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.webchat.domain.common.PageRequest;
import com.txwx.webchat.domain.common.PageResult;
import com.txwx.webchat.domain.condition.FlowSearchCondition;
import com.txwx.webchat.domain.entity.DwsBizsummaryChannelDaily;
import com.txwx.webchat.domain.vo.ImportResultVo;
import com.txwx.webchat.service.IDwsBizsummaryChannelDailyService;
import com.txwx.webchat.service.impl.ImportServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/flowData")
@Tag(name = "01--【微信运营】--流量数据")
public class FlowDataController extends BaseController {

    @Autowired
    private ImportServiceImpl importServiceImpl;
    @Autowired
    private IDwsBizsummaryChannelDailyService iDwsBizsummaryChannelDailyService;

    @PostMapping("/list")
    @Operation(summary = "流量汇总数据列表")
    public AjaxResult select(Integer pageNum, Integer pageSize, @RequestBody(required = false) FlowSearchCondition condition) {
        PageRequest pageRequest = new PageRequest(pageNum, pageSize);
        PageResult<DwsBizsummaryChannelDaily> res = iDwsBizsummaryChannelDailyService.select(pageRequest, condition);
        return success(res);
    }

    @GetMapping("/downloadTemplate")
    @Operation(summary = "下载模板")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        try {
            // 1. 设置响应头（和导出错误行的逻辑一模一样）
            response.reset();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("流量数据导入模板", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

            // 2. 核心魔法：传一个空的 List 进去！
            // EasyExcel 会根据 DwsUsers.class 的注解自动画出表头，但因为数据是空的，所以刚好就是个完美的模板
            EasyExcel.write(response.getOutputStream(), DwsBizsummaryChannelDaily.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("导入模板")
                    .doWrite(new ArrayList<>());

        } catch (Exception e) {
            // 万一生成失败，重置 response 并返回 JSON 错误提示
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().println("{\"code\":500, \"msg\":\"下载模板失败\"}");
        }
    }

    @PostMapping("/importExcel")
    @Operation(summary = "导入流量汇总数据")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file, HttpServletResponse response) throws Exception {
        String sql = "INSERT INTO dws_bizsummary_channel_daily " +
                "(ref_date, read_user_cnt, share_user, " +
                "redirect_ori_page_count, redirect_ori_page_user, " +
                "collection_count, collection_user,send_page_count, channel) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        ImportResultVo res = importServiceImpl.importExcel(file, DwsBizsummaryChannelDaily.class, sql, response, null);
        if (res.getErrors().size() > 0) return null;
        else return success("导入成功!");
    }

    @PostMapping("/exportExcel")
    @Operation(summary = "导出流量汇总数据")
    public void exportExcel(HttpServletResponse response, @RequestBody(required = false) FlowSearchCondition condition) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("流量数据导出", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(response.getOutputStream(), DwsBizsummaryChannelDaily.class).build();
            WriteSheet writeSheet = EasyExcel.writerSheet("流量数据").build();
            int pageSize = 10000;
            int pageNum = 1;
            boolean hasNext = true;

            while (hasNext) {
                PageRequest pageRequest = new PageRequest(pageNum, pageSize);
                PageResult<DwsBizsummaryChannelDaily> select = iDwsBizsummaryChannelDailyService.select(pageRequest, condition);
                List<DwsBizsummaryChannelDaily> dataList = select.getContent();
                if (dataList == null || dataList.isEmpty()) {
                    hasNext = false;
                    break;
                }
                excelWriter.write(dataList, writeSheet);
                if (dataList.size() < pageSize) hasNext = false;
                dataList.clear();
                pageNum++;
            }
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }
}
