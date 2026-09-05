package com.example.cardservice.application.providers;

import com.example.cardservice.application.providers.dto.CustomerResponse;

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
