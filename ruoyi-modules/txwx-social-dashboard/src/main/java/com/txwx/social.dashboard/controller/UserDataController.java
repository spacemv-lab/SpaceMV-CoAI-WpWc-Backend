package com.txwx.social.dashboard.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.txwx.social.dashboard.config.WebChatConfig;
import com.txwx.social.dashboard.domain.condition.BaseSearchCondition;
import com.txwx.social.dashboard.domain.entity.DwsUsers;
import com.txwx.social.dashboard.domain.vo.ImportResultVo;
import com.txwx.social.dashboard.exception.ImportException;
import com.txwx.social.dashboard.service.IDwsUsersService;
import com.txwx.social.dashboard.util.ImportUtil;
import com.txwx.social.dashboard.util.SqlUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/userData")
@Tag(name = "01--【微信运营】--用户数据")
public class UserDataController extends BaseController {

    @Autowired
    private WebChatConfig webChatConfig;
    @Autowired
    private ImportUtil importUtil;
    @Autowired
    private IDwsUsersService iDwsUsersService;

    @PreAuthorize("@ss.hasPermi('media:mediaProductData:api')")
    @PostMapping("/list")
    @Operation(summary = "用户数据列表")
    public TableDataInfo select(@RequestBody(required = false) BaseSearchCondition condition) {
        startPage();
        List<DwsUsers> usersList = iDwsUsersService.select(condition);
        return getDataTable(usersList);
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
            String fileName = URLEncoder.encode("用户数据导入模板", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

            // 2. 核心魔法：传一个空的 List 进去！
            // EasyExcel 会根据 DwsUsers.class 的注解自动画出表头，但因为数据是空的，所以刚好就是个完美的模板
            EasyExcel.write(response.getOutputStream(), DwsUsers.class)
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
    @Operation(summary = "导入用户数据")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file, @RequestParam("accountId") Long accountId) throws Exception {

        Map<String, Object> extInfo = new HashMap<>();
        extInfo.put("accountId", accountId);
        try {
            ImportResultVo res = importUtil.importExcel(file,
                    DwsUsers.class,
                    webChatConfig.getInsertdwsuserssql(),
                    null,
                    extInfo);
            if (res.getSkippedCount() != null && res.getSkippedCount() > 0) {
                return AjaxResult.success("导入成功，但跳过了 " + res.getSkippedCount() + " 条重复数据");
            }
            return AjaxResult.success("导入成功!");
        } catch (ImportException e) {
            return AjaxResult.error(e.getMessage(), e.getErrors());
        }
    }

    @PreAuthorize("@ss.hasPermi('media:mediaProductData:api')")
    @PostMapping("/exportExcel")
    @Operation(summary = "导出用户数据")
    public void exportExcel(HttpServletResponse response, @RequestBody(required = false) BaseSearchCondition condition) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("用户数据导出", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(response.getOutputStream(), DwsUsers.class).build();
            WriteSheet writeSheet = EasyExcel.writerSheet("用户数据").build();
            int pageSize = 10000;
            int pageNum = 1;
            boolean hasNext = true;

            while (hasNext) {
                Page<DwsUsers> select = iDwsUsersService.selectPage(pageNum, pageSize, condition);
                List<DwsUsers> dataList = select.getRecords();
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
