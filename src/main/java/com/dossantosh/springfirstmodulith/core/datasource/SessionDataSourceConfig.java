package com.dossantosh.springfirstmodulith.core.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.jdbc.config.annotation.SpringSessionDataSource;

import javax.sql.DataSource;

/**
 * Spring Session (JDBC) MUST use a stable, non-routed DataSource.
 */
@Configuration
public class SessionDataSourceConfig {

    @Bean
    @ConfigurationProperties("app.datasource.session")
    public DataSourceProperties sessionDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @SpringSessionDataSource
    public DataSource sessionDataSource(DataSourceProperties sessionDataSourceProperties) {
        return sessionDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }
}
