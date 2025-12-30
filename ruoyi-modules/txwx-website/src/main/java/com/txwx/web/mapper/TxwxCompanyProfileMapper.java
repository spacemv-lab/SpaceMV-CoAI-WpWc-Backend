package com.txwx.web.mapper;

import com.txwx.web.domain.TxwxCompanyProfile;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * 公司简介正式表Mapper接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface TxwxCompanyProfileMapper
{
    /**
     * 查询公司简介正式表
     * 
     * @param id 公司简介正式表主键
     * @return 公司简介正式表
     */
    TxwxCompanyProfile selectCompanyProfileById(@Param("id") Long id);

    /**
     * 查询公司简介正式表（获取唯一一条记录）
     * 
     * @return 公司简介正式表
     */
    TxwxCompanyProfile selectCompanyProfile();

    /**
     * 新增公司简介正式表
     * 
     * @param companyProfile 公司简介正式表
     * @return 结果
     */
    int insertCompanyProfile(TxwxCompanyProfile companyProfile);

    /**
     * 修改公司简介正式表
     * 
     * @param companyProfile 公司简介正式表
     * @return 结果
     */
    int updateCompanyProfile(TxwxCompanyProfile companyProfile);

    /**
     * 删除公司简介正式表
     * 
     * @param id 公司简介正式表主键
     * @return 结果
     */
    int deleteCompanyProfileById(@Param("id") Long id);

    /**
     * 清空公司简介正式表
     * 
     * @return 结果
     */
    int clearCompanyProfile();

    /**
     * 更新发布人和发布时间
     *
     * @param publishedBy 发布人
     * @param publishedTime 发布时间
     */
    int updateByAndTime(@Param("publishedBy") String publishedBy, @Param("publishedTime") Date publishedTime);
}