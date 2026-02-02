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

import com.txwx.web.domain.TxwxApplicationScenarioTemp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 应用场景临时表Mapper接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface TxwxApplicationScenarioTempMapper
{
    /**
     * 查询应用场景临时表
     * 
     * @param scenarioId 应用场景临时表主键
     * @return 应用场景临时表
     */
    TxwxApplicationScenarioTemp selectApplicationScenarioTempByScenarioId(@Param("scenarioId") Long scenarioId);

    /**
     * 查询应用场景临时表列表
     * 
     * @param applicationScenarioTemp 应用场景临时表
     * @return 应用场景临时表集合
     */
    List<TxwxApplicationScenarioTemp> selectApplicationScenarioTempList(TxwxApplicationScenarioTemp applicationScenarioTemp);

    /**
     * 根据产品类型查询应用场景临时表列表
     * 
     * @param productType 产品类型
     * @return 应用场景临时表集合
     */
    List<TxwxApplicationScenarioTemp> selectApplicationScenarioTempListByProductType(@Param("productType") String productType);

    /**
     * 新增应用场景临时表
     * 
     * @param applicationScenarioTemp 应用场景临时表
     * @return 结果
     */
    int insertApplicationScenarioTemp(TxwxApplicationScenarioTemp applicationScenarioTemp);

    /**
     * 批量新增应用场景临时表
     * 
     * @param applicationScenarioTempList 应用场景临时表列表
     * @return 结果
     */
    int batchInsertApplicationScenarioTemp(@Param("applicationScenarioTempList") List<TxwxApplicationScenarioTemp> applicationScenarioTempList);

    /**
     * 修改应用场景临时表
     * 
     * @param applicationScenarioTemp 应用场景临时表
     * @return 结果
     */
    int updateApplicationScenarioTemp(TxwxApplicationScenarioTemp applicationScenarioTemp);

    /**
     * 删除应用场景临时表
     * 
     * @param scenarioId 应用场景临时表主键
     * @return 结果
     */
    int deleteApplicationScenarioTempByScenarioId(@Param("scenarioId") Long scenarioId);

    /**
     * 批量删除应用场景临时表
     * 
     * @param scenarioIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteApplicationScenarioTempByScenarioIds(@Param("scenarioIds") Long[] scenarioIds);

    /**
     * 根据产品类型删除应用场景临时表
     * 
     * @param productType 产品类型
     * @return 结果
     */
    int deleteApplicationScenarioTempByProductType(@Param("productType") String productType);

    /**
     * 清空应用场景临时表
     * 
     * @return 结果
     */
    int clearApplicationScenarioTemp();

    /**
     * 根据产品类型清空应用场景临时表
     * 
     * @param productType 产品类型
     * @return 结果
     */
    int clearApplicationScenarioTempByProductType(@Param("productType") String productType);

    /**
     * 将临时表数据复制到正式表
     * 
     * @return 结果
     */
    int copyTempToFormal();

    /**
     * 根据产品类型将临时表数据复制到正式表
     * 
     * @param productType 产品类型
     * @return 结果
     */
    int copyTempToFormalByProductType(@Param("productType") String productType);

    /**
     * 更新应用场景排序
     * 
     * @param scenarioId 场景ID
     * @param sortOrder 排序号
     * @return 结果
     */
    int updateScenarioSort(@Param("scenarioId") Long scenarioId, @Param("sortOrder") Integer sortOrder);

    /**
     * 获取应用场景的最大排序号
     * 
     * @param productType 产品类型
     * @return 最大排序号
     */
    Integer getMaxSortOrderByProductType(@Param("productType") String productType);
}