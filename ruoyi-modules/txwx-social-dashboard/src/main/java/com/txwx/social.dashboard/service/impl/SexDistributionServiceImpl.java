package com.txwx.social.dashboard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.txwx.social.dashboard.domain.entity.SexDistribution;
import com.txwx.social.dashboard.domain.mapper.SexDistributionMapper;
import com.txwx.social.dashboard.service.ISexDistributionService;
import org.springframework.stereotype.Service;

@Service
public class SexDistributionServiceImpl extends ServiceImpl<SexDistributionMapper, SexDistribution> implements ISexDistributionService {
}
