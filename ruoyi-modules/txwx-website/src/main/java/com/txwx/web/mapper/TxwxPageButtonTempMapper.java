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

import com.txwx.web.domain.TxwxPageButtonTemp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 页面按钮配置临时表Mapper接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface TxwxPageButtonTempMapper
{
    /**
     * 查询页面按钮配置临时表
     * 
     * @param buttonId 页面按钮配置临时表主键
     * @return 页面按钮配置临时表
     */
    TxwxPageButtonTemp selectPageButtonTempByButtonId(@Param("buttonId") Long buttonId);

    /**
     * 查询页面按钮配置临时表列表
     * 
     * @param pageButtonTemp 页面按钮配置临时表
     * @return 页面按钮配置临时表集合
     */
    List<TxwxPageButtonTemp> selectPageButtonTempList(TxwxPageButtonTemp pageButtonTemp);

    /**
     * 根据页面代码查询按钮配置临时表列表
     * 
     * @param pageCode 页面代码
     * @return 页面按钮配置临时表集合
     */
    List<TxwxPageButtonTemp> selectPageButtonTempListByPageCode(@Param("pageCode") String pageCode);

    /**
     * 根据页面代码和展示状态查询按钮配置临时表列表
     * 
     * @param pageCode 页面代码
     * @param isShow 是否展示
     * @return 页面按钮配置临时表集合
     */
    List<TxwxPageButtonTemp> selectShowPageButtonTempListByPageCode(@Param("pageCode") String pageCode, @Param("isShow") String isShow);


    /**
     * 根据页面代码和是否已发布状态查询按钮配置临时表列表
     *
     * @param pageCode 页面代码
     * @param isPublish 是否已发布
     * @return 页面按钮配置临时表集合
     */
    List<TxwxPageButtonTemp> selectIsPublishPageButtonTempListByPageCode(@Param("pageCode") String pageCode, @Param("isPublish") String isPublish);

    /**
     * 根据页面代码获取最大排序号
     * 
     * @param pageCode 页面代码
     * @return 最大排序号
     */
    Integer getMaxSortOrderByPageCode(@Param("pageCode") String pageCode);

    /**
     * 新增页面按钮配置临时表
     * 
     * @param pageButtonTemp 页面按钮配置临时表
     * @return 结果
     */
    int insertPageButtonTemp(TxwxPageButtonTemp pageButtonTemp);

    /**
     * 修改页面按钮配置临时表
     * 
     * @param pageButtonTemp 页面按钮配置临时表
     * @return 结果
     */
    int updatePageButtonTemp(TxwxPageButtonTemp pageButtonTemp);


    /**
     * @description: 修改所有的按钮配置为已发布，用于发布事件
     */
    int updateAllPublish();

    /**
     * 更新按钮排序
     * 
     * @param buttonId 按钮ID
     * @param sortOrder 排序号
     * @return 结果
     */
    int updateButtonSort(@Param("buttonId") Long buttonId, @Param("sortOrder") Integer sortOrder);

    /**
     * 删除页面按钮配置临时表
     * 
     * @param buttonId 页面按钮配置临时表主键
     * @return 结果
     */
    int deletePageButtonTempByButtonId(@Param("buttonId") Long buttonId);

    /**
     * 批量删除页面按钮配置临时表
     * 
     * @param buttonIds 需要删除的数据主键集合
     * @return 结果
     */
    int deletePageButtonTempByButtonIds(@Param("buttonIds") Long[] buttonIds);

    /**
     * 根据页面代码删除所有按钮配置临时表
     * 
     * @param pageCode 页面代码
     * @return 结果
     */
    int deletePageButtonTempByPageCode(@Param("pageCode") String pageCode);

    /**
     * 清空页面按钮配置临时表
     * 
     * @return 结果
     */
    int clearPageButtonTemp();

    /**
     * 将临时表数据复制到正式表
     * 
     * @return 结果
     */
    int copyTempToFormal();

    /**
     * 根据页面代码将临时表数据复制到正式表
     * 
     * @param pageCode 页面代码
     * @return 结果
     */
    int copyTempToFormalByPageCode(@Param("pageCode") String pageCode);

    /**
     * 批量保存按钮配置（保存页面所有按钮）
     * 
     * @param pageButtonTempList 按钮配置临时表列表
     * @return 结果
     */
    int batchInsertPageButtonTemp(@Param("pageButtonTempList") List<TxwxPageButtonTemp> pageButtonTempList);

    /**
     * 根据页面代码清空按钮配置临时表
     * 
     * @param pageCode 页面代码
     * @return 结果
     */
    int clearPageButtonTempByPageCode(@Param("pageCode") String pageCode);
}