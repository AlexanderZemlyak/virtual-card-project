package com.example.card_service.application.providers;

import com.example.card_service.application.providers.dto.CustomerResponse;

import java.util.UUID;

public record CustomerInfo(
        UUID customerId,
        Integer customerAge
) {
    public static CustomerInfo from(CustomerResponse customerResponse) {
        return new CustomerInfo(
                customerResponse.id(),
                customerResponse.age()
        );
    }
}
