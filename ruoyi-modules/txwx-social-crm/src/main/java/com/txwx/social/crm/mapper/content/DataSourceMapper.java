/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.mapper.content;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.common.datasource.annotation.Postgres;
import com.txwx.social.crm.domain.po.DataSourcePO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Postgres
public interface DataSourceMapper extends BaseMapper<DataSourcePO> {

    @Select("SELECT COUNT(*) FROM data_sources WHERE slug = #{slug}")
    int countBySlug(@Param("slug") String slug);

    @Select({
        "<script>",
        "SELECT ds.*,",
        "  sl.status AS latest_sync_status,",
        "  sl.started_at AS latest_sync_time,",
        "  sl.data_points AS latest_data_points",
        "FROM data_sources ds",
        "LEFT JOIN (",
        "  SELECT source_id, status, started_at, data_points,",
        "    ROW_NUMBER() OVER (PARTITION BY source_id ORDER BY started_at DESC) AS rn",
        "  FROM sync_logs",
        ") sl ON sl.source_id = ds.id AND sl.rn = 1",
        "WHERE 1=1",
        "  <if test='param.id != null and param.id != \"\"'>AND ds.id = #{param.id}</if>",
        "  <if test='param.name != null and param.name != \"\"'>AND ds.name ILIKE '%' || #{param.name} || '%'</if>",
        "  <if test='param.type != null and param.type != \"\"'>AND ds.type = #{param.type}</if>",
        "  <if test='param.types != null and param.types.size() > 0'>",
        "    AND ds.type IN",
        "    <foreach item='t' collection='param.types' open='(' separator=',' close=')'>#{t}</foreach>",
        "  </if>",
        "  <if test='param.region != null and param.region != \"\"'>AND ds.region = #{param.region}</if>",
        "  <if test='param.category != null and param.category != \"\"'>AND ds.category = #{param.category}</if>",
        "  <if test='param.status != null and param.status != \"\"'>",
        "    AND (CASE WHEN ds.last_sync_status IS NULL OR ds.last_sync_status = '' THEN 'never' ELSE ds.last_sync_status END) = #{param.status}</if>",
        "  <if test='param.enabled != null'>AND ds.enabled = #{param.enabled}</if>",
        "ORDER BY ds.updated_at DESC, ds.created_at DESC",
        "</script>"
    })
    List<Map<String, Object>> selectListWithStatus(@Param("param") Map<String, Object> param);
}
