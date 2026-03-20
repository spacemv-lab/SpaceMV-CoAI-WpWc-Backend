package com.txwx.webchat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.txwx.webchat.domain.entity.TerrainDistribution;
import com.txwx.webchat.mapper.TerrainDistributionMapper;
import com.txwx.webchat.service.ITerrainDistributionService;
import org.springframework.stereotype.Service;

@Service
public class TerrainDistributionServiceImpl extends ServiceImpl<TerrainDistributionMapper, TerrainDistribution> implements ITerrainDistributionService {
}
