package com.txwx.social.dashboard.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.txwx.social.dashboard.domain.entity.mysql.MediaPlatform;
import com.txwx.social.dashboard.mapper.MediaPlatformMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@DS("slave")
public class MediaPlatformStatusServiceImpl {

    @Autowired
    private MediaPlatformMapper mediaPlatformMapper;

    @Transactional(rollbackFor = Exception.class)
    public void updateSyncStatus(Long platformId, Integer syncStatus, String errorMsg) {
        MediaPlatform mediaPlatform = new MediaPlatform();
        mediaPlatform.setId(platformId);
        mediaPlatform.setSyncStatus(syncStatus);
        mediaPlatformMapper.updateById(mediaPlatform);
    }

    public MediaPlatform selectById(Long platformId) {
        return mediaPlatformMapper.selectById(platformId);
    }
}
