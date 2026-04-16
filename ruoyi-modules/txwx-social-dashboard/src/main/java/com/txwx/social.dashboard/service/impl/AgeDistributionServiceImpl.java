package com.txwx.social.dashboard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.txwx.social.dashboard.domain.entity.AgeDistribution;
import com.txwx.social.dashboard.domain.mapper.AgeDistributionMapper;
import com.txwx.social.dashboard.service.IAgeDistributionService;
import org.springframework.stereotype.Service;

@Service
public class AgeDistributionServiceImpl extends ServiceImpl<AgeDistributionMapper, AgeDistribution> implements IAgeDistributionService {
}
