package com.example.cardservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class CustomerClientConfig {

    @Bean
    public RestClient customerRestClient() {
        return RestClient.builder()
                .baseUrl(
                        System.getenv().getOrDefault(
                        "USER_SERVICE_URL",
                        "http://localhost:8081")
                )
                .build();
    }
}
