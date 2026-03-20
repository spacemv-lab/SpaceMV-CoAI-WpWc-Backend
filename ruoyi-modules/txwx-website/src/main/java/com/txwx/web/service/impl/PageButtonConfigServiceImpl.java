package com.txwx.web.service.impl;

import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.web.domain.DropButton;
import com.txwx.web.domain.TxwxPageButton;
import com.txwx.web.domain.TxwxPageButtonTemp;
import com.txwx.web.mapper.TxwxDropButtonMapper;
import com.txwx.web.mapper.TxwxDropButtonTempMapper;
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

    @Autowired
    private TxwxDropButtonTempMapper dropButtonTempMapper;

    @Autowired
    private TxwxDropButtonMapper dropButtonMapper;

    // ========================= 保存操作 =========================

    @Override
    @Transactional
    public int savePageButtonConfig(String pageCode, List<TxwxPageButtonTemp> pageButtonTempList)
    {
        // 先获取该页面的所有按钮ID，清空对应的下拉按钮数据
        List<TxwxPageButtonTemp> oldButtons = pageButtonTempMapper.selectPageButtonTempListByPageCode(pageCode);
        if (oldButtons != null && !oldButtons.isEmpty()) {
            for (TxwxPageButtonTemp button : oldButtons) {
                dropButtonTempMapper.deleteDropButtonTempByButtonId(button.getButtonId());
            }
        }
        // 再清空该页面的临时表数据
        pageButtonTempMapper.clearPageButtonTempByPageCode(pageCode);

        int result = 0;
        if (pageButtonTempList != null && !pageButtonTempList.isEmpty())
        {
            // 逐个插入按钮数据，以便获取每个按钮的buttonId
            for (TxwxPageButtonTemp button : pageButtonTempList) {
                result += pageButtonTempMapper.insertPageButtonTemp(button);

                // 如果按钮类型是下拉按钮（buttonType=3），且有下拉按钮列表
                if ("3".equals(button.getButtonType()) && button.getDropButtonList() != null && !button.getDropButtonList().isEmpty()) {
                    // 设置按钮ID关联
                    for (DropButton dropButton : button.getDropButtonList()) {
                        dropButton.setButtonId(button.getButtonId());
                    }
                    // 批量插入下拉按钮数据
                    dropButtonTempMapper.batchInsertDropButtonTemp(button.getDropButtonList());
                }
            }
        }

        return result;
    }

    // ========================= 预览操作 =========================

    @Override
    public List<TxwxPageButtonTemp> previewPageButtonConfig(String pageCode)
    {
        List<TxwxPageButtonTemp> buttonList = pageButtonTempMapper.selectPageButtonTempListByPageCode(pageCode);
        // 为每个按钮加载下拉按钮列表
        if (buttonList != null && !buttonList.isEmpty()) {
            for (TxwxPageButtonTemp button : buttonList) {
                if ("3".equals(button.getButtonType())) {
                    List<DropButton> dropButtonList = dropButtonTempMapper.selectDropButtonTempListByButtonId(button.getButtonId());
                    button.setDropButtonList(dropButtonList);
                }
            }
        }
        return buttonList;
    }

    @Override
    public List<TxwxPageButtonTemp> previewShowPageButtonConfig(String pageCode)
    {
        List<TxwxPageButtonTemp> buttonList = pageButtonTempMapper.selectShowPageButtonTempListByPageCode(pageCode, "1");
        // 为每个按钮加载下拉按钮列表
        if (buttonList != null && !buttonList.isEmpty()) {
            for (TxwxPageButtonTemp button : buttonList) {
                if ("3".equals(button.getButtonType())) {
                    List<DropButton> dropButtonList = dropButtonTempMapper.selectDropButtonTempListByButtonId(button.getButtonId());
                    button.setDropButtonList(dropButtonList);
                }
            }
        }
        return buttonList;
    }

    @Override
    public List<TxwxPageButtonTemp> previewPublishPageButtonConfig(String pageCode, String isPublish) {
        List<TxwxPageButtonTemp> buttonList = pageButtonTempMapper.selectIsPublishPageButtonTempListByPageCode(pageCode, isPublish);
        // 为每个按钮加载下拉按钮列表
        if (buttonList != null && !buttonList.isEmpty()) {
            for (TxwxPageButtonTemp button : buttonList) {
                if ("3".equals(button.getButtonType())) {
                    List<DropButton> dropButtonList = dropButtonTempMapper.selectDropButtonTempListByButtonId(button.getButtonId());
                    button.setDropButtonList(dropButtonList);
                }
            }
        }
        return buttonList;
    }

    // ========================= 展示操作（正式表数据） =========================

    @Override
    public List<TxwxPageButton> displayPageButtonConfig(String pageCode)
    {
        List<TxwxPageButton> buttonList = pageButtonMapper.selectPageButtonListByPageCode(pageCode);
        // 为每个按钮加载下拉按钮列表
        if (buttonList != null && !buttonList.isEmpty()) {
            for (TxwxPageButton button : buttonList) {
                if ("3".equals(button.getButtonType())) {
                    List<DropButton> dropButtonList = dropButtonMapper.selectDropButtonListByButtonId(button.getButtonId());
                    button.setDropButtonList(dropButtonList);
                }
            }
        }
        return buttonList;
    }

    @Override
    public List<TxwxPageButton> displayShowPageButtonConfig(String pageCode)
    {
        List<TxwxPageButton> buttonList = pageButtonMapper.selectShowPageButtonListByPageCode(pageCode, "1");
        // 为每个按钮加载下拉按钮列表
        if (buttonList != null && !buttonList.isEmpty()) {
            for (TxwxPageButton button : buttonList) {
                if ("3".equals(button.getButtonType())) {
                    List<DropButton> dropButtonList = dropButtonMapper.selectDropButtonListByButtonId(button.getButtonId());
                    button.setDropButtonList(dropButtonList);
                }
            }
        }
        return buttonList;
    }

    // ========================= 发布操作 =========================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int publishPageButtonConfig(String pageCode)
    {
        int result = 0;
        //String username = SecurityUtils.getLoginUser().getUsername();
        String username = "admin";
        TxwxPageButton txwxPageButton = new TxwxPageButton();
        txwxPageButton.setPublishedBy(username);
        txwxPageButton.setPublishedTime(DateUtils.getNowDate());

        // 1. 清空该页面在正式表中的数据（包括下拉按钮数据）
        List<TxwxPageButton> oldButtons = pageButtonMapper.selectPageButtonListByPageCode(pageCode);
        if (oldButtons != null && !oldButtons.isEmpty()) {
            for (TxwxPageButton button : oldButtons) {
                dropButtonMapper.deleteDropButtonByButtonId(button.getButtonId());
            }
        }
        pageButtonMapper.deletePageButtonByPageCode(pageCode);

        // 2. 将临时表中的该页面所有数据的isPublish置为1
        pageButtonTempMapper.updateAllPublish();

        // 3. 将临时表中该页面的数据逐条插入到正式表，以便获取正式表的buttonId
        List<TxwxPageButtonTemp> tempButtons = pageButtonTempMapper.selectPageButtonTempListByPageCode(pageCode);
        if (tempButtons != null && !tempButtons.isEmpty()) {
            for (TxwxPageButtonTemp tempButton : tempButtons) {
                // 复制临时表数据到正式表对象
                TxwxPageButton formalButton = new TxwxPageButton();
                formalButton.setPageCode(tempButton.getPageCode());
                formalButton.setButtonType(tempButton.getButtonType());
                formalButton.setButtonText(tempButton.getButtonText());
                formalButton.setBackgroundColor(tempButton.getBackgroundColor());
                formalButton.setJumpUrl(tempButton.getJumpUrl());
                formalButton.setImageUrl(tempButton.getImageUrl());
                formalButton.setIsShow(tempButton.getIsShow());
                formalButton.setIsPublish(tempButton.getIsPublish());
                formalButton.setState(tempButton.getState());
                formalButton.setSortOrder(tempButton.getSortOrder());
                formalButton.setCreateBy(tempButton.getCreateBy());
                formalButton.setCreateTime(tempButton.getCreateTime());
                formalButton.setUpdateBy(tempButton.getUpdateBy());
                formalButton.setUpdateTime(tempButton.getUpdateTime());
                formalButton.setRemark(tempButton.getRemark());
                formalButton.setSavedBy(tempButton.getSavedBy());
                formalButton.setSavedTime(tempButton.getSavedTime());
                formalButton.setPublishedBy(tempButton.getPublishedBy());
                formalButton.setPublishedTime(tempButton.getPublishedTime());

                // 插入正式表，获取新的buttonId
                result += pageButtonMapper.insertPageButton(formalButton);

                // 4. 如果按钮类型是下拉按钮，将下拉按钮数据从临时表复制到正式表
                if ("3".equals(tempButton.getButtonType())) {
                    List<DropButton> dropButtonList = dropButtonTempMapper.selectDropButtonTempListByButtonId(tempButton.getButtonId());
                    if (dropButtonList != null && !dropButtonList.isEmpty()) {
                        // 为每个下拉按钮设置新的buttonId并插入
                        for (DropButton dropButton : dropButtonList) {
                            dropButton.setButtonId(formalButton.getButtonId());
                            dropButtonMapper.insertDropButton(dropButton);
                        }
                    }
                }
            }
        }

        // 5. 更新本次发布的发布人和发布时间
        pageButtonMapper.updatePageButtonByAndTime(txwxPageButton);

        // 6. 将临时表清空（包括按钮表和下拉按钮表）
        // 先清空该页面在临时表中的下拉按钮数据
        for (TxwxPageButtonTemp tempButton : tempButtons) {
            dropButtonTempMapper.deleteDropButtonTempByButtonId(tempButton.getButtonId());
        }
        // 再清空按钮临时表
        pageButtonTempMapper.clearPageButtonTempByPageCode(pageCode);

        return Math.max(result, 0);
    }

    @Override
    @Transactional
    public int publishAllPageButtonConfig()
    {
        int result = 0;

        // 1. 清空正式表（包括下拉按钮数据）
        pageButtonMapper.clearPageButton();
        dropButtonMapper.clearDropButton();

        // 2. 将临时表数据复制到正式表
        result += pageButtonTempMapper.copyTempToFormal();

        // 3. 清空临时表（包括下拉按钮数据）
        dropButtonTempMapper.clearDropButtonTemp();
        pageButtonTempMapper.clearPageButtonTemp();

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
