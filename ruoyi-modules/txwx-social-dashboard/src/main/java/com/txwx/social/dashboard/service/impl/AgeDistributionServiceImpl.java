/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.dashboard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.txwx.social.dashboard.domain.entity.AgeDistribution;
import com.txwx.social.dashboard.mapper.AgeDistributionMapper;
import com.txwx.social.dashboard.service.IAgeDistributionService;
import org.springframework.stereotype.Service;

@Service
public class AgeDistributionServiceImpl extends ServiceImpl<AgeDistributionMapper, AgeDistribution> implements IAgeDistributionService {
}
