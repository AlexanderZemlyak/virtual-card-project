package com.example.card_service.application.domain.events;

import com.example.card_service.application.providers.CustomerInfo;

import java.util.UUID;

public record ApplicationCreatedEvent(
        UUID applicationId,
        CustomerInfo customerInfo
) { }
