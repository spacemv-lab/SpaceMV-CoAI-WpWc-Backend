package com.txwx.webchat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.txwx.webchat.domain.entity.ChannelComposition;
import com.txwx.webchat.mapper.ChannelCompositionMapper;
import com.txwx.webchat.service.IChannelCompositionService;
import org.springframework.stereotype.Service;

@Service
public class ChannelCompositionServiceImpl extends ServiceImpl<ChannelCompositionMapper, ChannelComposition> implements IChannelCompositionService {
}
