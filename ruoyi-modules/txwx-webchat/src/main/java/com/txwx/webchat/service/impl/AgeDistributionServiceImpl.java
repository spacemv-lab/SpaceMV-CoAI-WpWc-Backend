package com.txwx.webchat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.txwx.webchat.domain.entity.AgeDistribution;
import com.txwx.webchat.mapper.AgeDistributionMapper;
import com.txwx.webchat.service.IAgeDistributionService;
import org.springframework.stereotype.Service;

@Service
public class AgeDistributionServiceImpl extends ServiceImpl<AgeDistributionMapper, AgeDistribution> implements IAgeDistributionService {
}
