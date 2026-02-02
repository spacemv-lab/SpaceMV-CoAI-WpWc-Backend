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

import com.txwx.web.domain.TxwxFocusTemp;
import com.txwx.web.domain.TxwxProductCertTemp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 产品认证临时表Mapper接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface TxwxProductCertTempMapper
{
    /**
     * 查询产品认证临时表
     * 
     * @param certId 产品认证临时表主键
     * @return 产品认证临时表
     */
    TxwxProductCertTemp selectProductCertTempByCertId(@Param("certId") Long certId);

    /**
     * 查询产品认证临时表列表
     * 
     * @return 产品认证临时表集合
     */
    List<TxwxProductCertTemp> selectProductCertTempList();

    /**
     * 新增产品认证临时表
     * 
     * @param productCertTemp 产品认证临时表
     * @return 结果
     */
    int insertProductCertTemp(TxwxProductCertTemp productCertTemp);

    /**
     * 修改产品认证临时表
     * 
     * @param productCertTemp 产品认证临时表
     * @return 结果
     */
    int updateProductCertTemp(TxwxProductCertTemp productCertTemp);

    /**
     * 删除产品认证临时表
     * 
     * @param certId 产品认证临时表主键
     * @return 结果
     */
    int deleteProductCertTempByCertId(@Param("certId") Long certId);

    /**
     * 批量删除产品认证临时表
     * 
     * @param certIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteProductCertTempByCertIds(Long[] certIds);

    /**
     * 清空产品认证临时表
     * 
     * @return 结果
     */
    int clearProductCertTemp();

    /**
     * 获取产品认证临时表的最大排序号
     * 
     * @return 最大排序号
     */
    Integer getMaxSortOrder();

    /**
     * 将临时表数据复制到正式表
     * 
     * @return 结果
     */
    int copyTempToFormal();

    /**
     * 批量保存产品认证配置
     *
     * @param txwxProductCertTempList 产品认证配置临时表列表
     * @return 结果
     */
    int batchInsertProductCertTemp(@Param("txwxProductCertTempList") List<TxwxProductCertTemp> txwxProductCertTempList);
}