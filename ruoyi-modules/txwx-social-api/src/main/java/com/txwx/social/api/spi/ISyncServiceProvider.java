package com.txwx.social.api.spi;

import com.txwx.social.api.domain.dto.WriteResultDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 同步服务提供者接口（SPI）
 * 由各渠道（微信、抖音、小红书等）具体实现
 *
 * @author txwx
 * @date 2026-04-06
 */
public interface ISyncServiceProvider {

    /**
     * 获取渠道类型
     *
     * @return 渠道类型
     */
    String getChannelType();

    /**
     * 获取访问令牌
     *
     * @param accountId 账号ID
     * @return 访问令牌
     */
    String getAccessToken(Long accountId);

    /**
     * 从三方平台获取数据
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param accountId 账号ID
     * @return Map<String, List<?>> key为数据类型，value为数据列表
     */
    Map<String, List<?>> fetchData(LocalDate startDate, LocalDate endDate, Long accountId);

    /**
     * 将数据写入ClickHouse（分离执行，先写入ods层，再聚合到dws层）
     *
     * @param dataMap Map<String, List<?>> 数据列表
     * @param accountId 账号ID
     * @return 写入结果
     */
    WriteResultDTO writeData(Map<String, List<?>> dataMap, Long accountId);
}
