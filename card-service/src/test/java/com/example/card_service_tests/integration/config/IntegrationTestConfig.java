package com.example.card_service_tests.integration.config;

import com.example.card_service.application.providers.CustomerProvider;
import com.example.card_service.config.AppConfig;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        AppConfig.class,
        TestDatabaseConfig.class
})
public class IntegrationTestConfig {

    @Bean
    public CustomerProvider customerProvider() {
        return Mockito.mock(CustomerProvider.class);
    }

}