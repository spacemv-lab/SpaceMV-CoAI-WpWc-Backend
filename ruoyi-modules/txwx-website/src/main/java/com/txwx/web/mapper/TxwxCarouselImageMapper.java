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

import com.txwx.web.domain.TxwxCarouselImage;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 首页轮播图配置Mapper接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface TxwxCarouselImageMapper
{
    /**
     * 查询首页轮播图配置
     * 
     * @param carouselId 首页轮播图配置主键
     * @return 首页轮播图配置
     */
    TxwxCarouselImage selectCarouselImageByCarouselId(@Param("carouselId") Long carouselId);

    /**
     * 查询首页轮播图配置列表
     * 
     * @param carouselImage 首页轮播图配置
     * @return 首页轮播图配置集合
     */
    List<TxwxCarouselImage> selectCarouselImageList(TxwxCarouselImage carouselImage);

    /**
     * 查询显示状态的轮播图列表
     * 
     * @param isShow 是否显示
     * @return 首页轮播图配置集合
     */
    List<TxwxCarouselImage> selectShowCarouselImageList(@Param("isShow") String isShow);

    /**
     * 新增首页轮播图配置
     * 
     * @param carouselImage 首页轮播图配置
     * @return 结果
     */
    int insertCarouselImage(TxwxCarouselImage carouselImage);

    /**
     * 修改首页轮播图配置
     * 
     * @param carouselImage 首页轮播图配置
     * @return 结果
     */
    int updateCarouselImage(TxwxCarouselImage carouselImage);

    /**
     * 删除首页轮播图配置
     * 
     * @param carouselId 首页轮播图配置主键
     * @return 结果
     */
    int deleteCarouselImageByCarouselId(@Param("carouselId") Long carouselId);

    /**
     * 批量删除首页轮播图配置
     * 
     * @param carouselIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteCarouselImageByCarouselIds(Long[] carouselIds);

    /**
     * 获取轮播图的最大排序号
     * 
     * @return 最大排序号
     */
    Integer getMaxSortOrder();

    /**
     * 更新轮播图排序
     * 
     * @param carouselId 轮播图ID
     * @param sortOrder 排序号
     * @return 结果
     */
    int updateCarouselSort(@Param("carouselId") Long carouselId, @Param("sortOrder") Integer sortOrder);

    /**
     * 清空首页轮播图配置正式表
     * 
     * @return 结果
     */
    int clearCarouselImage();

    /**
     * 更新发布人和发布时间
     *
     * @param publishedBy 发布人
     * @param publishedTime 发布时间
     */
    int updateByAndTime(@Param("publishedBy") String publishedBy, @Param("publishedTime") Date publishedTime);
}