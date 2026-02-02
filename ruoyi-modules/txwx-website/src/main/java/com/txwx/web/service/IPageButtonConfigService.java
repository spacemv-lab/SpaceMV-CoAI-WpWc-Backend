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
package com.txwx.web.service;

import com.txwx.web.domain.TxwxPageButton;
import com.txwx.web.domain.TxwxPageButtonTemp;

import java.util.List;

/**
 * 页面按钮配置Service接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface IPageButtonConfigService 
{
    // ========================= 保存操作 =========================
    
    /**
     * 保存页面按钮配置（到临时表）
     * 
     * @param pageCode 页面代码
     * @param pageButtonTempList 按钮配置临时表列表
     * @return 结果
     */
    int savePageButtonConfig(String pageCode, List<TxwxPageButtonTemp> pageButtonTempList);

    // ========================= 预览操作 =========================
    
    /**
     * 获取临时表中的按钮配置列表
     * 
     * @param pageCode 页面代码
     * @return 按钮配置临时表集合
     */
    List<TxwxPageButtonTemp> previewPageButtonConfig(String pageCode);

    /**
     * 获取临时表中展示状态的按钮配置列表
     * 
     * @param pageCode 页面代码
     * @return 按钮配置临时表集合
     */
    List<TxwxPageButtonTemp> previewShowPageButtonConfig(String pageCode);


    List<TxwxPageButtonTemp> previewPublishPageButtonConfig(String pageCode, String isPublish);

    // ========================= 展示操作（正式表数据） =========================
    
    /**
     * 获取正式表中的按钮配置列表
     * 
     * @param pageCode 页面代码
     * @return 按钮配置正式表集合
     */
    List<TxwxPageButton> displayPageButtonConfig(String pageCode);

    /**
     * 获取正式表中展示状态的按钮配置列表
     * 
     * @param pageCode 页面代码
     * @return 按钮配置正式表集合
     */
    List<TxwxPageButton> displayShowPageButtonConfig(String pageCode);

    // ========================= 发布操作 =========================
    
    /**
     * 发布页面按钮配置
     * 
     * @param pageCode 页面代码
     * @return 结果
     */
    int publishPageButtonConfig(String pageCode);

    /**
     * 发布所有页面按钮配置
     * 
     * @return 结果
     */
    int publishAllPageButtonConfig();

    // ========================= 删除操作 =========================
    
    /**
     * 批量删除按钮配置临时表
     * 
     * @param buttonIds 需要删除的按钮ID集合
     * @return 结果
     */
    int deletePageButtonTempByButtonIds(Long[] buttonIds);

    /**
     * 根据页面代码删除所有按钮配置临时表
     * 
     * @param pageCode 页面代码
     * @return 结果
     */
    int deletePageButtonTempByPageCode(String pageCode);

    // ========================= 辅助操作 =========================
    
    /**
     * 获取临时表中按钮的最大排序号
     * 
     * @param pageCode 页面代码
     * @return 最大排序号
     */
    Integer getMaxSortOrderInTemp(String pageCode);

    /**
     * 获取正式表中按钮的最大排序号
     * 
     * @param pageCode 页面代码
     * @return 最大排序号
     */
    Integer getMaxSortOrderInFormal(String pageCode);

    /**
     * 更新临时表中按钮排序
     * 
     * @param buttonId 按钮ID
     * @param sortOrder 排序号
     * @return 结果
     */
    int updateButtonSortInTemp(Long buttonId, Integer sortOrder);

    /**
     * 清空临时表数据
     * 
     * @return 结果
     */
    int clearTempData();
}