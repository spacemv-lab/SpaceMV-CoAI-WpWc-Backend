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

import com.txwx.web.domain.TxwxTypicalCustomer;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 首页典型客户配置Mapper接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface TxwxTypicalCustomerMapper
{
    /**
     * 查询首页典型客户配置
     * 
     * @param customerId 首页典型客户配置主键
     * @return 首页典型客户配置
     */
    TxwxTypicalCustomer selectTypicalCustomerByCustomerId(@Param("customerId") Long customerId);

    /**
     * 查询首页典型客户配置列表
     * 
     * @param typicalCustomer 首页典型客户配置
     * @return 首页典型客户配置集合
     */
    List<TxwxTypicalCustomer> selectTypicalCustomerList(TxwxTypicalCustomer typicalCustomer);

    /**
     * 查询显示状态的典型客户列表
     * 
     * @param isShow 是否显示
     * @return 首页典型客户配置集合
     */
    List<TxwxTypicalCustomer> selectShowTypicalCustomerList(@Param("isShow") String isShow);

    /**
     * 新增首页典型客户配置
     * 
     * @param typicalCustomer 首页典型客户配置
     * @return 结果
     */
    int insertTypicalCustomer(TxwxTypicalCustomer typicalCustomer);

    /**
     * 修改首页典型客户配置
     * 
     * @param typicalCustomer 首页典型客户配置
     * @return 结果
     */
    int updateTypicalCustomer(TxwxTypicalCustomer typicalCustomer);

    /**
     * 删除首页典型客户配置
     * 
     * @param customerId 首页典型客户配置主键
     * @return 结果
     */
    int deleteTypicalCustomerByCustomerId(@Param("customerId") Long customerId);

    /**
     * 批量删除首页典型客户配置
     * 
     * @param customerIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteTypicalCustomerByCustomerIds(Long[] customerIds);

    /**
     * 获取典型客户的最大排序号
     * 
     * @return 最大排序号
     */
    Integer getMaxSortOrder();

    /**
     * 更新客户排序
     * 
     * @param customerId 客户ID
     * @param sortOrder 排序号
     * @return 结果
     */
    int updateCustomerSort(@Param("customerId") Long customerId, @Param("sortOrder") Integer sortOrder);

    /**
     * 清空首页典型客户配置正式表
     * 
     * @return 结果
     */
    int clearTypicalCustomer();

    /**
     * 更新发布人和发布时间
     *
     * @param publishedBy 发布人
     * @param publishedTime 发布时间
     */
    int updateByAndTime(@Param("publishedBy") String publishedBy, @Param("publishedTime") Date publishedTime);
}