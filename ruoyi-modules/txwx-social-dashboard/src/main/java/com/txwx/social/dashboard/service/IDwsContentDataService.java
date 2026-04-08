package com.txwx.social.dashboard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.txwx.social.dashboard.domain.ArticleDetailDaily;
import com.txwx.social.dashboard.domain.common.PageRequest;
import com.txwx.social.dashboard.domain.common.PageResult;
import com.txwx.social.dashboard.domain.condition.ContentDataSearchCondition;
import com.txwx.social.dashboard.domain.entity.DwsContentData;
import com.txwx.social.dashboard.domain.entity.DwsUsers;

public interface IDwsContentDataService extends IService<DwsContentData> {
    PageResult<DwsContentData> select(PageRequest pageRequest, ContentDataSearchCondition condition);
}
