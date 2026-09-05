package com.example.cardservice.application.domain.integration.config;

import com.example.cardservice.config.AppConfig;
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