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