package com.txwx.social.dashboard.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.txwx.social.dashboard.domain.entity.TerrainDistribution;
import com.txwx.social.dashboard.domain.vo.ImportResultVo;
import com.txwx.social.dashboard.exception.ImportException;
import com.txwx.social.dashboard.service.ITerrainDistributionService;
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
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/terrainData")
@Tag(name = "01--【微信运营】--地域分布数据")
public class TerrainController extends BaseController {

    @Autowired
    private ImportUtil importUtil;
    @Autowired
    private ClickhouseService clickhouseService;
    @Autowired
    private ITerrainDistributionService iTerrainDistributionService;

    @PreAuthorize("@ss.hasPermi('media:mediaProductData:api')")
    @PostMapping("/list")
    @Operation(summary = "地域分布列表")
    public TableDataInfo select(@RequestParam Long accountId) {

        LambdaQueryWrapper<TerrainDistribution> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TerrainDistribution::getAccountId, accountId);
        queryWrapper.last("ORDER BY toFloat32(replace(proportion, '%', '')) DESC");
        List<TerrainDistribution> list = iTerrainDistributionService.list(queryWrapper);
        return getDataTable(list);
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
            String fileName = URLEncoder.encode("地域分布数据导入模板", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

            EasyExcel.write(response.getOutputStream(), TerrainDistribution.class)
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
    @Operation(summary = "导入地域分布数据")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file, HttpServletResponse response, @RequestParam("accountId") Long accountId) throws Exception {
        String truncateSql = SqlUtils.deleteSql("dim_terrain_distribution");
        try {
            clickhouseService.singleInsert(truncateSql, accountId);
            logger.info("清空terrain_distribution表成功");
        }catch (Exception e) {
            logger.warn("清空terrain_distribution表失败（可能是第一次运行）: " + e.getMessage());
        }
        String sql = "insert into dim_terrain_distribution (terrain, user_number, proportion, pull_time, account_id) values (?, ?, ?, ?, ?)";
        Map<String, Object> extInfo = new HashMap<>();
        extInfo.put("accountId", accountId);
        try {
            ImportResultVo res = importUtil.importExcel(file,
                    TerrainDistribution.class,
                    sql,
                    null,
                    extInfo);
            if (res.getSkippedCount() != null && res.getSkippedCount() > 0) {
                return success("导入成功，但跳过了 " + res.getSkippedCount() + " 条重复数据");
            }
            return success("导入成功!");
        } catch (ImportException e) {
            // 下载错误文件
            byte[] errorData = importUtil.exportErrorList(TerrainDistribution.class, e.getErrors(), extInfo);
            response.reset();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String rawFileName = "导入失败记录_" + System.currentTimeMillis();
            String encodedFileName = URLEncoder.encode(rawFileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName + ".xlsx");
            response.getOutputStream().write(errorData);
            response.getOutputStream().flush();
            // 返回错误统计
            return error("导入存在 " + e.getErrors().size() + " 条错误数据，请下载错误文件查看");
        }
    }

    @PreAuthorize("@ss.hasPermi('media:mediaProductData:api')")
    @PostMapping("/exportExcel")
    @Operation(summary = "导出地域分布")
    public void exportExcel(HttpServletResponse response, @RequestParam("accountId") Long accountId) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("导出地域分布", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(response.getOutputStream(), TerrainDistribution.class).build();
            WriteSheet writeSheet = EasyExcel.writerSheet("地域分布").build();
            int pageSize = 10000;
            int pageNum = 1;
            boolean hasNext = true;

            while (hasNext) {
                PageHelper.startPage(pageNum, pageSize);
                LambdaQueryWrapper<TerrainDistribution> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(TerrainDistribution::getAccountId, accountId);
                queryWrapper.last("ORDER BY toFloat32(replace(proportion, '%', '')) DESC");
                List<TerrainDistribution> dataList = iTerrainDistributionService.list(queryWrapper);
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
