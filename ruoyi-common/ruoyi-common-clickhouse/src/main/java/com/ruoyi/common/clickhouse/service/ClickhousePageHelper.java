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

    /**
     * 分页查询
     * @param sql 原始SQL
     * @param params 参数
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return PageDomain包含数据和分页信息
     */
    public TableDataInfo queryForPage(String sql, int pageNum, int pageSize, Object... params) {
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
