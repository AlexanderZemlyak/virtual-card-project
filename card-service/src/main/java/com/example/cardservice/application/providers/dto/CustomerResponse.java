package com.example.cardservice.application.providers.dto;

import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        Integer age
) {
}
