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

import com.txwx.web.domain.TxwxProductBasicInfoTemp;
import org.apache.ibatis.annotations.Param;

/**
 * 产品基础信息临时表Mapper接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface TxwxProductBasicInfoTempMapper
{
    /**
     * 查询产品基础信息临时表
     * 
     * @param id 产品基础信息临时表主键
     * @return 产品基础信息临时表
     */
    TxwxProductBasicInfoTemp selectProductBasicInfoTempById(@Param("id") Long id);

    /**
     * 根据产品类型查询产品基础信息临时表
     * 
     * @param productType 产品类型
     * @return 产品基础信息临时表
     */
    TxwxProductBasicInfoTemp selectProductBasicInfoTempByProductType(@Param("productType") String productType);

    /**
     * 新增产品基础信息临时表
     * 
     * @param productBasicInfoTemp 产品基础信息临时表
     * @return 结果
     */
    int insertProductBasicInfoTemp(TxwxProductBasicInfoTemp productBasicInfoTemp);

    /**
     * 修改产品基础信息临时表
     * 
     * @param productBasicInfoTemp 产品基础信息临时表
     * @return 结果
     */
    int updateProductBasicInfoTemp(TxwxProductBasicInfoTemp productBasicInfoTemp);

    /**
     * 删除产品基础信息临时表
     * 
     * @param id 产品基础信息临时表主键
     * @return 结果
     */
    int deleteProductBasicInfoTempById(@Param("id") Long id);

    /**
     * 根据产品类型删除产品基础信息临时表
     * 
     * @param productType 产品类型
     * @return 结果
     */
    int deleteProductBasicInfoTempByProductType(@Param("productType") String productType);

    /**
     * 清空产品基础信息临时表
     * 
     * @return 结果
     */
    int clearProductBasicInfoTemp();

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
}