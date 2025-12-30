package com.txwx.web.mapper;

import com.txwx.web.domain.TxwxProductBasicInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * 产品基础信息正式表Mapper接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface TxwxProductBasicInfoMapper
{
    /**
     * 查询产品基础信息正式表
     * 
     * @param id 产品基础信息正式表主键
     * @return 产品基础信息正式表
     */
    TxwxProductBasicInfo selectProductBasicInfoById(@Param("id") Long id);

    /**
     * 根据产品类型查询产品基础信息
     * 
     * @param productType 产品类型
     * @return 产品基础信息正式表
     */
    TxwxProductBasicInfo selectProductBasicInfoByProductType(@Param("productType") String productType);

    /**
     * 新增产品基础信息正式表
     * 
     * @param productBasicInfo 产品基础信息正式表
     * @return 结果
     */
    int insertProductBasicInfo(TxwxProductBasicInfo productBasicInfo);

    /**
     * 修改产品基础信息正式表
     * 
     * @param productBasicInfo 产品基础信息正式表
     * @return 结果
     */
    int updateProductBasicInfo(TxwxProductBasicInfo productBasicInfo);

    /**
     * 删除产品基础信息正式表
     * 
     * @param id 产品基础信息正式表主键
     * @return 结果
     */
    int deleteProductBasicInfoById(@Param("id") Long id);

    /**
     * 根据产品类型删除产品基础信息正式表
     * 
     * @param productType 产品类型
     * @return 结果
     */
    int deleteProductBasicInfoByProductType(@Param("productType") String productType);

    /**
     * 清空产品基础信息正式表
     * 
     * @return 结果
     */
    int clearProductBasicInfo();

    /**
     * 更新发布人和发布时间
     *
     * @param publishedBy 发布人
     * @param publishedTime 发布时间
     */
    int updateByAndTime(@Param("publishedBy") String publishedBy, @Param("publishedTime") Date publishedTime);
}