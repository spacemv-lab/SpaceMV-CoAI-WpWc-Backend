package com.txwx.social.crm.controller.content;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.social.crm.mapper.content.IndicatorsMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/content/site/wendao/charts")
@Tag(name = "内容中心-问道图表")
@RequiredArgsConstructor
public class ContentWendaoChartController extends BaseController {

    private final IndicatorsMapper indicatorsMapper;

    @GetMapping("/indicators")
    @Operation(summary = "查询公开指标列表")
    public AjaxResult listIndicators(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String region,
        @RequestParam(required = false) String category,
        @RequestParam(defaultValue = "name") String sort,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "50") int pageSize
    ) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> items = indicatorsMapper.searchWithValues(keyword, type, region, category, pageSize, offset);
        int total = indicatorsMapper.countSearch(keyword, type, region, category);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("items", items);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return AjaxResult.success(result);
    }

    @GetMapping("/indicator/{slug}")
    @Operation(summary = "查询单个指标详情+时序数据")
    public AjaxResult getIndicator(@PathVariable String slug) {
        Map<String, Object> indicator = indicatorsMapper.findBySlug(slug);
        if (indicator == null) {
            return AjaxResult.error("指标不存在");
        }

        List<Map<String, Object>> seriesData = indicatorsMapper.findSeriesBySlug(slug, 500);
        // reverse to ascending order
        Collections.reverse(seriesData);

        Map<String, Object> chart = new LinkedHashMap<>();
        chart.put("name", indicator.get("name"));
        chart.put("description", indicator.get("name") + " 时序数据");
        chart.put("unit", indicator.get("unit"));
        chart.put("data", seriesData);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("indicator", indicator);
        result.put("chart", chart);
        return AjaxResult.success(result);
    }

    @GetMapping("/filter-options")
    @Operation(summary = "获取指标筛选选项（地区+分类+类型）")
    public AjaxResult getFilterOptions() {
        List<Map<String, String>> regions = List.of(
            Map.of("label", "中国", "value", "cn"),
            Map.of("label", "美国", "value", "us"),
            Map.of("label", "全球", "value", "global")
        );

        List<Map<String, String>> categories = new ArrayList<>();
        categories.add(Map.of("label", "物价", "value", "price"));
        categories.add(Map.of("label", "就业", "value", "employment"));
        categories.add(Map.of("label", "制造业", "value", "manufacturing"));
        categories.add(Map.of("label", "贸易", "value", "trade"));
        categories.add(Map.of("label", "货币", "value", "monetary"));
        categories.add(Map.of("label", "市场", "value", "market"));
        categories.add(Map.of("label", "World Indices", "value", "World Indices"));
        categories.add(Map.of("label", "Bonds", "value", "Bonds"));
        categories.add(Map.of("label", "Currencies", "value", "Currencies"));
        categories.add(Map.of("label", "Options", "value", "Options"));
        categories.add(Map.of("label", "Sectors", "value", "Sectors"));
        categories.add(Map.of("label", "Stocks", "value", "Stocks"));
        categories.add(Map.of("label", "Crypto", "value", "Crypto"));
        categories.add(Map.of("label", "Private Companies", "value", "Private Companies"));
        categories.add(Map.of("label", "ETFs", "value", "ETFs"));
        categories.add(Map.of("label", "Futures", "value", "Futures"));
        categories.add(Map.of("label", "Mutual Funds", "value", "Mutual Funds"));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("regions", regions);
        result.put("categories", categories);
        return AjaxResult.success(result);
    }

    @GetMapping("/indicator/{slug}/series")
    @Operation(summary = "查询指标原始时序数据")
    public AjaxResult getIndicatorSeries(
        @PathVariable String slug,
        @RequestParam(defaultValue = "500") int limit
    ) {
        List<Map<String, Object>> seriesData = indicatorsMapper.findSeriesBySlug(slug, limit);
        Collections.reverse(seriesData);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("slug", slug);
        result.put("series", seriesData);
        return AjaxResult.success(result);
    }
}
