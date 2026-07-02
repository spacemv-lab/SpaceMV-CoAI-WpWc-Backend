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
        @RequestParam(required = false) String region,
        @RequestParam(required = false) String category,
        @RequestParam(defaultValue = "name") String sort,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "50") int pageSize
    ) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> items = indicatorsMapper.searchWithValues(keyword, region, category, pageSize, offset);
        int total = indicatorsMapper.countSearch(keyword, region, category);

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
    @Operation(summary = "获取指标筛选选项（地区+分类）")
    public AjaxResult getFilterOptions() {
        List<Map<String, Object>> regionRows = indicatorsMapper.selectRegions();
        List<Map<String, Object>> categoryRows = indicatorsMapper.selectCategories();

        List<Map<String, String>> regions = new ArrayList<>();
        for (Map<String, Object> row : regionRows) {
            regions.add(Map.of("label", (String) row.get("label"), "value", (String) row.get("code")));
        }

        List<Map<String, String>> categories = new ArrayList<>();
        for (Map<String, Object> row : categoryRows) {
            categories.add(Map.of("label", (String) row.get("label"), "value", (String) row.get("code")));
        }

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
