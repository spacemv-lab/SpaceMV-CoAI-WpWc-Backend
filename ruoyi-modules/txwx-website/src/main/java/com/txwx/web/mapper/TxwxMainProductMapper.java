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

import com.txwx.web.domain.TxwxMainProduct;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 首页主要产品配置Mapper接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface TxwxMainProductMapper
{
    /**
     * 查询首页主要产品配置
     * 
     * @param productId 首页主要产品配置主键
     * @return 首页主要产品配置
     */
    TxwxMainProduct selectMainProductByProductId(@Param("productId") Long productId);

    /**
     * 查询首页主要产品配置列表
     * 
     * @param mainProduct 首页主要产品配置
     * @return 首页主要产品配置集合
     */
    List<TxwxMainProduct> selectMainProductList(TxwxMainProduct mainProduct);

    /**
     * 查询显示状态的主要产品列表
     * 
     * @param isShow 是否显示
     * @return 首页主要产品配置集合
     */
    List<TxwxMainProduct> selectShowMainProductList(@Param("isShow") String isShow);

    /**
     * 新增首页主要产品配置
     * 
     * @param mainProduct 首页主要产品配置
     * @return 结果
     */
    int insertMainProduct(TxwxMainProduct mainProduct);

    /**
     * 修改首页主要产品配置
     * 
     * @param mainProduct 首页主要产品配置
     * @return 结果
     */
    int updateMainProduct(TxwxMainProduct mainProduct);

    /**
     * 删除首页主要产品配置
     * 
     * @param productId 首页主要产品配置主键
     * @return 结果
     */
    int deleteMainProductByProductId(@Param("productId") Long productId);

    /**
     * 批量删除首页主要产品配置
     * 
     * @param productIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteMainProductByProductIds(Long[] productIds);

    /**
     * 获取主要产品的最大排序号
     * 
     * @return 最大排序号
     */
    Integer getMaxSortOrder();

    /**
     * 更新产品排序
     * 
     * @param productId 产品ID
     * @param sortOrder 排序号
     * @return 结果
     */
    int updateProductSort(@Param("productId") Long productId, @Param("sortOrder") Integer sortOrder);

    /**
     * 清空首页主要产品配置正式表
     * 
     * @return 结果
     */
    int clearMainProduct();

    /**
     * 更新发布人和发布时间
     *
     * @param publishedBy 发布人
     * @param publishedTime 发布时间
     */
    int updateByAndTime(@Param("publishedBy") String publishedBy, @Param("publishedTime") Date publishedTime);
}