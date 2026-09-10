package com.example.card_service.producers.events;

import java.util.UUID;

public record ApplicationScoredEvent(
        UUID applicationId,
        UUID customerId
) { }
