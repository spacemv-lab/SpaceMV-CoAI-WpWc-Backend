package com.txwx.webchat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.txwx.webchat.domain.ArticleDetailDaily;
import com.txwx.webchat.domain.common.PageRequest;
import com.txwx.webchat.domain.common.PageResult;
import com.txwx.webchat.domain.condition.ContentDataSearchCondition;
import com.txwx.webchat.domain.entity.DwsContentData;
import com.txwx.webchat.domain.entity.DwsUsers;

public interface IDwsContentDataService extends IService<DwsContentData> {
    PageResult<DwsContentData> select(PageRequest pageRequest, ContentDataSearchCondition condition);
}
