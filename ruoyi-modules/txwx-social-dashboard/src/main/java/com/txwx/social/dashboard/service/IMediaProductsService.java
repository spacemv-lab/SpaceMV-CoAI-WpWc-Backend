package com.txwx.social.dashboard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.txwx.social.dashboard.domain.entity.mysql.MediaProduct;
import com.txwx.social.dashboard.domain.vo.MediaProductVo;

import java.util.List;

public interface IMediaProductsService extends IService<MediaProduct> {

    List<MediaProductVo> selectList();

    boolean delete(List<Long> ids);

    MediaProductVo selectById(Long id);

    boolean add(MediaProduct mediaProduct);
}
