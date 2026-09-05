package com.example.cardservice.application.api.dto;

import com.example.cardservice.application.enums.ApplicationStatus;

import java.util.UUID;

public record CardApplicationResponse(
        UUID applicationId,
        UUID customerId,
        ApplicationStatus status
        // Instant createdAt
) {
}