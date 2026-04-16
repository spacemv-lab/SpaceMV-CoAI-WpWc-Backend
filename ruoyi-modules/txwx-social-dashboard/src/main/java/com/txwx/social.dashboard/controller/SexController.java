package com.txwx.social.dashboard.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.social.dashboard.domain.entity.SexDistribution;
import com.txwx.social.dashboard.domain.vo.ImportResultVo;
import com.txwx.social.dashboard.service.ISexDistributionService;
import com.txwx.social.dashboard.util.ImportUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/sexData")
@Tag(name = "01--【微信运营】--性别分布数据")
public class SexController extends BaseController {

    @Autowired
    private ImportUtil importUtil;
    @Autowired
    private ISexDistributionService iSexDistributionService;
    @Autowired
    private ClickhouseService clickhouseService;

    @PostMapping("/list")
    @Operation(summary = "性别分布列表")
    public AjaxResult select(@RequestBody List<Long> accountIds) {
        LambdaQueryWrapper<SexDistribution> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SexDistribution::getAccountId, accountIds);
        queryWrapper.last("ORDER BY toFloat32(replace(proportion, '%', '')) DESC");
        return success(iSexDistributionService.list(queryWrapper));
    }

    @GetMapping("/downloadTemplate")
    @Operation(summary = "下载模板")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        try {
            // 1. 设置响应头（和导出错误行的逻辑一模一样）
            response.reset();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("性别分布数据导入模板", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

            EasyExcel.write(response.getOutputStream(), SexDistribution.class)
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
    @Operation(summary = "导入性别分布数据")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file, HttpServletResponse response) throws Exception {
        // 需要先清空表
        String truncateSql = "TRUNCATE TABLE dim_sex_distribution";
        try {
            clickhouseService.singleInsert(truncateSql);
            logger.info("清空sex_distribution表成功");
        } catch (Exception e) {
            logger.warn("清空sex_distribution表失败（可能是第一次运行）: " + e.getMessage());
        }
        String sql = "INSERT INTO dim_sex_distribution (sex, user_number, proportion, account_id) VALUES (?, ?, ?, ?)";
        Map<String, Object> extInfo = new HashMap<>();
        ImportResultVo res = importUtil.importExcel(file, SexDistribution.class,
                sql,
                response,
                null,
                extInfo);
        if (!res.getErrors().isEmpty()) return null;
        else return success("导入成功!");
    }

    @PostMapping("/exportExcel")
    @Operation(summary = "导出性别分布")
    public void exportExcel(HttpServletResponse response, @RequestBody List<Long> accountIds) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("导出性别分布", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        try {
            LambdaQueryWrapper<SexDistribution> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SexDistribution::getAccountId, accountIds.get(0));
            queryWrapper.last("ORDER BY toFloat32(replace(proportion, '%', '')) DESC");
            List<SexDistribution> list = iSexDistributionService.list(queryWrapper);

            EasyExcel.write(response.getOutputStream(), SexDistribution.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("性别分布")
                    .doWrite(list);
        } catch (Exception e){
            // 重置 response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().println("{\"status\": 500, \"message\": \"导出失败\"}");
        }
    }
}
