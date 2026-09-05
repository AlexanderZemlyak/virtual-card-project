package com.example.cardservice.producers.events;

import java.util.UUID;

public record ApplicationScoredEvent(
        UUID applicationId,
        UUID customerId
) { }
