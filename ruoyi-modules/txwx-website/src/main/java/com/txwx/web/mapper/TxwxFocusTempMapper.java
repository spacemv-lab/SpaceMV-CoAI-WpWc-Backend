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

import com.txwx.web.domain.TxwxCarouselImageTemp;
import com.txwx.web.domain.TxwxFocusTemp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 关注配置临时表Mapper接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface TxwxFocusTempMapper
{
    /**
     * 查询关注配置临时表
     * 
     * @param focusId 关注配置临时表主键
     * @return 关注配置临时表
     */
    TxwxFocusTemp selectFocusTempByFocusId(@Param("focusId") Long focusId);

    /**
     * 查询关注配置临时表列表
     * 
     * @return 关注配置临时表集合
     */
    List<TxwxFocusTemp> selectFocusTempList();

    /**
     * 新增关注配置临时表
     * 
     * @param focusTemp 关注配置临时表
     * @return 结果
     */
    int insertFocusTemp(TxwxFocusTemp focusTemp);

    /**
     * 修改关注配置临时表
     * 
     * @param focusTemp 关注配置临时表
     * @return 结果
     */
    int updateFocusTemp(TxwxFocusTemp focusTemp);

    /**
     * 删除关注配置临时表
     * 
     * @param focusId 关注配置临时表主键
     * @return 结果
     */
    int deleteFocusTempByFocusId(@Param("focusId") Long focusId);

    /**
     * 批量删除关注配置临时表
     * 
     * @param focusIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteFocusTempByFocusIds(Long[] focusIds);

    /**
     * 清空关注配置临时表
     * 
     * @return 结果
     */
    int clearFocusTemp();

    /**
     * 获取关注临时表的最大排序号
     * 
     * @return 最大排序号
     */
    Integer getMaxSortOrder();

    /**
     * 将临时表数据复制到正式表
     * 
     * @return 结果
     */
    int copyTempToFormal();

    /**
     * 批量保存关注配置
     *
     * @param txwxFocusTempList 关注配置临时表列表
     * @return 结果
     */
    int batchInsertFocusTemp(@Param("txwxFocusTempList") List<TxwxFocusTemp> txwxFocusTempList);
}