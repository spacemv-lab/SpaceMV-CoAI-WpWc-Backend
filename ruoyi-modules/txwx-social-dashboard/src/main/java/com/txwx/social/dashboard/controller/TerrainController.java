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
import com.txwx.social.dashboard.domain.common.PageRequest;
import com.txwx.social.dashboard.domain.common.PageResult;
import com.txwx.social.dashboard.domain.dto.ProductPlatformDto;
import com.txwx.social.dashboard.domain.entity.AgeDistribution;
import com.txwx.social.dashboard.domain.entity.ChannelComposition;
import com.txwx.social.dashboard.domain.entity.DwsUsers;
import com.txwx.social.dashboard.domain.entity.TerrainDistribution;
import com.txwx.social.dashboard.domain.vo.ImportResultVo;
import com.txwx.social.dashboard.service.ITerrainDistributionService;
import com.txwx.social.dashboard.service.impl.ImportServiceImpl;
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
@RequestMapping("/terrainData")
@Tag(name = "01--【微信运营】--地域分布数据")
public class TerrainController extends BaseController {

    @Autowired
    private ImportServiceImpl importServiceImpl;
    @Autowired
    private ClickhouseService clickhouseService;
    @Autowired
    private ITerrainDistributionService iTerrainDistributionService;

    @PostMapping("/list")
    @Operation(summary = "地域分布列表")
    public AjaxResult select(Integer pageNum, Integer pageSize, @RequestBody ProductPlatformDto productPlatformDto) {
        PageHelper.startPage(pageNum, pageSize);
        LambdaQueryWrapper<TerrainDistribution> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TerrainDistribution::getPlatformId, productPlatformDto.getPlatformId());
        queryWrapper.eq(TerrainDistribution::getProductId, productPlatformDto.getProductId());
        queryWrapper.last("ORDER BY toFloat32(replace(proportion, '%', '')) DESC");
        List<TerrainDistribution> list = iTerrainDistributionService.list(queryWrapper);
        PageInfo<TerrainDistribution> pageInfo = new PageInfo<>(list);
        return success(new PageResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getList()));
    }

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

    @PostMapping("/importExcel")
    @Operation(summary = "导入地域分布数据")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file, HttpServletResponse response) throws Exception {
        String truncateSql = "truncate table terrain_distribution";
        try {
            clickhouseService.singleInsert(truncateSql);
            logger.info("清空terrain_distribution表成功");
        }catch (Exception e) {
            logger.warn("清空terrain_distribution表失败（可能是第一次运行）: " + e.getMessage());
        }
        String sql = "insert into terrain_distribution (terrain, user_number, proportion, product_id, platform_id) values (?, ?, ?, ?, ?)";
        ImportResultVo res = importServiceImpl.importExcel(file, TerrainDistribution.class, sql, response, null);
        if (res.getErrors().size() > 0) return null;
        else return success("导入成功!");
    }

    @PostMapping("/exportExcel")
    @Operation(summary = "导出地域分布")
    public void exportExcel(HttpServletResponse response, @RequestBody ProductPlatformDto productPlatformDto) throws IOException {
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
                queryWrapper.eq(TerrainDistribution::getPlatformId, productPlatformDto.getPlatformId());
                queryWrapper.eq(TerrainDistribution::getProductId, productPlatformDto.getProductId());
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
