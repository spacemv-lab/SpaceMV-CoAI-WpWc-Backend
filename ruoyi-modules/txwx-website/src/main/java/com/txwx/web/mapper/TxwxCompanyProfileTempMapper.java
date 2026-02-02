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

import com.txwx.web.domain.TxwxCompanyProfileTemp;
import org.apache.ibatis.annotations.Param;

/**
 * 公司简介临时表Mapper接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface TxwxCompanyProfileTempMapper
{
    /**
     * 查询公司简介临时表
     * 
     * @param id 公司简介临时表主键
     * @return 公司简介临时表
     */
    TxwxCompanyProfileTemp selectCompanyProfileTempById(@Param("id") Long id);

    /**
     * 查询公司简介临时表（获取唯一一条记录）
     * 
     * @return 公司简介临时表
     */
    TxwxCompanyProfileTemp selectCompanyProfileTemp();

    /**
     * 新增公司简介临时表
     * 
     * @param companyProfileTemp 公司简介临时表
     * @return 结果
     */
    int insertCompanyProfileTemp(TxwxCompanyProfileTemp companyProfileTemp);

    /**
     * 修改公司简介临时表
     * 
     * @param companyProfileTemp 公司简介临时表
     * @return 结果
     */
    int updateCompanyProfileTemp(TxwxCompanyProfileTemp companyProfileTemp);

    /**
     * 删除公司简介临时表
     * 
     * @param id 公司简介临时表主键
     * @return 结果
     */
    int deleteCompanyProfileTempById(@Param("id") Long id);

    /**
     * 清空公司简介临时表
     * 
     * @return 结果
     */
    int clearCompanyProfileTemp();

    /**
     * 将临时表数据复制到正式表
     * 
     * @return 结果
     */
    int copyTempToFormal();
}