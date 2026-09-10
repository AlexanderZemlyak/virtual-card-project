package com.example.card_service.application.api.dto;

import com.example.card_service.application.enums.ApplicationStatus;

import java.util.UUID;

public record CardApplicationResponse(
        UUID applicationId,
        UUID customerId,
        ApplicationStatus status
        // Instant createdAt
) {
}