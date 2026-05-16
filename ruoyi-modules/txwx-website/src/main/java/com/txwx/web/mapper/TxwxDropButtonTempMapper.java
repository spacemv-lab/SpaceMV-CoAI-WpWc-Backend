/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.web.mapper;

import com.txwx.web.domain.DropButton;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 下拉按钮配置临时表Mapper接口
 *
 * @author txwx
 * @date 2026-02-09
 */
public interface TxwxDropButtonTempMapper
{
    /**
     * 查询下拉按钮配置临时表
     *
     * @param dropButtonId 下拉按钮配置临时表主键
     * @return 下拉按钮配置临时表
     */
    DropButton selectDropButtonTempByDropButtonId(@Param("dropButtonId") Long dropButtonId);

    /**
     * 根据按钮ID查询下拉按钮配置临时表列表
     *
     * @param buttonId 按钮ID
     * @return 下拉按钮配置临时表集合
     */
    List<DropButton> selectDropButtonTempListByButtonId(@Param("buttonId") Long buttonId);

    /**
     * 查询下拉按钮配置临时表列表
     *
     * @param dropButton 下拉按钮配置临时表
     * @return 下拉按钮配置临时表集合
     */
    List<DropButton> selectDropButtonTempList(DropButton dropButton);

    /**
     * 新增下拉按钮配置临时表
     *
     * @param dropButton 下拉按钮配置临时表
     * @return 结果
     */
    int insertDropButtonTemp(DropButton dropButton);

    /**
     * 修改下拉按钮配置临时表
     *
     * @param dropButton 下拉按钮配置临时表
     * @return 结果
     */
    int updateDropButtonTemp(DropButton dropButton);

    /**
     * 删除下拉按钮配置临时表
     *
     * @param dropButtonId 下拉按钮配置临时表主键
     * @return 结果
     */
    int deleteDropButtonTempByDropButtonId(@Param("dropButtonId") Long dropButtonId);

    /**
     * 批量删除下拉按钮配置临时表
     *
     * @param dropButtonIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteDropButtonTempByDropButtonIds(@Param("dropButtonIds") Long[] dropButtonIds);

    /**
     * 根据按钮ID删除所有下拉按钮配置临时表
     *
     * @param buttonId 按钮ID
     * @return 结果
     */
    int deleteDropButtonTempByButtonId(@Param("buttonId") Long buttonId);

    /**
     * 清空下拉按钮配置临时表
     *
     * @return 结果
     */
    int clearDropButtonTemp();

    /**
     * 批量插入下拉按钮配置
     *
     * @param dropButtonList 下拉按钮配置临时表列表
     * @return 结果
     */
    int batchInsertDropButtonTemp(@Param("dropButtonList") List<DropButton> dropButtonList);
}
