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

import com.txwx.web.domain.TxwxTypicalCaseProductTemp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 典型案例/典型产品临时表Mapper接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface TxwxTypicalCaseProductTempMapper
{
    /**
     * 查询典型案例/典型产品临时表
     * 
     * @param caseProductId 典型案例/典型产品临时表主键
     * @return 典型案例/典型产品临时表
     */
    TxwxTypicalCaseProductTemp selectTypicalCaseProductTempByCaseProductId(@Param("caseProductId") Long caseProductId);

    /**
     * 查询典型案例/典型产品临时表列表
     * 
     * @param typicalCaseProductTemp 典型案例/典型产品临时表
     * @return 典型案例/典型产品临时表集合
     */
    List<TxwxTypicalCaseProductTemp> selectTypicalCaseProductTempList(TxwxTypicalCaseProductTemp typicalCaseProductTemp);

    /**
     * 根据产品类型查询典型案例/典型产品临时表列表
     * 
     * @param productType 产品类型
     * @return 典型案例/典型产品临时表集合
     */
    List<TxwxTypicalCaseProductTemp> selectTypicalCaseProductTempListByProductType(@Param("productType") String productType);

    /**
     * 新增典型案例/典型产品临时表
     * 
     * @param typicalCaseProductTemp 典型案例/典型产品临时表
     * @return 结果
     */
    int insertTypicalCaseProductTemp(TxwxTypicalCaseProductTemp typicalCaseProductTemp);

    /**
     * 批量新增典型案例/典型产品临时表
     * 
     * @param typicalCaseProductTempList 典型案例/典型产品临时表列表
     * @return 结果
     */
    int batchInsertTypicalCaseProductTemp(@Param("typicalCaseProductTempList") List<TxwxTypicalCaseProductTemp> typicalCaseProductTempList);

    /**
     * 修改典型案例/典型产品临时表
     * 
     * @param typicalCaseProductTemp 典型案例/典型产品临时表
     * @return 结果
     */
    int updateTypicalCaseProductTemp(TxwxTypicalCaseProductTemp typicalCaseProductTemp);

    /**
     * 删除典型案例/典型产品临时表
     * 
     * @param caseProductId 典型案例/典型产品临时表主键
     * @return 结果
     */
    int deleteTypicalCaseProductTempByCaseProductId(@Param("caseProductId") Long caseProductId);

    /**
     * 批量删除典型案例/典型产品临时表
     * 
     * @param caseProductIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteTypicalCaseProductTempByCaseProductIds(@Param("caseProductIds") Long[] caseProductIds);

    /**
     * 根据产品类型删除典型案例/典型产品临时表
     * 
     * @param productType 产品类型
     * @return 结果
     */
    int deleteTypicalCaseProductTempByProductType(@Param("productType") String productType);

    /**
     * 清空典型案例/典型产品临时表
     * 
     * @return 结果
     */
    int clearTypicalCaseProductTemp();

    /**
     * 根据产品类型清空典型案例/典型产品临时表
     * 
     * @param productType 产品类型
     * @return 结果
     */
    int clearTypicalCaseProductTempByProductType(@Param("productType") String productType);

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
     * 更新案例/产品排序
     * 
     * @param caseProductId 案例/产品ID
     * @param sortOrder 排序号
     * @return 结果
     */
    int updateCaseProductSort(@Param("caseProductId") Long caseProductId, @Param("sortOrder") Integer sortOrder);

    /**
     * 获取案例/产品的最大排序号
     * 
     * @param productType 产品类型
     * @return 最大排序号
     */
    Integer getMaxSortOrderByProductType(@Param("productType") String productType);
}