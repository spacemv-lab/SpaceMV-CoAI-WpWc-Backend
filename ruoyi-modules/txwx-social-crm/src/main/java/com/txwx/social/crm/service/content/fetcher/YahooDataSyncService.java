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
import com.txwx.social.crm.mapper.content.DataSourceMapper;
import com.txwx.social.crm.mapper.content.IndicatorDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class YahooDataSyncService {

    private static final String YAHOO_PROXY = "http://localhost:5001";
    private static final String SCREENER_URL = YAHOO_PROXY + "/v1/finance/screener/predefined/saved";
    private static final String QUOTE_URL = YAHOO_PROXY + "/v7/finance/quote";
    private static final Map<String, String> HEADERS = Map.of();

    private final DataSourceMapper dataSourceMapper;
    private final YahooDataFetcher yahooDataFetcher;
    private final IndicatorDataMapper indicatorDataMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> syncCategory(String categorySlug) {
        YahooConstants.YahooCategory cat = YahooConstants.CATEGORIES.get(categorySlug);
        if (cat == null) {
            return Map.of("success", false, "error", "未知分类: " + categorySlug);
        }

        try {
            List<String> tickers;
            if (cat.scrId() != null) {
                tickers = fetchTickersFromScreener(cat.scrId());
            } else if (cat.tickers() != null) {
                tickers = cat.tickers();
            } else {
                return Map.of("success", false, "error", "分类 " + categorySlug + " 没有定义tickers");
            }

            if (tickers.isEmpty()) {
                return Map.of("success", false, "error", "分类 " + cat.name() + " 未获取到tickers");
            }

            int[] counts = new int[]{0, 0, 0};

            for (int i = 0; i < tickers.size(); i += 50) {
                List<String> batch = tickers.subList(i, Math.min(i + 50, tickers.size()));
                Map<String, YahooQuote> quotes = fetchQuotes(batch);
                for (String ticker : batch) {
                    YahooQuote quote = quotes.get(ticker);
                    processTicker(ticker, cat, quote, counts);
                }
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("category", cat.name());
            result.put("added", counts[0]);
            result.put("updated", counts[1]);
            result.put("dataPoints", counts[2]);
            result.put("success", true);
            return result;

        } catch (Exception e) {
            log.error("Yahoo category sync failed: {}", cat.name(), e);
            return Map.of("success", false, "error", e.getMessage());
        }
    }

    public Map<String, Object> syncAllCategories() {
        int totalAdded = 0, totalUpdated = 0, totalPoints = 0;
        List<String> errors = new ArrayList<>();
        for (String slug : YahooConstants.getAllCategorySlugs()) {
            Map<String, Object> result = syncCategory(slug);
            if (Boolean.TRUE.equals(result.get("success"))) {
                totalAdded += (int) result.getOrDefault("added", 0);
                totalUpdated += (int) result.getOrDefault("updated", 0);
                totalPoints += (int) result.getOrDefault("dataPoints", 0);
            } else {
                errors.add((String) result.get("error"));
            }
        }
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("success", errors.isEmpty());
        summary.put("added", totalAdded);
        summary.put("updated", totalUpdated);
        summary.put("dataPoints", totalPoints);
        if (!errors.isEmpty()) {
            summary.put("errors", errors);
        }
        return summary;
    }

    public void syncAllCategoriesAsync() {
        new Thread(() -> {
            try {
                syncAllCategories();
            } catch (Exception e) {
                log.error("Async Yahoo sync failed", e);
            }
        }).start();
    }

    /**
     * 同步所有 Yahoo 数据源的时序数据（兜底，用于初始填补或手动触发）
     */
    public Map<String, Object> syncAllData() {
        List<DataSourcePO> yahooSources = dataSourceMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<DataSourcePO>()
                        .eq(DataSourcePO::getType, "yahoo")
                        .eq(DataSourcePO::getEnabled, true));
        int total = yahooSources.size();
        int success = 0;
        int failed = 0;
        int totalPoints = 0;
        for (DataSourcePO source : yahooSources) {
            try {
                List<Map<String, Object>> points = yahooDataFetcher.fetch(source);
                for (Map<String, Object> pt : points) {
                    pt.put("slug", source.getSlug());
                }
                if (!points.isEmpty()) {
                    if (points.size() == 1) {
                        indicatorDataMapper.upsert(points.get(0));
                    } else {
                        indicatorDataMapper.batchUpsert(points);
                    }
                    source.setLastSyncAt(new Date());
                    source.setLastSyncStatus("success");
                    source.setLastSyncMessage(null);
                    totalPoints += points.size();
                }
                source.setUpdatedAt(new Date());
                dataSourceMapper.updateById(source);
                success++;
                log.info("Yahoo data sync success: {} ({}), {} points", source.getName(), source.getId(), points.size());
            } catch (Exception e) {
                log.warn("Yahoo data sync failed: {} ({})", source.getName(), source.getId(), e);
                source.setLastSyncStatus("error");
                source.setLastSyncMessage(e.getMessage());
                source.setUpdatedAt(new Date());
                dataSourceMapper.updateById(source);
                failed++;
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", total);
        result.put("success", success);
        result.put("failed", failed);
        result.put("dataPoints", totalPoints);
        log.info("Yahoo bulk data sync finished: total={}, success={}, failed={}, points={}", total, success, failed, totalPoints);
        return result;
    }

    public void syncAllDataAsync() {
        new Thread(() -> {
            try {
                syncAllData();
            } catch (Exception e) {
                log.error("Async Yahoo bulk data sync failed", e);
            }
        }).start();
    }

    private List<String> fetchTickersFromScreener(String scrId) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("scrId", scrId);
        body.put("count", YahooConstants.SCREENER_COUNT);
        String json = HttpUtil.postJson(SCREENER_URL, HEADERS, body);
        JsonNode root = objectMapper.readTree(json);
        JsonNode finance = root.path("finance");
        JsonNode result = finance.path("result");
        if (result == null || !result.isArray()) return Collections.emptyList();

        List<String> tickers = new ArrayList<>();
        for (JsonNode item : result) {
            String symbol = item.path("symbol").asText();
            if (!symbol.isBlank()) {
                tickers.add(symbol);
            }
        }
        return tickers;
    }

    private Map<String, YahooQuote> fetchQuotes(List<String> tickers) throws Exception {
        String symbolsStr = String.join(",", tickers);
        String url = QUOTE_URL + "?symbols=" + URLEncoder.encode(symbolsStr, StandardCharsets.UTF_8);
        String json = HttpUtil.get(url, HEADERS);
        JsonNode root = objectMapper.readTree(json);
        JsonNode quoteResponse = root.path("quoteResponse");
        JsonNode result = quoteResponse.path("result");
        if (result == null || !result.isArray()) return Collections.emptyMap();

        Map<String, YahooQuote> quotes = new LinkedHashMap<>();
        for (JsonNode item : result) {
            String symbol = item.path("symbol").asText();
            String name = item.path("shortName").asText(null);
            if (name == null || name.isBlank()) {
                name = item.path("longName").asText(symbol);
            }
            String currency = item.path("currency").asText(null);
            if (currency == null) currency = "";
            quotes.put(symbol, new YahooQuote(name, currency));
        }
        return quotes;
    }

    private void processTicker(String ticker, YahooConstants.YahooCategory cat,
                                YahooQuote quote, int[] counts) {
        String name = quote != null ? quote.name() : ticker;
        String currency = quote != null ? quote.currency() : "";

        boolean exists = dataSourceMapper.countBySlug(ticker) > 0;

        DataSourcePO ds;
        if (!exists) {
            ds = new DataSourcePO();
            ds.setId(ticker);
            ds.setName(name);
            ds.setSlug(ticker);
            ds.setType("yahoo");
            ds.setConfig(Map.of("ticker", ticker, "category", cat.name()));
            ds.setUnit(currency);
            ds.setDisplayUnit(currency);
            ds.setRegion(cat.region());
            ds.setCategory(cat.name());
            ds.setTags(List.of("yahoo", cat.slug(), cat.name()));
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
        } else {
            ds = dataSourceMapper.selectById(ticker);
            if (ds != null) {
                ds.setCategory(cat.name());
                ds.setTags(List.of("yahoo", cat.slug(), cat.name()));
                ds.setUpdatedAt(new Date());
                dataSourceMapper.updateById(ds);
            }
            counts[1]++;
        }

        // 同步元数据后，立即抓取行情数据写入 indicator_data
        if (ds != null) {
            try {
                List<Map<String, Object>> points = yahooDataFetcher.fetch(ds);
                for (Map<String, Object> pt : points) {
                    pt.put("slug", ds.getSlug());
                }
                if (!points.isEmpty()) {
                    if (points.size() == 1) {
                        indicatorDataMapper.upsert(points.get(0));
                    } else {
                        indicatorDataMapper.batchUpsert(points);
                    }
                    ds.setLastSyncAt(new Date());
                    ds.setLastSyncStatus("success");
                    ds.setLastSyncMessage(null);
                    dataSourceMapper.updateById(ds);
                    counts[2] += points.size();
                }
            } catch (Exception e) {
                log.warn("Failed to fetch data for Yahoo ticker {}: {}", ticker, e.getMessage());
                if (ds != null) {
                    ds.setLastSyncStatus("error");
                    ds.setLastSyncMessage(e.getMessage());
                    dataSourceMapper.updateById(ds);
                }
            }
        }
    }

    record YahooQuote(String name, String currency) {}
}
