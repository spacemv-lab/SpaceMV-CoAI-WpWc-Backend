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

import com.txwx.web.domain.TxwxCarouselImageTemp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 首页轮播图配置临时表Mapper接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface TxwxCarouselImageTempMapper
{
    /**
     * 查询首页轮播图配置临时表
     * 
     * @param carouselId 首页轮播图配置临时表主键
     * @return 首页轮播图配置临时表
     */
    TxwxCarouselImageTemp selectCarouselImageTempByCarouselId(@Param("carouselId") Long carouselId);

    /**
     * 查询首页轮播图配置临时表列表
     * 
     * @param carouselImageTemp 首页轮播图配置临时表
     * @return 首页轮播图配置临时表集合
     */
    List<TxwxCarouselImageTemp> selectCarouselImageTempList(TxwxCarouselImageTemp carouselImageTemp);

    /**
     * 查询显示状态的轮播图临时表列表
     * 
     * @param isShow 是否显示
     * @return 首页轮播图配置临时表集合
     */
    List<TxwxCarouselImageTemp> selectShowCarouselImageTempList(@Param("isShow") String isShow);

    /**
     * 新增首页轮播图配置临时表
     * 
     * @param carouselImageTemp 首页轮播图配置临时表
     * @return 结果
     */
    int insertCarouselImageTemp(TxwxCarouselImageTemp carouselImageTemp);

    /**
     * 修改首页轮播图配置临时表
     * 
     * @param carouselImageTemp 首页轮播图配置临时表
     * @return 结果
     */
    int updateCarouselImageTemp(TxwxCarouselImageTemp carouselImageTemp);

    /**
     * 更新轮播图排序
     * 
     * @param carouselId 轮播图ID
     * @param sortOrder 排序号
     * @return 结果
     */
    int updateCarouselSort(@Param("carouselId") Long carouselId, @Param("sortOrder") Integer sortOrder);

    /**
     * 删除首页轮播图配置临时表
     * 
     * @param carouselId 首页轮播图配置临时表主键
     * @return 结果
     */
    int deleteCarouselImageTempByCarouselId(@Param("carouselId") Long carouselId);

    /**
     * 批量删除首页轮播图配置临时表
     * 
     * @param carouselIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteCarouselImageTempByCarouselIds(@Param("carouselIds") Long[] carouselIds);

    /**
     * 清空首页轮播图配置临时表
     * 
     * @return 结果
     */
    int clearCarouselImageTemp();

    /**
     * 将临时表数据复制到正式表
     * 
     * @return 结果
     */
    int copyTempToFormal();

    /**
     * 批量保存轮播图配置
     * 
     * @param carouselImageTempList 轮播图配置临时表列表
     * @return 结果
     */
    int batchInsertCarouselImageTemp(@Param("carouselImageTempList") List<TxwxCarouselImageTemp> carouselImageTempList);

    /**
     * 获取轮播图的最大排序号
     * 
     * @return 最大排序号
     */
    Integer getMaxSortOrder();
}