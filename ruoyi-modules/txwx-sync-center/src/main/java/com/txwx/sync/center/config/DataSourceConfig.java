package com.txwx.sync.center.config;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 数据源配置
 *
 * @author txwx
 * @date 2026-04-03
 */
@Configuration
@Import(DynamicDataSourceAutoConfiguration.class)
public class DataSourceConfig {
}
