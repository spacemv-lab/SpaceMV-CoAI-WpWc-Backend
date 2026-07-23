/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.service.content.fetcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.http.service.HttpUtil;
import com.txwx.social.crm.domain.po.DataSourcePO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class FredDataFetcher implements DataFetcher {

    private static final String API_URL = "https://api.stlouisfed.org/fred/series/observations";
    private static final String CSV_URL = "https://fred.stlouisfed.org/graph/fredgraph.csv";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${wendao.fred.api-key:}")
    private String fredApiKey;

    @Override
    public boolean supports(String type) {
        return "fred".equals(type);
    }

    @Override
    public List<Map<String, Object>> fetch(DataSourcePO source) throws Exception {
        String seriesId = resolveSeriesId(source);
        if (seriesId == null || seriesId.isBlank()) {
            throw new IllegalArgumentException("FRED数据源缺少seriesId配置");
        }
        String transform = source.getTransform() != null ? source.getTransform() : "none";
        List<Map<String, Object>> rawPoints;

        if (fredApiKey != null && !fredApiKey.isBlank() && !fredApiKey.startsWith("your-")) {
            rawPoints = fetchFromApi(seriesId);
        } else {
            rawPoints = fetchFromCsv(seriesId);
        }

        if ("yoy".equals(transform)) {
            rawPoints = applyYoyTransform(rawPoints);
        }
        return rawPoints;
    }

    private String resolveSeriesId(DataSourcePO source) {
        if (source.getConfig() instanceof Map) {
            Object sid = ((Map<?, ?>) source.getConfig()).get("seriesId");
            return sid != null ? sid.toString() : null;
        }
        return null;
    }

    private List<Map<String, Object>> fetchFromApi(String seriesId) throws Exception {
        String url = API_URL + "?series_id=" + seriesId
                + "&api_key=" + fredApiKey
                + "&file_type=json&sort_order=asc&limit=5000";
        String json = HttpUtil.get(url);
        JsonNode root = objectMapper.readTree(json);
        JsonNode observations = root.get("observations");
        String sourceUrl = "https://fred.stlouisfed.org/series/" + seriesId;
        List<Map<String, Object>> points = new ArrayList<>();
        if (observations != null && observations.isArray()) {
            for (JsonNode obs : observations) {
                String date = obs.get("date").asText();
                String valueStr = obs.get("value").asText();
                if (".".equals(valueStr)) continue;
                double value = Double.parseDouble(valueStr);
                if (!Double.isFinite(value)) continue;
                Map<String, Object> point = new LinkedHashMap<>();
                point.put("date", date);
                point.put("value", value);
                point.put("rawValue", value);
                point.put("metadata", "{\"sourceUrl\":\"" + sourceUrl + "\"}");
                points.add(point);
            }
        }
        return points;
    }

    private List<Map<String, Object>> fetchFromCsv(String seriesId) throws Exception {
        String csvUrl = CSV_URL + "?id=" + seriesId;
        String csv = HttpUtil.get(csvUrl);
        String sourceUrl = "https://fred.stlouisfed.org/series/" + seriesId;
        String metadata = "{\"sourceUrl\":\"" + sourceUrl + "\"}";
        List<Map<String, Object>> points = new ArrayList<>();
        String[] lines = csv.split("\\r?\\n");
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isBlank()) continue;
            String[] parts = line.split(",");
            if (parts.length < 2) continue;
            String date = parts[0].trim();
            String valueStr = parts[1].trim();
            if (".".equals(valueStr)) continue;
            try {
                double value = Double.parseDouble(valueStr);
                if (!Double.isFinite(value)) continue;
                Map<String, Object> point = new LinkedHashMap<>();
                point.put("date", date);
                point.put("value", value);
                point.put("rawValue", value);
                point.put("metadata", metadata);
                points.add(point);
            } catch (NumberFormatException ignored) {
            }
        }
        return points;
    }

    static List<Map<String, Object>> applyYoyTransform(List<Map<String, Object>> points) {
        Map<String, Double> byDate = new LinkedHashMap<>();
        for (Map<String, Object> p : points) {
            byDate.put((String) p.get("date"), (Double) p.get("value"));
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> p : points) {
            String date = (String) p.get("date");
            int year = Integer.parseInt(date.substring(0, 4)) - 1;
            String prevDate = year + date.substring(4);
            Double prev = byDate.get(prevDate);
            if (prev == null || prev == 0) continue;
            double current = (Double) p.get("value");
            double yoy = ((current - prev) / prev) * 100;
            if (!Double.isFinite(yoy)) continue;
            Map<String, Object> transformed = new LinkedHashMap<>(p);
            transformed.put("value", yoy);
            result.add(transformed);
        }
        return result;
    }
}
