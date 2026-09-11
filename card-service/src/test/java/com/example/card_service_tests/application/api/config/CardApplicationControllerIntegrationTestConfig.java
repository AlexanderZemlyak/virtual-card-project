package com.example.card_service_tests.application.api.config;

import com.example.card_service.application.domain.ScoringService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CardApplicationControllerIntegrationTestConfig {

    @Bean
    public ScoringService scoringService() {
        return Mockito.mock(ScoringService.class);
    }

}
