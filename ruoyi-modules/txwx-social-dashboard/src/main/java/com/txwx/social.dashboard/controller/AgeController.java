package com.txwx.social.dashboard.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.ruoyi.common.core.utils.sql.SqlUtil;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.social.dashboard.domain.entity.AgeDistribution;
import com.txwx.social.dashboard.domain.vo.ImportResultVo;
import com.txwx.social.dashboard.service.IAgeDistributionService;
import com.txwx.social.dashboard.util.ImportUtil;
import com.txwx.social.dashboard.util.SqlUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ageData")
@Tag(name = "01--【微信运营】--年龄分布数据")
public class AgeController extends BaseController {

    @Autowired
    private ImportUtil importUtil;
    @Autowired
    private ClickhouseService clickhouseService;
    @Autowired
    private IAgeDistributionService iAgeDistributionService;

    @PostMapping("/list")
    @Operation(summary = "年龄分布列表")
    public AjaxResult select(@NotEmpty(message = "账号信息不能为空") @RequestParam("accountId") Long accountId) {
        //TODO 目前仅支持单账号
        LambdaQueryWrapper<AgeDistribution> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgeDistribution::getAccountId, accountId);

        queryWrapper.last("ORDER BY toFloat32(replace(proportion, '%', '')) DESC");
        return success(iAgeDistributionService.list(queryWrapper));
    }

    @GetMapping("/downloadTemplate")
    @Operation(summary = "下载模板")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        try {
            // 1. 设置响应头（和导出错误行的逻辑一模一样）
            response.reset();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("年龄分布数据导入模板", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

            EasyExcel.write(response.getOutputStream(), AgeDistribution.class)
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
    @Operation(summary = "导入年龄分布数据")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file, HttpServletResponse response, @RequestParam("accountId") Long accountId) throws Exception {
        String truncateSql = SqlUtils.deleteSql("dim_age_distribution");
        try {
            clickhouseService.singleInsert(truncateSql, accountId);
            logger.info("清空age_distribution表成功");
        }catch (Exception e) {
            logger.warn("清空age_distribution表失败（可能是第一次运行）: " + e.getMessage());
        }
        String sql = "insert into dim_age_distribution (age, user_number, proportion, account_id) values (?, ?, ?, ?)";
        Map<String, Object> extInfo = new HashMap<>();
        extInfo.put("accountId", accountId);
        ImportResultVo res = importUtil.importExcel(file, AgeDistribution.class, sql, response, null, extInfo);
        if (!res.getErrors().isEmpty()) return null;
        else return success("导入成功!");
    }

    @PostMapping("/exportExcel")
    @Operation(summary = "导出年龄分布")
    public void exportExcel(HttpServletResponse response, @RequestParam("accountId") Long accountId) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("导出年龄分布", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        try {
            LambdaQueryWrapper<AgeDistribution> queryWrapper = new LambdaQueryWrapper<>();
            //TODO 仅支持单账号
            queryWrapper.eq(AgeDistribution::getAccountId, accountId);
            queryWrapper.last("ORDER BY toFloat32(replace(proportion, '%', '')) DESC");
            List<AgeDistribution> list = iAgeDistributionService.list(queryWrapper);

            EasyExcel.write(response.getOutputStream(), AgeDistribution.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("年龄分布")
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
