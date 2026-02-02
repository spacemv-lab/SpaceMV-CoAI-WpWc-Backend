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

import com.txwx.web.domain.TxwxFocus;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 关注配置正式表Mapper接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface TxwxFocusMapper
{
    /**
     * 查询关注配置正式表
     * 
     * @param focusId 关注配置正式表主键
     * @return 关注配置正式表
     */
    TxwxFocus selectFocusByFocusId(@Param("focusId") Long focusId);

    /**
     * 查询关注配置正式表列表
     * 
     * @return 关注配置正式表集合
     */
    List<TxwxFocus> selectFocusList();

    /**
     * 新增关注配置正式表
     * 
     * @param focus 关注配置正式表
     * @return 结果
     */
    int insertFocus(TxwxFocus focus);

    /**
     * 修改关注配置正式表
     * 
     * @param focus 关注配置正式表
     * @return 结果
     */
    int updateFocus(TxwxFocus focus);

    /**
     * 删除关注配置正式表
     * 
     * @param focusId 关注配置正式表主键
     * @return 结果
     */
    int deleteFocusByFocusId(@Param("focusId") Long focusId);

    /**
     * 批量删除关注配置正式表
     * 
     * @param focusIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteFocusByFocusIds(Long[] focusIds);

    /**
     * 清空关注配置正式表
     * 
     * @return 结果
     */
    int clearFocus();

    /**
     * 获取关注的最大排序号
     * 
     * @return 最大排序号
     */
    Integer getMaxSortOrder();

    /**
     * 更新发布人和发布时间
     *
     * @param publishedBy 发布人
     * @param publishedTime 发布时间
     */
    int updateByAndTime(@Param("publishedBy") String publishedBy, @Param("publishedTime") Date publishedTime);
}