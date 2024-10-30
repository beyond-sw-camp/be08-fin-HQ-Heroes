package com.hq.heroes.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class MetaDBConfig {

    @Value("${spring.datasource-batch.url}")
    private String batchUrl;

    @Value("${spring.datasource-batch.username}")
    private String batchUsername;

    @Value("${spring.datasource-batch.password}")
    private String batchPassword;

    @Value("${spring.datasource-batch.driver-class-name}")
    private String batchDriverClassName;

    @Bean
    @Primary
    public DataSource metaDBSource() {

        return DataSourceBuilder.create()
                .url(batchUrl)
                .username(batchUsername)
                .password(batchPassword)
                .driverClassName(batchDriverClassName)
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager metaTransactionManager() {

        return new DataSourceTransactionManager(metaDBSource());
    }

}
