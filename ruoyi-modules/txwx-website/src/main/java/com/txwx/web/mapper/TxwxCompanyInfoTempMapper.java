/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.web.mapper;

import com.txwx.web.domain.TxwxCompanyInfoTemp;
import org.apache.ibatis.annotations.Param;

/**
 * 公司基本信息临时表Mapper接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface TxwxCompanyInfoTempMapper
{
    /**
     * 查询公司基本信息临时表
     * 
     * @param id 公司基本信息临时表主键
     * @return 公司基本信息临时表
     */
    TxwxCompanyInfoTemp selectCompanyInfoTempById(@Param("id") Long id);

    /**
     * 查询公司基本信息临时表（获取唯一一条记录）
     * 
     * @return 公司基本信息临时表
     */
    TxwxCompanyInfoTemp selectCompanyInfoTemp();

    /**
     * 新增公司基本信息临时表
     * 
     * @param companyInfoTemp 公司基本信息临时表
     * @return 结果
     */
    int insertCompanyInfoTemp(TxwxCompanyInfoTemp companyInfoTemp);

    /**
     * 修改公司基本信息临时表
     * 
     * @param companyInfoTemp 公司基本信息临时表
     * @return 结果
     */
    int updateCompanyInfoTemp(TxwxCompanyInfoTemp companyInfoTemp);

    /**
     * 删除公司基本信息临时表
     * 
     * @param id 公司基本信息临时表主键
     * @return 结果
     */
    int deleteCompanyInfoTempById(@Param("id") Long id);

    /**
     * 清空公司基本信息临时表
     * 
     * @return 结果
     */
    int clearCompanyInfoTemp();

    /**
     * 将临时表数据复制到正式表
     * 
     * @return 结果
     */
    int copyTempToFormal();
}