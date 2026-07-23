package com.txwx.social.crm.mapper.content;

import com.ruoyi.common.datasource.annotation.Postgres;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Postgres
public interface IndicatorsMapper {

    @Select("SELECT ds.slug, ds.name, ds.unit, ds.region, ds.category, ds.tags, ds.type " +
            "FROM data_sources ds WHERE ds.enabled = true " +
            "ORDER BY ds.name ASC")
    List<Map<String, Object>> listAll();

    @Select({"SELECT ds.slug, ds.name, ds.unit, ds.region, ds.category, ds.tags, ds.type, " +
            "id.date AS latest_date, id.value AS latest_value " +
            "FROM data_sources ds " +
            "LEFT JOIN LATERAL (SELECT date, value FROM indicator_data " +
            "  WHERE slug = ds.slug ORDER BY date DESC LIMIT 1) id ON true " +
            "WHERE ds.enabled = true " +
            "ORDER BY ds.name ASC"})
    List<Map<String, Object>> listWithValues();

    @Select("SELECT ds.slug, ds.name, ds.unit, ds.region, ds.category, ds.tags, ds.type " +
            "FROM data_sources ds WHERE ds.slug = #{slug}")
    Map<String, Object> findBySlug(@Param("slug") String slug);

    @Select("SELECT date, value FROM indicator_data " +
            "WHERE slug = #{slug} ORDER BY date DESC LIMIT #{limit}")
    List<Map<String, Object>> findSeriesBySlug(@Param("slug") String slug, @Param("limit") int limit);

    @Select({"SELECT ds.slug, ds.name, ds.unit, ds.region, ds.category, ds.tags, ds.type, " +
            "id.date AS latest_date, id.value AS latest_value " +
            "FROM data_sources ds " +
            "LEFT JOIN LATERAL (SELECT date, value FROM indicator_data " +
            "  WHERE slug = ds.slug ORDER BY date DESC LIMIT 1) id ON true " +
            "WHERE ds.enabled = true " +
            "AND (#{keyword,jdbcType=VARCHAR} IS NULL OR #{keyword,jdbcType=VARCHAR} = '' OR " +
            "  ds.name ILIKE '%' || #{keyword,jdbcType=VARCHAR} || '%' OR " +
            "  ds.slug ILIKE '%' || #{keyword,jdbcType=VARCHAR} || '%' OR " +
            "  EXISTS (SELECT 1 FROM unnest(ds.tags) AS tag WHERE tag ILIKE '%' || #{keyword,jdbcType=VARCHAR} || '%')) " +
            "AND (#{type,jdbcType=VARCHAR} IS NULL OR #{type,jdbcType=VARCHAR} = '' OR " +
            "  ds.type = #{type,jdbcType=VARCHAR}) " +
            "AND (#{region,jdbcType=VARCHAR} IS NULL OR #{region,jdbcType=VARCHAR} = '' OR " +
            "  ds.region = #{region,jdbcType=VARCHAR}) " +
            "AND (#{category,jdbcType=VARCHAR} IS NULL OR #{category,jdbcType=VARCHAR} = '' OR " +
            "  ds.category = #{category,jdbcType=VARCHAR}) " +
            "ORDER BY ds.name ASC " +
            "LIMIT #{limit} OFFSET #{offset}"})
    List<Map<String, Object>> searchWithValues(@Param("keyword") String keyword,
                                                @Param("type") String type,
                                                @Param("region") String region,
                                                @Param("category") String category,
                                                @Param("limit") int limit,
                                                @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM data_sources ds WHERE ds.enabled = true " +
            "AND (#{keyword,jdbcType=VARCHAR} IS NULL OR #{keyword,jdbcType=VARCHAR} = '' OR " +
            "  ds.name ILIKE '%' || #{keyword,jdbcType=VARCHAR} || '%' OR " +
            "  ds.slug ILIKE '%' || #{keyword,jdbcType=VARCHAR} || '%' OR " +
            "  EXISTS (SELECT 1 FROM unnest(ds.tags) AS tag WHERE tag ILIKE '%' || #{keyword,jdbcType=VARCHAR} || '%')) " +
            "AND (#{type,jdbcType=VARCHAR} IS NULL OR #{type,jdbcType=VARCHAR} = '' OR " +
            "  ds.type = #{type,jdbcType=VARCHAR}) " +
            "AND (#{region,jdbcType=VARCHAR} IS NULL OR #{region,jdbcType=VARCHAR} = '' OR " +
            "  ds.region = #{region,jdbcType=VARCHAR}) " +
            "AND (#{category,jdbcType=VARCHAR} IS NULL OR #{category,jdbcType=VARCHAR} = '' OR " +
            "  ds.category = #{category,jdbcType=VARCHAR})")
    int countSearch(@Param("keyword") String keyword,
                    @Param("type") String type,
                    @Param("region") String region,
                    @Param("category") String category);

    @Select("SELECT code, label FROM indicator_region ORDER BY sort_order ASC")
    List<Map<String, Object>> selectRegions();

    @Select("SELECT code, label FROM indicator_category ORDER BY sort_order ASC")
    List<Map<String, Object>> selectCategories();
}
