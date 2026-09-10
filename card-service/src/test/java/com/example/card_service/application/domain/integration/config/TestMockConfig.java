package com.example.card_service.application.domain.integration.config;

import com.example.card_service.application.domain.ScoringService;
import com.example.card_service.application.providers.CustomerProvider;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestMockConfig {

    @Bean
    public CustomerProvider customerProvider() {
        return Mockito.mock(CustomerProvider.class);
    }

    @Bean
    public ScoringService scoringService() {
        return Mockito.mock(ScoringService.class);
    }
}