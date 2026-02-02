/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the MIT License;
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 
 * the copywrite is belongs to  SpaceMV  team
 */
package com.txwx.web.mapper;

import com.txwx.web.domain.TxwxPageButton;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 页面按钮配置Mapper接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface TxwxPageButtonMapper
{
    /**
     * 查询页面按钮配置
     * 
     * @param buttonId 页面按钮配置主键
     * @return 页面按钮配置
     */
    TxwxPageButton selectPageButtonByButtonId(@Param("buttonId") Long buttonId);

    /**
     * 查询页面按钮配置列表
     * 
     * @param pageButton 页面按钮配置
     * @return 页面按钮配置集合
     */
    List<TxwxPageButton> selectPageButtonList(TxwxPageButton pageButton);

    /**
     * 根据页面代码查询按钮配置列表
     * 
     * @param pageCode 页面代码
     * @return 页面按钮配置集合
     */
    List<TxwxPageButton> selectPageButtonListByPageCode(@Param("pageCode") String pageCode);

    /**
     * 根据页面代码查询展示状态的按钮配置列表
     * 
     * @param pageCode 页面代码
     * @param isShow 是否展示
     * @return 页面按钮配置集合
     */
    List<TxwxPageButton> selectShowPageButtonListByPageCode(@Param("pageCode") String pageCode, @Param("isShow") String isShow);

    /**
     * 新增页面按钮配置
     * 
     * @param pageButton 页面按钮配置
     * @return 结果
     */
    int insertPageButton(TxwxPageButton pageButton);

    /**
     * 修改页面按钮配置
     * 
     * @param pageButton 页面按钮配置
     * @return 结果
     */
    int updatePageButton(TxwxPageButton pageButton);

    /**
     * 修改页面发布人和发布时间
     *
     * @param pageButton 页面按钮配置
     * @return 结果
     */
    int updatePageButtonByAndTime(TxwxPageButton pageButton);

    /**
     * 删除页面按钮配置
     * 
     * @param buttonId 页面按钮配置主键
     * @return 结果
     */
    int deletePageButtonByButtonId(@Param("buttonId") Long buttonId);

    /**
     * 批量删除页面按钮配置
     * 
     * @param buttonIds 需要删除的数据主键集合
     * @return 结果
     */
    int deletePageButtonByButtonIds(Long[] buttonIds);

    /**
     * 根据页面代码删除所有按钮配置
     * 
     * @param pageCode 页面代码
     * @return 结果
     */
    int deletePageButtonByPageCode(@Param("pageCode") String pageCode);

    /**
     * 获取页面按钮的最大排序号
     * 
     * @param pageCode 页面代码
     * @return 最大排序号
     */
    Integer getMaxSortOrderByPageCode(@Param("pageCode") String pageCode);

    /**
     * 更新按钮排序
     * 
     * @param buttonId 按钮ID
     * @param sortOrder 排序号
     * @return 结果
     */
    int updateButtonSort(@Param("buttonId") Long buttonId, @Param("sortOrder") Integer sortOrder);

    /**
     * 清空页面按钮配置正式表
     * 
     * @return 结果
     */
    int clearPageButton();
}