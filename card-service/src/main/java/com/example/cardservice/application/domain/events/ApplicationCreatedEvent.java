package com.example.cardservice.application.domain.events;

import com.example.cardservice.application.providers.CustomerInfo;

import java.util.UUID;

public record ApplicationCreatedEvent(
        UUID applicationId,
        CustomerInfo customerInfo
) { }
