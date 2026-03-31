package com.txwx.webchat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.txwx.webchat.domain.entity.mysql.MediaPlatform;
import com.txwx.webchat.domain.vo.UserPlatformVo;

import java.util.List;

public interface IMediaPlatformService extends IService<MediaPlatform> {
    boolean saveUpdate(MediaPlatform mediaPlatform);

    Boolean dataSync(String appId) throws Exception;

    boolean delete(Long id);

    List<UserPlatformVo> selectList();
}
