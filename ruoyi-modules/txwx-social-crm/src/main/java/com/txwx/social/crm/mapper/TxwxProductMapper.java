/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.mapper;

import com.txwx.social.crm.domain.po.TxwxProductPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 产品Mapper
 *
 * @author txwx
 * @date 2026-04-03
 */
@Mapper
public interface TxwxProductMapper {

    /**
     * 查询产品列表
     *
     * @param product 产品参数
     * @return 产品列表
     */
    List<TxwxProductPO> selectProductList(TxwxProductPO product);

    /**
     * 查询产品详情
     *
     * @param id 主键ID
     * @return 产品详情
     */
    TxwxProductPO selectProductById(@Param("id") Long id);

    /**
     * 新增产品
     *
     * @param product 产品信息
     * @return 结果
     */
    int insertProduct(TxwxProductPO product);

    /**
     * 新增产品
     *
     * @param product 产品信息
     * @return 结果
     */
    int insertProductReturnID(TxwxProductPO product);

    /**
     * 修改产品
     *
     * @param product 产品信息
     * @return 结果
     */
    int updateProduct(TxwxProductPO product);

    /**
     * 删除产品
     *
     * @param ids 主键ID列表
     * @return 结果
     */
    int deleteProductByIds(@Param("ids") List<Long> ids);
}
