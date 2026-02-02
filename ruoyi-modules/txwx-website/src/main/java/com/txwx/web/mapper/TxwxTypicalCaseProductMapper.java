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

import com.txwx.web.domain.TxwxTypicalCaseProduct;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 典型案例/典型产品正式表Mapper接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface TxwxTypicalCaseProductMapper
{
    /**
     * 查询典型案例/典型产品正式表
     * 
     * @param caseProductId 典型案例/典型产品正式表主键
     * @return 典型案例/典型产品正式表
     */
    TxwxTypicalCaseProduct selectTypicalCaseProductByCaseProductId(@Param("caseProductId") Long caseProductId);

    /**
     * 查询典型案例/典型产品正式表列表
     * 
     * @param typicalCaseProduct 典型案例/典型产品正式表
     * @return 典型案例/典型产品正式表集合
     */
    List<TxwxTypicalCaseProduct> selectTypicalCaseProductList(TxwxTypicalCaseProduct typicalCaseProduct);

    /**
     * 根据产品类型查询典型案例/典型产品列表
     * 
     * @param productType 产品类型
     * @return 典型案例/典型产品正式表集合
     */
    List<TxwxTypicalCaseProduct> selectTypicalCaseProductListByProductType(@Param("productType") String productType);

    /**
     * 新增典型案例/典型产品正式表
     * 
     * @param typicalCaseProduct 典型案例/典型产品正式表
     * @return 结果
     */
    int insertTypicalCaseProduct(TxwxTypicalCaseProduct typicalCaseProduct);

    /**
     * 修改典型案例/典型产品正式表
     * 
     * @param typicalCaseProduct 典型案例/典型产品正式表
     * @return 结果
     */
    int updateTypicalCaseProduct(TxwxTypicalCaseProduct typicalCaseProduct);

    /**
     * 删除典型案例/典型产品正式表
     * 
     * @param caseProductId 典型案例/典型产品正式表主键
     * @return 结果
     */
    int deleteTypicalCaseProductByCaseProductId(@Param("caseProductId") Long caseProductId);

    /**
     * 批量删除典型案例/典型产品正式表
     * 
     * @param caseProductIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteTypicalCaseProductByCaseProductIds(@Param("caseProductIds") Long[] caseProductIds);

    /**
     * 根据产品类型删除典型案例/典型产品正式表
     * 
     * @param productType 产品类型
     * @return 结果
     */
    int deleteTypicalCaseProductByProductType(@Param("productType") String productType);

    /**
     * 清空典型案例/典型产品正式表
     * 
     * @return 结果
     */
    int clearTypicalCaseProduct();

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

    /**
     * 更新发布人和发布时间
     *
     * @param publishedBy 发布人
     * @param publishedTime 发布时间
     */
    int updateByAndTime(@Param("publishedBy") String publishedBy, @Param("publishedTime") Date publishedTime);
}