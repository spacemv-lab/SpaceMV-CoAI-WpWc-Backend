/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.common.clickhouse.service;

import com.ruoyi.common.core.constant.HttpStatus;
import com.ruoyi.common.core.web.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ClickhousePageHelper {

    @Autowired
    private ClickhouseService clickhouseService;

    public TableDataInfo queryForPage(String sql, int pageNum, int pageSize, Object... params) {
    // 最简单的安全检查：只允许SELECT开头，且不包含危险操作
    String sqlUpper = sql.trim().toUpperCase();
    
    // 1. 必须是SELECT开头
    if (!sqlUpper.startsWith("SELECT ")) {  // SELECT后面必须有空格
        throw new SecurityException("只允许SELECT查询");
    }
    
    // 2. 简单黑名单检查
    if (sqlUpper.contains(";") || 
        sqlUpper.contains("DELETE ") || 
        sqlUpper.contains("DROP ") || 
        sqlUpper.contains("UPDATE ") || 
        sqlUpper.contains("INSERT ")) {
        throw new SecurityException("SQL包含危险操作");
    }
    
    // 1. 查询总数
        String countSql = "SELECT COUNT(1) as total FROM (" + sql + ") t";
        List<Map<String, Object>> countResult = clickhouseService.readData(countSql, params);
        long total = 0L;
        if (!countResult.isEmpty()) {
            total = Long.parseLong(countResult.get(0).get("total").toString());
        }

        // 2. 构建分页SQL（ClickHouse语法）
        String pageSql = sql + " LIMIT " + pageSize + " OFFSET " + (pageNum - 1) * pageSize;

        // 3. 查询分页数据
        List<Map<String, Object>> data = clickhouseService.readData(pageSql, params);

        // 4. 返回TableDataInfo
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(data);
        rspData.setTotal(total);
        rspData.setMsg("查询成功");
        return rspData;
    }
}