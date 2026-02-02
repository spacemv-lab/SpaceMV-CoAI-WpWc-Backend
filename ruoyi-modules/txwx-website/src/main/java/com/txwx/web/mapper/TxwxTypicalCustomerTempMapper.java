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

import com.txwx.web.domain.TxwxTypicalCustomerTemp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 首页典型客户配置临时表Mapper接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface TxwxTypicalCustomerTempMapper
{
    /**
     * 查询首页典型客户配置临时表
     * 
     * @param customerId 首页典型客户配置临时表主键
     * @return 首页典型客户配置临时表
     */
    TxwxTypicalCustomerTemp selectTypicalCustomerTempByCustomerId(@Param("customerId") Long customerId);

    /**
     * 查询首页典型客户配置临时表列表
     * 
     * @param typicalCustomerTemp 首页典型客户配置临时表
     * @return 首页典型客户配置临时表集合
     */
    List<TxwxTypicalCustomerTemp> selectTypicalCustomerTempList(TxwxTypicalCustomerTemp typicalCustomerTemp);

    /**
     * 查询显示状态的典型客户临时表列表
     * 
     * @param isShow 是否显示
     * @return 首页典型客户配置临时表集合
     */
    List<TxwxTypicalCustomerTemp> selectShowTypicalCustomerTempList(@Param("isShow") String isShow);

    /**
     * 新增首页典型客户配置临时表
     * 
     * @param typicalCustomerTemp 首页典型客户配置临时表
     * @return 结果
     */
    int insertTypicalCustomerTemp(TxwxTypicalCustomerTemp typicalCustomerTemp);

    /**
     * 修改首页典型客户配置临时表
     * 
     * @param typicalCustomerTemp 首页典型客户配置临时表
     * @return 结果
     */
    int updateTypicalCustomerTemp(TxwxTypicalCustomerTemp typicalCustomerTemp);

    /**
     * 更新客户排序
     * 
     * @param customerId 客户ID
     * @param sortOrder 排序号
     * @return 结果
     */
    int updateCustomerSort(@Param("customerId") Long customerId, @Param("sortOrder") Integer sortOrder);

    /**
     * 删除首页典型客户配置临时表
     * 
     * @param customerId 首页典型客户配置临时表主键
     * @return 结果
     */
    int deleteTypicalCustomerTempByCustomerId(@Param("customerId") Long customerId);

    /**
     * 批量删除首页典型客户配置临时表
     * 
     * @param customerIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteTypicalCustomerTempByCustomerIds(@Param("customerIds") Long[] customerIds);

    /**
     * 清空首页典型客户配置临时表
     * 
     * @return 结果
     */
    int clearTypicalCustomerTemp();

    /**
     * 将临时表数据复制到正式表
     * 
     * @return 结果
     */
    int copyTempToFormal();

    /**
     * 批量保存典型客户配置
     * 
     * @param typicalCustomerTempList 典型客户配置临时表列表
     * @return 结果
     */
    int batchInsertTypicalCustomerTemp(@Param("typicalCustomerTempList") List<TxwxTypicalCustomerTemp> typicalCustomerTempList);

    /**
     * 获取典型客户的最大排序号
     * 
     * @return 最大排序号
     */
    Integer getMaxSortOrder();
}