package com.txwx.webchat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.txwx.webchat.domain.entity.SexDistribution;
import com.txwx.webchat.mapper.SexDistributionMapper;
import com.txwx.webchat.service.ISexDistributionService;
import org.springframework.stereotype.Service;

@Service
public class SexDistributionServiceImpl extends ServiceImpl<SexDistributionMapper, SexDistribution> implements ISexDistributionService {
}
