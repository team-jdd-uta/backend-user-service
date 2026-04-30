package com.teamuta.userinfoserver.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Configuration
public class RoutingDataSourceConfig {

    @Bean(name = "primaryShardDataSource")
    public DataSource primaryShardDataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password,
            @Value("${spring.datasource.driver-class-name}") String driverClassName) {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
                .build();
    }

    @Bean(name = "secondaryShardDataSource")
    public DataSource secondaryShardDataSource(
            @Value("${spring.datasource.shard2.url}") String url,
            @Value("${spring.datasource.shard2.username}") String username,
            @Value("${spring.datasource.shard2.password}") String password,
            @Value("${spring.datasource.driver-class-name}") String driverClassName) {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
                .build();
    }

    @Bean
    @Primary
    public DataSource dataSource(
            @Qualifier("primaryShardDataSource") DataSource primaryShardDataSource,
            @Qualifier("secondaryShardDataSource") DataSource secondaryShardDataSource) {

        AbstractRoutingDataSource routingDataSource = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return CustomerShardContext.getCurrentShard();
            }
        };

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(CustomerShardContext.SHARD_3307, primaryShardDataSource);
        targetDataSources.put(CustomerShardContext.SHARD_3309, secondaryShardDataSource);

        routingDataSource.setDefaultTargetDataSource(primaryShardDataSource);
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }
}