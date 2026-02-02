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
package com.txwx.web.service.impl;

import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.web.domain.TxwxPageButton;
import com.txwx.web.domain.TxwxPageButtonTemp;
import com.txwx.web.mapper.TxwxPageButtonMapper;
import com.txwx.web.mapper.TxwxPageButtonTempMapper;
import com.txwx.web.service.IPageButtonConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 页面按钮配置Service业务层处理
 * 
 * @author txwx
 * @date 2025-12-06
 */
@Service
public class PageButtonConfigServiceImpl implements IPageButtonConfigService 
{
    @Autowired
    private TxwxPageButtonTempMapper pageButtonTempMapper;
    
    @Autowired
    private TxwxPageButtonMapper pageButtonMapper;

    // ========================= 保存操作 =========================

    @Override
    @Transactional
    public int savePageButtonConfig(String pageCode, List<TxwxPageButtonTemp> pageButtonTempList)
    {
        // 先清空该页面的临时表数据
        pageButtonTempMapper.clearPageButtonTempByPageCode(pageCode);
        
        int result = 0;
        if (pageButtonTempList != null && !pageButtonTempList.isEmpty())
        {
            // 批量插入新数据
            result = pageButtonTempMapper.batchInsertPageButtonTemp(pageButtonTempList);
        }
        
        return result;
    }

    // ========================= 预览操作 =========================

    @Override
    public List<TxwxPageButtonTemp> previewPageButtonConfig(String pageCode)
    {
        return pageButtonTempMapper.selectPageButtonTempListByPageCode(pageCode);
    }

    @Override
    public List<TxwxPageButtonTemp> previewShowPageButtonConfig(String pageCode)
    {
        return pageButtonTempMapper.selectShowPageButtonTempListByPageCode(pageCode, "1");
    }

    @Override
    public List<TxwxPageButtonTemp> previewPublishPageButtonConfig(String pageCode, String isPublish) {
        return pageButtonTempMapper.selectIsPublishPageButtonTempListByPageCode(pageCode, isPublish);
    }

    // ========================= 展示操作（正式表数据） =========================

    @Override
    public List<TxwxPageButton> displayPageButtonConfig(String pageCode)
    {
        return pageButtonMapper.selectPageButtonListByPageCode(pageCode);
    }

    @Override
    public List<TxwxPageButton> displayShowPageButtonConfig(String pageCode)
    {
        return pageButtonMapper.selectShowPageButtonListByPageCode(pageCode, "1");
    }

    // ========================= 发布操作 =========================

    @Override
    @Transactional
    public int publishPageButtonConfig(String pageCode)
    {
        int result = 0;
        String username = SecurityUtils.getLoginUser().getUsername();
        TxwxPageButton txwxPageButton = new TxwxPageButton();
        txwxPageButton.setPublishedBy(username);
        txwxPageButton.setPublishedTime(DateUtils.getNowDate());
        
        // 1. 清空该页面在正式表中的数据
        pageButtonMapper.deletePageButtonByPageCode(pageCode);

        // 2.将临时表中的所有数据的isPublish置为1
        pageButtonTempMapper.updateAllPublish();
        
        // 3. 将临时表中该页面的数据复制到正式表
        result += pageButtonTempMapper.copyTempToFormalByPageCode(pageCode);

        // 4. 更新本次发布的发布人和发布时间
        pageButtonMapper.updatePageButtonByAndTime(txwxPageButton);

        // 5. 将临时表清空
        pageButtonTempMapper.clearPageButtonTempByPageCode(pageCode);
        
        return result;
    }

    @Override
    @Transactional
    public int publishAllPageButtonConfig()
    {
        int result = 0;
        
        // 1. 清空正式表
        pageButtonMapper.clearPageButton();
        
        // 2. 将临时表数据复制到正式表
        result += pageButtonTempMapper.copyTempToFormal();
        
        return result;
    }

    // ========================= 删除操作 =========================

    @Override
    public int deletePageButtonTempByButtonIds(Long[] buttonIds)
    {
        return pageButtonTempMapper.deletePageButtonTempByButtonIds(buttonIds);
    }

    @Override
    public int deletePageButtonTempByPageCode(String pageCode)
    {
        return pageButtonTempMapper.deletePageButtonTempByPageCode(pageCode);
    }

    // ========================= 辅助操作 =========================

    @Override
    public Integer getMaxSortOrderInTemp(String pageCode)
    {
        return pageButtonTempMapper.getMaxSortOrderByPageCode(pageCode);
    }

    @Override
    public Integer getMaxSortOrderInFormal(String pageCode)
    {
        return pageButtonMapper.getMaxSortOrderByPageCode(pageCode);
    }

    @Override
    public int updateButtonSortInTemp(Long buttonId, Integer sortOrder)
    {
        return pageButtonTempMapper.updateButtonSort(buttonId, sortOrder);
    }

    @Override
    @Transactional
    public int clearTempData()
    {
        return pageButtonTempMapper.clearPageButtonTemp();
    }
}