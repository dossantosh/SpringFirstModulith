package com.dossantosh.springfirstmodulith.core.datasource.runtime;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulator;

import javax.sql.DataSource;

/**
 * Optional: Initialize the historic database using a SQL script at startup.
 *
 * <p>
 * Enable with: app.datasource.historic.init=true
 * </p>
 */
@Configuration
public class HistoricSqlInitConfig {

    @Bean
    @ConditionalOnProperty(name = "app.datasource.historic.init", havingValue = "true")
    public DatabasePopulator historicDatabasePopulator(
            @Qualifier("historicDataSource") DataSource historicDataSource
    ) {
        ResourceDatabasePopulator populator =
                new ResourceDatabasePopulator(new ClassPathResource("data-postgres.sql"));
        populator.execute(historicDataSource);
        return populator;
    }
}
