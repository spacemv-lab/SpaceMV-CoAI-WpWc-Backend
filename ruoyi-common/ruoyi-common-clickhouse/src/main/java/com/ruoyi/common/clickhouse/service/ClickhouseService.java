package com.ruoyi.common.clickhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ClickhouseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void singleInsert(String sql) {
        jdbcTemplate.update(sql);
    }

    public void batchInsert(String sql, List<Object[]> batchArgs) {
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    public List<Map<String, Object>> readData(String sql) {
        return  jdbcTemplate.queryForList(sql);
    }
}
