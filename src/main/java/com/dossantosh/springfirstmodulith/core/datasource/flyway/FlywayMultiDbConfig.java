package com.dossantosh.springfirstmodulith.core.datasource.flyway;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class FlywayMultiDbConfig {

    /**
     * PROD database
     */
    @Bean
    public Flyway flywayProd(
            @Qualifier("prodDataSource") DataSource prodDataSource,
            Environment env
    ) {
        Flyway flyway = Flyway.configure()
                .dataSource(prodDataSource)
                .locations("classpath:db/common", "classpath:db/prod")
                .baselineOnMigrate(true)
                .load();

        runFlyway(flyway, env, "prod");
        return flyway;
    }

    /**
     * HISTORIC database
     */
    @Bean
    public Flyway flywayHistoric(
            @Qualifier("historicDataSource") DataSource historicDataSource,
            Environment env
    ) {
        Flyway flyway = Flyway.configure()
                .dataSource(historicDataSource)
                .locations("classpath:db/common")
                .baselineOnMigrate(true)
                .load();

        runFlyway(flyway, env, "historic");
        return flyway;
    }

    private void runFlyway(Flyway flyway, Environment env, String dbName) {
        // One toggle for both DBs:
        // APP_FLYWAY_REPAIR=true  -> repair + migrate
        // default false          -> migrate only
        boolean repair = Boolean.parseBoolean(env.getProperty("APP_FLYWAY_REPAIR", "false"));

        // Optional: allow only prod/historic if you want:
        // APP_FLYWAY_REPAIR_TARGET=prod|historic|both
        String target = env.getProperty("APP_FLYWAY_REPAIR_TARGET", "both");

        boolean shouldRepair = repair && ("both".equalsIgnoreCase(target) || dbName.equalsIgnoreCase(target));

        if (shouldRepair) {
            flyway.repair();
        }

        flyway.migrate();
    }
}
