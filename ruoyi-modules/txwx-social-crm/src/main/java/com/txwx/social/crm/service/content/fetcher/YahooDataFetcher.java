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
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Slf4j
public class YahooDataFetcher implements DataFetcher {

    private static final String CHART_URL = "http://localhost:5001/v8/finance/chart";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(String type) {
        return "yahoo".equals(type);
    }

    @Override
    public List<Map<String, Object>> fetch(DataSourcePO source) throws Exception {
        String ticker = resolveTicker(source);
        if (ticker == null || ticker.isBlank()) {
            throw new IllegalArgumentException("Yahoo数据源缺少ticker配置");
        }
        String url = CHART_URL + "/" + URLEncoder.encode(ticker, StandardCharsets.UTF_8)
                + "?range=max&interval=1d";
        String json = HttpUtil.get(url);
        return parseYahooResponse(json, ticker);
    }

    private String resolveTicker(DataSourcePO source) {
        if (source.getConfig() instanceof Map) {
            Object t = ((Map<?, ?>) source.getConfig()).get("ticker");
            return t != null ? t.toString() : null;
        }
        return null;
    }

    private List<Map<String, Object>> parseYahooResponse(String json, String ticker) throws Exception {
        JsonNode root = objectMapper.readTree(json);
        JsonNode result = root.path("chart").path("result").get(0);
        if (result == null) return Collections.emptyList();

        JsonNode timestamps = result.get("timestamp");
        JsonNode closes = result.path("indicators").path("quote").get(0).path("close");
        if (timestamps == null || closes == null) return Collections.emptyList();

        String sourceUrl = "https://finance.yahoo.com/chart/" + ticker.replace("^", "%5E");
        String metadata = "{\"sourceUrl\":\"" + sourceUrl + "\"}";

        List<Map<String, Object>> points = new ArrayList<>();
        for (int i = 0; i < timestamps.size() && i < closes.size(); i++) {
            if (closes.get(i).isNull()) continue;
            String date = new java.text.SimpleDateFormat("yyyy-MM-dd")
                    .format(new java.util.Date(timestamps.get(i).asLong() * 1000));
            double value = closes.get(i).asDouble();
            if (!Double.isFinite(value)) continue;

            Map<String, Object> point = new LinkedHashMap<>();
            point.put("date", date);
            point.put("value", value);
            point.put("rawValue", value);
            point.put("metadata", metadata);
            points.add(point);
        }
        return points;
    }
}
