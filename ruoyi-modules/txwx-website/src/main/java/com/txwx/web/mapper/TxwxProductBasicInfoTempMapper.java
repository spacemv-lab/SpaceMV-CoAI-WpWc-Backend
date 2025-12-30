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