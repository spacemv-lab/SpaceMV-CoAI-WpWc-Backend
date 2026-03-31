package com.txwx.webchat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.txwx.webchat.domain.entity.mysql.MediaProduct;
import com.txwx.webchat.domain.vo.MediaProductVo;

import java.util.List;

public interface IMediaProductsService extends IService<MediaProduct> {

    List<MediaProductVo> selectList();

    boolean delete(List<Long> ids);

    MediaProductVo selectById(Long id);

    boolean add(MediaProduct mediaProduct);
}
