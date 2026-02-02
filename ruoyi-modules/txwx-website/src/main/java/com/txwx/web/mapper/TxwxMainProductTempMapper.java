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

import com.txwx.web.domain.TxwxMainProductTemp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 首页主打产品配置临时表Mapper接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface TxwxMainProductTempMapper
{
    /**
     * 查询首页主打产品配置临时表
     * 
     * @param productId 首页主打产品配置临时表主键
     * @return 首页主打产品配置临时表
     */
    TxwxMainProductTemp selectMainProductTempByProductId(@Param("productId") Long productId);

    /**
     * 查询首页主打产品配置临时表列表
     * 
     * @param mainProductTemp 首页主打产品配置临时表
     * @return 首页主打产品配置临时表集合
     */
    List<TxwxMainProductTemp> selectMainProductTempList(TxwxMainProductTemp mainProductTemp);

    /**
     * 查询显示状态的主打产品临时表列表
     * 
     * @param isShow 是否显示
     * @return 首页主打产品配置临时表集合
     */
    List<TxwxMainProductTemp> selectShowMainProductTempList(@Param("isShow") String isShow);

    /**
     * 新增首页主打产品配置临时表
     * 
     * @param mainProductTemp 首页主打产品配置临时表
     * @return 结果
     */
    int insertMainProductTemp(TxwxMainProductTemp mainProductTemp);

    /**
     * 修改首页主打产品配置临时表
     * 
     * @param mainProductTemp 首页主打产品配置临时表
     * @return 结果
     */
    int updateMainProductTemp(TxwxMainProductTemp mainProductTemp);

    /**
     * 更新产品排序
     * 
     * @param productId 产品ID
     * @param sortOrder 排序号
     * @return 结果
     */
    int updateProductSort(@Param("productId") Long productId, @Param("sortOrder") Integer sortOrder);

    /**
     * 删除首页主打产品配置临时表
     * 
     * @param productId 首页主打产品配置临时表主键
     * @return 结果
     */
    int deleteMainProductTempByProductId(@Param("productId") Long productId);

    /**
     * 批量删除首页主打产品配置临时表
     * 
     * @param productIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteMainProductTempByProductIds(@Param("productIds") Long[] productIds);

    /**
     * 清空首页主打产品配置临时表
     * 
     * @return 结果
     */
    int clearMainProductTemp();

    /**
     * 将临时表数据复制到正式表
     * 
     * @return 结果
     */
    int copyTempToFormal();

    /**
     * 批量保存主打产品配置
     * 
     * @param mainProductTempList 主打产品配置临时表列表
     * @return 结果
     */
    int batchInsertMainProductTemp(@Param("mainProductTempList") List<TxwxMainProductTemp> mainProductTempList);

    /**
     * 获取主打产品的最大排序号
     * 
     * @return 最大排序号
     */
    Integer getMaxSortOrder();
}