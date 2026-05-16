/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.dashboard.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.txwx.social.dashboard.domain.entity.ChannelComposition;
import com.txwx.social.dashboard.domain.vo.ImportResultVo;
import com.txwx.social.dashboard.exception.ImportException;
import com.txwx.social.dashboard.service.IChannelCompositionService;
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
@RequestMapping("/channelData")
@Tag(name = "01--【微信运营】--渠道构成数据")
public class ChannelCompositionController extends BaseController {

    @Autowired
    private ImportUtil importUtil;
    @Autowired
    private IChannelCompositionService iChannelCompositionService;
    @Autowired
    private ClickhouseService clickhouseService;

    @PreAuthorize("@ss.hasPermi('media:mediaProductData:api')")
    @PostMapping("/list")
    @Operation(summary = "渠道构成列表")
    public AjaxResult select(@RequestParam("accountId") List<Long> accountIds) {
        LambdaQueryWrapper<ChannelComposition> queryWrapper = new LambdaQueryWrapper<>();
        //TODO 仅支持单账号
        queryWrapper.eq(ChannelComposition::getAccountId, accountIds.get(0));
        queryWrapper.last("ORDER BY toFloat32(replace(proportion, '%', '')) DESC");
        return success(iChannelCompositionService.list(queryWrapper));
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
            String fileName = URLEncoder.encode("渠道构成数据导入模板", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

            EasyExcel.write(response.getOutputStream(), ChannelComposition.class)
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
    @Operation(summary = "导入渠道构成数据")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file, HttpServletResponse response, @RequestParam("accountId") Long accountId) throws Exception {
        String truncateSql = SqlUtils.deleteSql("dim_channel_composition");
        try {
            clickhouseService.singleInsert(truncateSql, accountId);
            logger.info("清空channel_composition表成功");
        }catch (Exception e) {
            logger.warn("清空channel_composition表失败（可能是第一次运行）: " + e.getMessage());
        }
        String sql = "insert into dim_channel_composition (channel, user_number, proportion, account_id) values (?, ?, ?, ?)";
        Map<String, Object> extInfo = new HashMap<>();
        extInfo.put("accountId", accountId);
        try {
            ImportResultVo res = importUtil.importExcel(file, ChannelComposition.class, sql, null, extInfo);
            if (res.getSkippedCount() != null && res.getSkippedCount() > 0) {
                return success("导入成功，但跳过了 " + res.getSkippedCount() + " 条重复数据");
            }
            return success("导入成功!");
        } catch (ImportException e) {
            byte[] errorData = importUtil.exportErrorList(ChannelComposition.class, e.getErrors(), extInfo);
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
    @Operation(summary = "导出渠道构成")
    public void exportExcel(HttpServletResponse response, @RequestParam("accountId") Long accountId) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("导出渠道构成", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        try {
            LambdaQueryWrapper<ChannelComposition> queryWrapper = new LambdaQueryWrapper<>();
            //TODO 仅支持单账号
            queryWrapper.eq(ChannelComposition::getAccountId, accountId);
            queryWrapper.last("ORDER BY toFloat32(replace(proportion, '%', '')) DESC");
            List<ChannelComposition> list = iChannelCompositionService.list(queryWrapper);

            EasyExcel.write(response.getOutputStream(), ChannelComposition.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("渠道构成")
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
