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

import com.txwx.web.domain.TxwxProductCert;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 产品认证正式表Mapper接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface TxwxProductCertMapper
{
    /**
     * 查询产品认证正式表
     * 
     * @param certId 产品认证正式表主键
     * @return 产品认证正式表
     */
    TxwxProductCert selectProductCertByCertId(@Param("certId") Long certId);

    /**
     * 查询产品认证正式表列表
     * 
     * @return 产品认证正式表集合
     */
    List<TxwxProductCert> selectProductCertList();

    /**
     * 新增产品认证正式表
     * 
     * @param productCert 产品认证正式表
     * @return 结果
     */
    int insertProductCert(TxwxProductCert productCert);

    /**
     * 修改产品认证正式表
     * 
     * @param productCert 产品认证正式表
     * @return 结果
     */
    int updateProductCert(TxwxProductCert productCert);

    /**
     * 删除产品认证正式表
     * 
     * @param certId 产品认证正式表主键
     * @return 结果
     */
    int deleteProductCertByCertId(@Param("certId") Long certId);

    /**
     * 批量删除产品认证正式表
     * 
     * @param certIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteProductCertByCertIds(Long[] certIds);

    /**
     * 清空产品认证正式表
     * 
     * @return 结果
     */
    int clearProductCert();

    /**
     * 获取产品认证的最大排序号
     * 
     * @return 最大排序号
     */
    Integer getMaxSortOrder();

    /**
     * 更新发布人和发布时间
     *
     * @param publishedBy 发布人
     * @param publishedTime 发布时间
     */
    int updateByAndTime(@Param("publishedBy") String publishedBy, @Param("publishedTime") Date publishedTime);
}