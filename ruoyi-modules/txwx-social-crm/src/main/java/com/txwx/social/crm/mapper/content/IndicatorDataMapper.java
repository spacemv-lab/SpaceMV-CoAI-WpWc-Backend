/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.mapper.content;

import com.ruoyi.common.datasource.annotation.Postgres;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Postgres
public interface IndicatorDataMapper {

    @Insert("INSERT INTO indicator_data (slug, date, value, raw_value, metadata, updated_at) " +
            "VALUES (#{slug}, #{date}, #{value}, #{rawValue}, #{metadata}::jsonb, now()) " +
            "ON CONFLICT (slug, date) DO UPDATE SET " +
            "value = EXCLUDED.value, " +
            "raw_value = COALESCE(EXCLUDED.raw_value, indicator_data.raw_value), " +
            "metadata = COALESCE(EXCLUDED.metadata, indicator_data.metadata), " +
            "updated_at = now()")
    int upsert(Map<String, Object> point);

    @Insert("<script>" +
            "INSERT INTO indicator_data (slug, date, value, raw_value, metadata, updated_at) VALUES " +
            "<foreach collection='list' item='p' separator=','>" +
            "(#{p.slug}, #{p.date}, #{p.value}, #{p.rawValue}, #{p.metadata}::jsonb, now())" +
            "</foreach> " +
            "ON CONFLICT (slug, date) DO UPDATE SET " +
            "value = EXCLUDED.value, " +
            "raw_value = COALESCE(EXCLUDED.raw_value, indicator_data.raw_value), " +
            "metadata = COALESCE(EXCLUDED.metadata, indicator_data.metadata), " +
            "updated_at = now()" +
            "</script>")
    int batchUpsert(@Param("list") List<Map<String, Object>> points);

    @Update({"<script>",
            "UPDATE indicator_data SET source_type = 'manual' WHERE slug = #{slug} AND date IN",
            "<foreach collection='dates' item='d' open='(' separator=',' close=')'>#{d}::date</foreach>",
            "</script>"})
    int markManual(@Param("slug") String slug, @Param("dates") List<String> dates);
}
