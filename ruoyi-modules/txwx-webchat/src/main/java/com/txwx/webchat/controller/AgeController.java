package com.txwx.webchat.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.webchat.domain.entity.AgeDistribution;
import com.txwx.webchat.domain.entity.SexDistribution;
import com.txwx.webchat.domain.vo.ImportResultVo;
import com.txwx.webchat.service.IAgeDistributionService;
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
@RequestMapping("/ageData")
@Tag(name = "01--【微信运营】--年龄分布数据")
public class AgeController extends BaseController {

    @Autowired
    private ImportServiceImpl importServiceImpl;
    @Autowired
    private ClickhouseService clickhouseService;
    @Autowired
    private IAgeDistributionService iAgeDistributionService;

    @PostMapping("/list")
    @Operation(summary = "年龄分布列表")
    public AjaxResult select() {
        LambdaQueryWrapper<AgeDistribution> queryWrapper = new LambdaQueryWrapper<>();
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

            // 2. 核心魔法：传一个空的 List 进去！
            // EasyExcel 会根据 DwsUsers.class 的注解自动画出表头，但因为数据是空的，所以刚好就是个完美的模板
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
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file, HttpServletResponse response) throws Exception {
        String truncateSql = "truncate table age_distribution";
        try {
            clickhouseService.singleInsert(truncateSql);
            logger.info("清空age_distribution表成功");
        }catch (Exception e) {
            logger.warn("清空age_distribution表失败（可能是第一次运行）: " + e.getMessage());
        }
        String sql = "insert into age_distribution (age, user_number, proportion) values (?, ?, ?)";
        ImportResultVo res = importServiceImpl.importExcel(file, AgeDistribution.class, sql, response, null);
        if (res.getErrors().size() > 0) return null;
        else return success("导入成功!");
    }

    @PostMapping("/exportExcel")
    @Operation(summary = "导出年龄分布")
    public void exportExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("导出年龄分布", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        try {
            LambdaQueryWrapper<AgeDistribution> queryWrapper = new LambdaQueryWrapper<>();
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
