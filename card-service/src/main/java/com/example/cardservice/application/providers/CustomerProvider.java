package com.example.cardservice.application.providers;

import com.example.cardservice.application.providers.dto.CustomerResponse;
import com.example.cardservice.application.providers.exceptions.CustomerServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Component
@Profile("!test")
public class CustomerProvider {

    private final RestClient restClient;
    private static final Logger log = LoggerFactory.getLogger(CustomerProvider.class);

    public CustomerProvider(
            RestClient customerRestClient
    ) {
        this.restClient = customerRestClient;
    }

    public CustomerResponse getCustomer(
            UUID customerId
    ) {
        try {
            return restClient.get()
                    .uri("/api/users/{id}", customerId)
                    .retrieve()
                    .body(CustomerResponse.class);
        } catch (RestClientException ex) {

            log.info("Ошибка соединения с сервисом пользователей:\n" + ex.getMessage());
            throw new CustomerServiceUnavailableException(
                    "Сервис пользователей недоступен"
            );
        }

    }
}
