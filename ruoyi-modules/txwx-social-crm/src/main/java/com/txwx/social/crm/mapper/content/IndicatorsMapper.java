package com.txwx.social.crm.mapper.content;

import com.ruoyi.common.datasource.annotation.Postgres;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Postgres
public interface IndicatorsMapper {

    @Select("SELECT slug, name, unit, region, category, tags " +
            "FROM indicators WHERE enabled = true " +
            "ORDER BY name ASC")
    List<Map<String, Object>> listAll();

    @Select({"SELECT i.slug, i.name, i.unit, i.region, i.category, i.tags, " +
            "id.date AS latest_date, id.value AS latest_value " +
            "FROM indicators i " +
            "LEFT JOIN LATERAL (SELECT date, value FROM indicator_data " +
            "  WHERE slug = i.slug ORDER BY date DESC LIMIT 1) id ON true " +
            "WHERE i.enabled = true " +
            "ORDER BY i.name ASC"})
    List<Map<String, Object>> listWithValues();

    @Select("SELECT slug, name, unit, region, category, tags " +
            "FROM indicators WHERE slug = #{slug}")
    Map<String, Object> findBySlug(@Param("slug") String slug);

    @Select("SELECT date, value FROM indicator_data " +
            "WHERE slug = #{slug} ORDER BY date DESC LIMIT #{limit}")
    List<Map<String, Object>> findSeriesBySlug(@Param("slug") String slug, @Param("limit") int limit);

    @Select({"SELECT i.slug, i.name, i.unit, i.region, i.category, i.tags, " +
            "id.date AS latest_date, id.value AS latest_value " +
            "FROM indicators i " +
            "LEFT JOIN LATERAL (SELECT date, value FROM indicator_data " +
            "  WHERE slug = i.slug ORDER BY date DESC LIMIT 1) id ON true " +
            "WHERE i.enabled = true " +
            "AND (#{keyword,jdbcType=VARCHAR} IS NULL OR #{keyword,jdbcType=VARCHAR} = '' OR " +
            "  i.name ILIKE '%' || #{keyword,jdbcType=VARCHAR} || '%' OR " +
            "  i.slug ILIKE '%' || #{keyword,jdbcType=VARCHAR} || '%') " +
            "AND (#{region,jdbcType=VARCHAR} IS NULL OR #{region,jdbcType=VARCHAR} = '' OR " +
            "  i.region = #{region,jdbcType=VARCHAR} OR " +
            "  i.region = (SELECT label FROM indicator_region WHERE code = #{region,jdbcType=VARCHAR})) " +
            "AND (#{category,jdbcType=VARCHAR} IS NULL OR #{category,jdbcType=VARCHAR} = '' OR " +
            "  i.category = #{category,jdbcType=VARCHAR} OR " +
            "  i.category = (SELECT label FROM indicator_category WHERE code = #{category,jdbcType=VARCHAR})) " +
            "ORDER BY i.name ASC " +
            "LIMIT #{limit} OFFSET #{offset}"})
    List<Map<String, Object>> searchWithValues(@Param("keyword") String keyword,
                                                @Param("region") String region,
                                                @Param("category") String category,
                                                @Param("limit") int limit,
                                                @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM indicators WHERE enabled = true " +
            "AND (#{keyword,jdbcType=VARCHAR} IS NULL OR #{keyword,jdbcType=VARCHAR} = '' OR " +
            "  name ILIKE '%' || #{keyword,jdbcType=VARCHAR} || '%' OR " +
            "  slug ILIKE '%' || #{keyword,jdbcType=VARCHAR} || '%') " +
            "AND (#{region,jdbcType=VARCHAR} IS NULL OR #{region,jdbcType=VARCHAR} = '' OR " +
            "  region = #{region,jdbcType=VARCHAR} OR " +
            "  region = (SELECT label FROM indicator_region WHERE code = #{region,jdbcType=VARCHAR})) " +
            "AND (#{category,jdbcType=VARCHAR} IS NULL OR #{category,jdbcType=VARCHAR} = '' OR " +
            "  category = #{category,jdbcType=VARCHAR} OR " +
            "  category = (SELECT label FROM indicator_category WHERE code = #{category,jdbcType=VARCHAR}))")
    int countSearch(@Param("keyword") String keyword,
                    @Param("region") String region,
                    @Param("category") String category);

    @Select("SELECT code, label FROM indicator_region ORDER BY sort_order ASC")
    List<Map<String, Object>> selectRegions();

    @Select("SELECT code, label FROM indicator_category ORDER BY sort_order ASC")
    List<Map<String, Object>> selectCategories();
}
