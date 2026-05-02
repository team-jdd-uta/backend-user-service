package com.teamuta.userinfoserver.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class SchemaInitializer {

    @Bean
    public DataSourceInitializer primaryShardSchemaInitializer(
            @Qualifier("primaryShardDataSource") DataSource dataSource) {
        return buildInitializer(dataSource);
    }

    @Bean
    public DataSourceInitializer secondaryShardSchemaInitializer(
            @Qualifier("secondaryShardDataSource") DataSource dataSource) {
        return buildInitializer(dataSource);
    }

    private DataSourceInitializer buildInitializer(DataSource dataSource) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("schema.sql"));

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(populator);
        return initializer;
    }
}
