/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.dashboard.controller;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.txwx.social.dashboard.config.WebChatConfig;
import com.txwx.social.dashboard.domain.condition.FlowSearchCondition;
import com.txwx.social.dashboard.domain.entity.OdsArticleSummaryDaily;
import com.txwx.social.dashboard.domain.vo.ImportResultVo;
import com.txwx.social.dashboard.exception.ImportException;
import com.txwx.social.dashboard.service.IDwsBizsummaryChannelDailyService;
import com.txwx.social.dashboard.util.ImportUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/flowData")
@Tag(name = "01--【微信运营】--流量数据")
@Slf4j
public class FlowDataController extends BaseController {

    @Autowired
    private WebChatConfig webChatConfig;
    @Autowired
    private ImportUtil importUtil;
    @Autowired
    private IDwsBizsummaryChannelDailyService iDwsBizsummaryChannelDailyService;

    @PreAuthorize("@ss.hasPermi('media:mediaProductData:api')")
    @PostMapping("/list")
    @Operation(summary = "流量汇总数据列表")
    public TableDataInfo select(@RequestBody(required = false) FlowSearchCondition condition,
                                @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
        condition.setPageSize(pageSize);
        condition.setPageNum(pageNum);
        return iDwsBizsummaryChannelDailyService.selectByCondition(condition);
    }


    @PreAuthorize("@ss.hasPermi('media:mediaProductData:api')")
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
            EasyExcel.write(response.getOutputStream(), OdsArticleSummaryDaily.class)
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

    @PreAuthorize("@ss.hasPermi('media:mediaProductData:api')")
    @PostMapping("/importExcel")
    @Operation(summary = "导入流量汇总数据")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file, HttpServletResponse response, @RequestParam("accountId") Long accountId) throws Exception {
        Map<String, Object> extInfo = new HashMap<>();
        extInfo.put("accountId", accountId);
        try {
            ImportResultVo res = importUtil.importExcel(file, OdsArticleSummaryDaily.class,
                    webChatConfig.getInsertarticlesummarydailysql(), null,
                    extInfo);
            if (res.getSkippedCount() != null && res.getSkippedCount() > 0) {
                return success("导入成功，但跳过了 " + res.getSkippedCount() + " 条重复数据");
            }
            return success("导入成功!");
        } catch (ImportException e) {
            byte[] errorData = importUtil.exportErrorList(OdsArticleSummaryDaily.class, e.getErrors(), extInfo);
            response.reset();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String rawFileName = "导入失败记录_" + System.currentTimeMillis();
            String encodedFileName = URLEncoder.encode(rawFileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName + ".xlsx");
            response.getOutputStream().write(errorData);
            response.getOutputStream().flush();
            return error("导入存在 " + e.getErrors().size() + " 条错误数据，请下载错误文件查看");
        }
    }

    @PreAuthorize("@ss.hasPermi('media:mediaProductData:api')")
    @PostMapping("/exportExcel")
    @Operation(summary = "导出流量汇总数据")
    public void exportExcel(HttpServletResponse response, @RequestBody(required = false) FlowSearchCondition condition) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("流量数据导出", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(response.getOutputStream(), OdsArticleSummaryDaily.class).build();
            WriteSheet writeSheet = EasyExcel.writerSheet("流量数据").build();
            int pageSize = 1000;
            int maxRows = 5000;
            int currentRowCount = 0;
            int pageNum = 1;

            while (currentRowCount < maxRows) {
                // 使用 Service 层分页查询
                condition.setPageNum(pageNum);
                condition.setPageSize(pageSize);
                TableDataInfo pageRes =iDwsBizsummaryChannelDailyService.selectByCondition(condition);

                List<Map<String, Object>> rawdataList = (List<Map<String, Object>>) pageRes.getRows();
                if (rawdataList == null || rawdataList.isEmpty()) {
                    break;
                }

                List<OdsArticleSummaryDaily> dataList = Lists.newArrayList();
                for (Map<String, Object> row : rawdataList) {
                    OdsArticleSummaryDaily articleSummaryDaily = new OdsArticleSummaryDaily();
                    BeanUtil.fillBeanWithMap(row, articleSummaryDaily, false, true);
                    dataList.add(articleSummaryDaily);
                }

                // 写入 Excel
                excelWriter.write(dataList, writeSheet);
                currentRowCount += dataList.size();

                // 判断是否还有更多数据
                if (dataList.size() < pageSize) {
                    break;
                }

                // 检查是否达到最大行数
                if (currentRowCount >= maxRows) {
                    log.info("导出数据已达到最大限制 {} 行，停止导出", maxRows);
                    break;
                }

                pageNum++;
            }

            log.info("导出完成，共导出 {} 行数据", currentRowCount);
        } catch (Exception e) {
            log.error("导出Excel失败", e);
            throw new RuntimeException("导出失败：" + e.getMessage());
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }
}
