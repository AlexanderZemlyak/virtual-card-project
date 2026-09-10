package com.example.card_service.application.domain.integration.config;

import com.example.card_service.config.AppConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        AppConfig.class,
        TestDatabaseConfig.class,
        TestMockConfig.class
})
public class IntegrationTestConfig {
}