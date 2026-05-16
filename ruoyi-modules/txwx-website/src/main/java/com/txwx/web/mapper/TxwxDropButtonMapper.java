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
 * 下拉按钮配置Mapper接口
 *
 * @author txwx
 * @date 2026-02-09
 */
public interface TxwxDropButtonMapper
{
    /**
     * 查询下拉按钮配置
     *
     * @param dropButtonId 下拉按钮配置主键
     * @return 下拉按钮配置
     */
    DropButton selectDropButtonByDropButtonId(@Param("dropButtonId") Long dropButtonId);

    /**
     * 根据按钮ID查询下拉按钮配置列表
     *
     * @param buttonId 按钮ID
     * @return 下拉按钮配置集合
     */
    List<DropButton> selectDropButtonListByButtonId(@Param("buttonId") Long buttonId);

    /**
     * 查询下拉按钮配置列表
     *
     * @param dropButton 下拉按钮配置
     * @return 下拉按钮配置集合
     */
    List<DropButton> selectDropButtonList(DropButton dropButton);

    /**
     * 新增下拉按钮配置
     *
     * @param dropButton 下拉按钮配置
     * @return 结果
     */
    int insertDropButton(DropButton dropButton);

    /**
     * 修改下拉按钮配置
     *
     * @param dropButton 下拉按钮配置
     * @return 结果
     */
    int updateDropButton(DropButton dropButton);

    /**
     * 删除下拉按钮配置
     *
     * @param dropButtonId 下拉按钮配置主键
     * @return 结果
     */
    int deleteDropButtonByDropButtonId(@Param("dropButtonId") Long dropButtonId);

    /**
     * 批量删除下拉按钮配置
     *
     * @param dropButtonIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteDropButtonByDropButtonIds(@Param("dropButtonIds") Long[] dropButtonIds);

    /**
     * 根据按钮ID删除所有下拉按钮配置
     *
     * @param buttonId 按钮ID
     * @return 结果
     */
    int deleteDropButtonByButtonId(@Param("buttonId") Long buttonId);

    /**
     * 清空下拉按钮配置正式表
     *
     * @return 结果
     */
    int clearDropButton();
}
