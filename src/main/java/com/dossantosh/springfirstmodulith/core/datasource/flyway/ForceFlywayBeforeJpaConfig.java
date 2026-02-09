package com.dossantosh.springfirstmodulith.core.datasource.flyway;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ForceFlywayBeforeJpaConfig {

    @Bean
    static BeanFactoryPostProcessor flywayBeforeEntityManagerFactory() {
        return new BeanFactoryPostProcessor() {
            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory bf) throws BeansException {
                // Boot’s standard EMF bean name
                if (bf.containsBeanDefinition("entityManagerFactory")) {
                    bf.getBeanDefinition("entityManagerFactory")
                      .setDependsOn("flywayProd", "flywayHistoric");
                }
            }
        };
    }
}
