/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.service.content.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.common.datasource.annotation.Postgres;
import com.txwx.social.crm.domain.po.DataSourcePO;
import com.txwx.social.crm.mapper.content.DataSourceMapper;
import com.txwx.social.crm.mapper.content.IndicatorDataMapper;
import com.txwx.social.crm.mapper.content.SyncLogMapper;
import com.txwx.social.crm.domain.po.SyncLogPO;
import com.txwx.social.crm.service.content.IDataSourceService;
import com.txwx.social.crm.service.content.fetcher.DataFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Postgres
public class DataSourceServiceImpl implements IDataSourceService {

    private final DataSourceMapper dataSourceMapper;
    private final IndicatorDataMapper indicatorDataMapper;
    private final SyncLogMapper syncLogMapper;
    private final List<DataFetcher> fetchers;
    private final JdbcTemplate jdbcTemplate;

    @org.springframework.beans.factory.annotation.Value("${wendao.fred.api-key:}")
    private String fredApiKey;

    private static final java.text.SimpleDateFormat DT_FMT = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String FRED_API_BASE = "https://api.stlouisfed.org/fred";
    private static final int RECENT_DAYS = 7;

    @Override
    public Map<String, Object> pageList(Map<String, Object> params) {
        int pageNum = params.get("pageNum") instanceof Number ? ((Number) params.get("pageNum")).intValue() : 1;
        int pageSize = params.get("pageSize") instanceof Number ? ((Number) params.get("pageSize")).intValue() : 20;
        if ("custom".equals(params.get("type"))) {
            params.remove("type");
            params.put("types", List.of("csv", "manual"));
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> list = dataSourceMapper.selectListWithStatus(params);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rows", list);
        result.put("total", pageInfo.getTotal());
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        return result;
    }

    @Override
    public DataSourcePO getById(String id) {
        return dataSourceMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DataSourcePO dataSource) {
        if (dataSource.getSlug() != null && !dataSource.getSlug().isBlank()) {
            if (dataSourceMapper.countBySlug(dataSource.getSlug()) > 0) {
                throw new IllegalArgumentException("slug 已存在: " + dataSource.getSlug());
            }
        }
        String username = SecurityUtils.getUsername();
        dataSource.setCreateBy(username);
        dataSource.setUpdatedBy(username);
        dataSource.setLastSyncStatus("never");
        if (dataSource.getEnabled() == null) dataSource.setEnabled(true);
        if (dataSource.getIsPublic() == null) dataSource.setIsPublic(true);
        if (dataSource.getProOnly() == null) dataSource.setProOnly(false);
        if (dataSource.getTransform() == null) dataSource.setTransform("none");
        dataSource.setCreateTime(new Date());
        dataSource.setUpdatedAt(new Date());
        dataSourceMapper.insert(dataSource);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(String id, DataSourcePO dataSource) {
        dataSource.setId(id);
        dataSource.setUpdatedBy(SecurityUtils.getUsername());
        dataSource.setUpdatedAt(new Date());
        dataSourceMapper.updateById(dataSource);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        DataSourcePO ds = new DataSourcePO();
        ds.setId(id);
        ds.setEnabled(false);
        ds.setUpdatedBy(SecurityUtils.getUsername());
        ds.setUpdatedAt(new Date());
        dataSourceMapper.updateById(ds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> sync(String id, String triggerType) {
        DataSourcePO source = dataSourceMapper.selectById(id);
        if (source == null) {
            return Map.of("success", false, "error", "数据源不存在");
        }
        if (!source.getEnabled()) {
            return Map.of("success", false, "error", "数据源已停用");
        }

        Date startedAt = new Date();
        String username = SecurityUtils.getUsername();
        int dataPoints = 0;

        try {
            DataFetcher fetcher = fetchers.stream()
                    .filter(f -> f.supports(source.getType()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("不支持的数据源类型: " + source.getType()));

            List<Map<String, Object>> points = fetcher.fetch(source);

            for (Map<String, Object> pt : points) {
                pt.put("slug", source.getSlug());
                if (!pt.containsKey("metadata")) pt.put("metadata", null);
            }
            if (!points.isEmpty()) {
                if (points.size() == 1) {
                    indicatorDataMapper.upsert(points.get(0));
                } else {
                    indicatorDataMapper.batchUpsert(points);
                }
                dataPoints = points.size();
            }

            source.setLastSyncAt(new Date());
            source.setLastSyncStatus("success");
            source.setLastSyncMessage(null);
            source.setUpdatedAt(new Date());
            dataSourceMapper.updateById(source);

            insertSyncLog(source.getId(), "success", triggerType, dataPoints, null, startedAt, username);

            log.info("Sync success: {} ({}), {} points", source.getName(), source.getId(), dataPoints);
            return Map.of("success", true, "dataPoints", dataPoints);

        } catch (Exception e) {
            log.error("Sync failed: {} ({})", source.getName(), source.getId(), e);

            source.setLastSyncStatus("error");
            source.setLastSyncMessage(e.getMessage());
            source.setUpdatedAt(new Date());
            dataSourceMapper.updateById(source);

            insertSyncLog(source.getId(), "error", triggerType, 0, e.getMessage(), startedAt, username);

            return Map.of("success", false, "error", e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importCsv(String id, MultipartFile file) {
        DataSourcePO source = dataSourceMapper.selectById(id);
        if (source == null) {
            return Map.of("success", false, "error", "数据源不存在");
        }
        Date startedAt = new Date();
        String username = SecurityUtils.getUsername();
        List<Map<String, Object>> points = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                return Map.of("success", false, "error", "CSV文件为空");
            }
            String[] headers = headerLine.split(",");
            int dateIdx = -1, valueIdx = -1;
            for (int i = 0; i < headers.length; i++) {
                String h = headers[i].trim().toLowerCase();
                if (h.contains("date")) dateIdx = i;
                if (h.contains("value")) valueIdx = i;
            }
            if (dateIdx < 0 || valueIdx < 0) {
                return Map.of("success", false, "error", "CSV需要包含date和value两列");
            }

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isBlank()) continue;
                String[] cols = line.split(",");
                if (cols.length <= Math.max(dateIdx, valueIdx)) continue;
                String dateStr = cols[dateIdx].trim();
                String valStr = cols[valueIdx].trim();
                if (dateStr.isBlank() || valStr.isBlank()) continue;
                try {
                    double value = Double.parseDouble(valStr);
                    if (!Double.isFinite(value)) continue;
                    Map<String, Object> pt = new LinkedHashMap<>();
                    pt.put("slug", source.getSlug());
                    pt.put("date", dateStr);
                    pt.put("value", value);
                    pt.put("rawValue", value);
                    points.add(pt);
                } catch (NumberFormatException ignored) {
                }
            }

            if (points.isEmpty()) {
                return Map.of("success", false, "error", "CSV中未解析到有效数据");
            }

            for (int i = 0; i < points.size(); i += 100) {
                int end = Math.min(i + 100, points.size());
                List<Map<String, Object>> batch = points.subList(i, end);
                StringBuilder sql = new StringBuilder("INSERT INTO indicator_data (slug, date, value, raw_value, source_type, updated_at) VALUES ");
                List<Object> params = new ArrayList<>();
                for (int j = 0; j < batch.size(); j++) {
                    if (j > 0) sql.append(", ");
                    sql.append("(?, ?::date, ?::numeric, ?::numeric, 'manual', now())");
                    Map<String, Object> p = batch.get(j);
                    params.add(source.getSlug());
                    params.add(p.get("date"));
                    params.add(p.get("value"));
                    params.add(p.get("rawValue"));
                }
                sql.append(" ON CONFLICT (slug, date) DO UPDATE SET value = EXCLUDED.value, raw_value = EXCLUDED.raw_value, source_type = 'manual', updated_at = now()");
                jdbcTemplate.update(sql.toString(), params.toArray());
            }

            source.setLastSyncAt(new Date());
            source.setLastSyncStatus("success");
            source.setLastSyncMessage("CSV导入: " + points.size() + "条");
            source.setUpdatedAt(new Date());
            dataSourceMapper.updateById(source);

            insertSyncLog(source.getId(), "success", "manual", points.size(), null, startedAt, username);

            log.info("CSV import: {} ({}), {} points", source.getName(), source.getId(), points.size());
            return Map.of("success", true, "dataPoints", points.size());

        } catch (Exception e) {
            log.error("CSV import failed: {} ({})", source.getName(), source.getId(), e);

            source.setLastSyncStatus("error");
            source.setLastSyncMessage("CSV导入失败: " + e.getMessage());
            source.setUpdatedAt(new Date());
            dataSourceMapper.updateById(source);

            insertSyncLog(source.getId(), "error", "manual", 0, e.getMessage(), startedAt, username);

            return Map.of("success", false, "error", e.getMessage());
        }
    }

    @Override
    public List<SyncLogItem> getSyncLogs(String sourceId) {
        LambdaQueryWrapper<SyncLogPO> wrapper = new LambdaQueryWrapper<SyncLogPO>()
                .eq(sourceId != null && !sourceId.isBlank(), SyncLogPO::getSourceId, sourceId)
                .orderByDesc(SyncLogPO::getStartedAt);
        return syncLogMapper.selectList(wrapper).stream()
                .map(this::toSyncLogItem)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> syncCategory(int categoryId) {
        if (fredApiKey == null || fredApiKey.isBlank() || fredApiKey.startsWith("your-")) {
            return Map.of("success", false, "error", "FRED API Key 未配置，无法按分类同步");
        }
        String categoryName = com.txwx.social.crm.service.content.fetcher.FredConstants.categoryNameById(categoryId);
        try {
            Map<String, Object> result = new java.util.LinkedHashMap<>();
            result.put("category", categoryName);
            int[] counts = new int[]{0, 0, 0};
            fetchSeriesRecursive(categoryId, categoryName, counts);
            result.put("added", counts[0]);
            result.put("updated", counts[1]);
            result.put("dataPoints", counts[2]);
            result.put("success", counts[0] + counts[1] > 0);
            if (counts[0] + counts[1] == 0) {
                result.put("error", "分类 " + categoryName + " 未找到系列数据");
            }
            return result;
        } catch (Exception e) {
            log.error("FRED category sync failed: {}", categoryName, e);
            return Map.of("success", false, "error", e.getMessage());
        }
    }

    private void fetchSeriesRecursive(int categoryId, String categoryName, int[] counts) {
        String url = FRED_API_BASE + "/category/series?category_id=" + categoryId
                + "&api_key=" + fredApiKey + "&file_type=json"
                + "&sort_order=desc&order_by=popularity"
                + "&limit=" + com.txwx.social.crm.service.content.fetcher.FredConstants.LIMIT_PER_CATEGORY;
        try {
            String json = com.ruoyi.common.http.service.HttpUtil.get(url);
            com.fasterxml.jackson.databind.JsonNode root = new com.fasterxml.jackson.databind.ObjectMapper().readTree(json);
            com.fasterxml.jackson.databind.JsonNode series = root.get("seriess");
            if (series != null && series.isArray() && series.size() > 0) {
                processSeries(series, categoryId, categoryName, counts);
            }
        } catch (Exception e) {
            log.warn("Failed to fetch series for category {}: {}", categoryId, e.getMessage());
        }

        String childrenUrl = FRED_API_BASE + "/category/children?category_id=" + categoryId
                + "&api_key=" + fredApiKey + "&file_type=json";
        try {
            String json = com.ruoyi.common.http.service.HttpUtil.get(childrenUrl);
            com.fasterxml.jackson.databind.JsonNode root = new com.fasterxml.jackson.databind.ObjectMapper().readTree(json);
            com.fasterxml.jackson.databind.JsonNode categories = root.get("categories");
            if (categories != null && categories.isArray()) {
                for (com.fasterxml.jackson.databind.JsonNode cat : categories) {
                    int childId = cat.get("id").asInt();
                    String childName = cat.has("name") ? cat.get("name").asText() : String.valueOf(childId);
                    fetchSeriesRecursive(childId, childName, counts);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to fetch children for category {}: {}", categoryId, e.getMessage());
        }
    }

    private void processSeries(com.fasterxml.jackson.databind.JsonNode series, int categoryId, String categoryName, int[] counts) {
        String mappedCat = com.txwx.social.crm.service.content.fetcher.FredConstants.mapCategory(categoryId);

        for (com.fasterxml.jackson.databind.JsonNode s : series) {
            String seriesId = s.get("id").asText();
            String title = s.has("title") ? s.get("title").asText() : seriesId;
            String units = s.has("units_short") ? s.get("units_short").asText() : "";

            boolean exists = dataSourceMapper.countBySlug(seriesId) > 0;

            if (!exists) {
                DataSourcePO ds = new DataSourcePO();
                ds.setId(seriesId);
                ds.setName(title);
                ds.setSlug(seriesId);
                ds.setType("fred");
                ds.setConfig(Map.of("seriesId", seriesId));
                ds.setUnit(units);
                ds.setRegion("us");
                ds.setCategory(mappedCat);
                ds.setTransform("none");
                ds.setSchedule("");
                ds.setEnabled(true);
                ds.setIsPublic(true);
                ds.setProOnly(false);
                ds.setLastSyncStatus("never");
                ds.setCreateBy("system");
                ds.setCreateTime(new Date());
                ds.setUpdatedBy("system");
                ds.setUpdatedAt(new Date());
                dataSourceMapper.insert(ds);
                counts[0]++;
            }

            DataSourcePO source = dataSourceMapper.selectById(seriesId);
            if (source == null) continue;

            try {
                String obsUrl = FRED_API_BASE + "/series/observations?series_id=" + seriesId
                        + "&api_key=" + fredApiKey + "&file_type=json"
                        + "&sort_order=desc&limit=100";
                String obsJson = com.ruoyi.common.http.service.HttpUtil.get(obsUrl);
                com.fasterxml.jackson.databind.JsonNode obsRoot = new com.fasterxml.jackson.databind.ObjectMapper().readTree(obsJson);
                com.fasterxml.jackson.databind.JsonNode observations = obsRoot.get("observations");
                if (observations != null && observations.isArray()) {
                    String sourceUrl = "https://fred.stlouisfed.org/series/" + seriesId;
                    String metadata = "{\"sourceUrl\":\"" + sourceUrl + "\"}";
                    int count = 0;
                    for (com.fasterxml.jackson.databind.JsonNode obs : observations) {
                        String date = obs.get("date").asText();
                        String valueStr = obs.get("value").asText();
                        if (".".equals(valueStr)) continue;
                        try {
                            double value = Double.parseDouble(valueStr);
                            if (!Double.isFinite(value)) continue;
                            Map<String, Object> pt = new java.util.LinkedHashMap<>();
                            pt.put("slug", seriesId);
                            pt.put("date", date);
                            pt.put("value", value);
                            pt.put("rawValue", value);
                            pt.put("metadata", metadata);
                            indicatorDataMapper.upsert(pt);
                            count++;
                            if (count >= 100) break;
                        } catch (NumberFormatException ignored) {}
                    }
                    counts[2] += count;
                }
                counts[1]++;
            } catch (Exception e) {
                log.warn("Failed to fetch data for {}: {}", seriesId, e.getMessage());
            }
        }

        log.info("FRED series processed for {} ({}), added={}, updated={}, points={}",
                categoryName, categoryId, counts[0], counts[1], counts[2]);
    }

    @Override
    public Map<String, Object> syncAllCategories() {
        int totalAdded = 0, totalUpdated = 0, totalPoints = 0;
        List<String> errors = new java.util.ArrayList<>();
        for (int catId : com.txwx.social.crm.service.content.fetcher.FredConstants.getAllCategoryIds()) {
            Map<String, Object> result = syncCategory(catId);
            if (Boolean.TRUE.equals(result.get("success"))) {
                totalAdded += (int) result.getOrDefault("added", 0);
                totalUpdated += (int) result.getOrDefault("updated", 0);
                totalPoints += (int) result.getOrDefault("dataPoints", 0);
            } else {
                errors.add((String) result.get("error"));
            }
        }
        Map<String, Object> summary = new java.util.LinkedHashMap<>();
        summary.put("success", errors.isEmpty());
        summary.put("added", totalAdded);
        summary.put("updated", totalUpdated);
        summary.put("dataPoints", totalPoints);
        if (!errors.isEmpty()) {
            summary.put("errors", errors);
        }
        return summary;
    }

    @Override
    public void syncAllCategoriesAsync() {
        new Thread(() -> {
            try {
                syncAllCategories();
            } catch (Exception e) {
                log.error("Async FRED sync failed", e);
            }
        }).start();
    }

    private void insertSyncLog(String sourceId, String status, String triggerType,
                                int dataPoints, String errorMsg,
                                Date startedAt, String username) {
        SyncLogPO log = new SyncLogPO();
        log.setSourceId(sourceId);
        log.setStatus(status);
        log.setTriggerType(triggerType);
        log.setDataPoints(dataPoints);
        log.setErrorMsg(errorMsg);
        log.setStartedAt(startedAt);
        log.setFinishedAt(new Date());
        log.setCreatedBy(username);
        syncLogMapper.insert(log);
    }

    private SyncLogItem toSyncLogItem(SyncLogPO po) {
        return new SyncLogItem(
                po.getId(), po.getSourceId(), po.getStatus(), po.getTriggerType(),
                po.getDataPoints(), po.getErrorMsg(),
                po.getStartedAt() != null ? DT_FMT.format(po.getStartedAt()) : null,
                po.getFinishedAt() != null ? DT_FMT.format(po.getFinishedAt()) : null,
                po.getCreatedBy()
        );
    }
}
