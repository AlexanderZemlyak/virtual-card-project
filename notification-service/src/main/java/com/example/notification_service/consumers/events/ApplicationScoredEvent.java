package com.example.notification_service.consumers.events;

import java.util.UUID;

public record ApplicationScoredEvent(
        UUID applicationId,
        UUID customerId
) {
}