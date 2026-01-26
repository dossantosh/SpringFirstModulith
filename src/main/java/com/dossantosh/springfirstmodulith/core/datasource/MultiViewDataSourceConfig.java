package com.dossantosh.springfirstmodulith.core.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Builds two physical DataSources (prod/historic) and exposes a {@link ViewRoutingDataSource}
 * as the application's primary {@link DataSource} so JPA repositories automatically route.
 */
@Configuration
public class MultiViewDataSourceConfig {

    @Bean
    @ConfigurationProperties("app.datasource.prod")
    public DataSourceProperties prodDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource prodDataSource(@Qualifier("prodDataSourceProperties") DataSourceProperties props) {
        return props.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    @ConfigurationProperties("app.datasource.historic")
    public DataSourceProperties historicDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource historicDataSource(@Qualifier("historicDataSourceProperties") DataSourceProperties props) {
        return props.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    @Primary
    public DataSource dataSource(@Qualifier("prodDataSource") DataSource prod,
                                 @Qualifier("historicDataSource") DataSource historic) {

        Map<Object, Object> targets = new HashMap<>();
        targets.put("prod", prod);
        targets.put("historic", historic);

        ViewRoutingDataSource routing = new ViewRoutingDataSource();
        routing.setTargetDataSources(targets);
        routing.setDefaultTargetDataSource(prod);
        return routing;
    }
}
