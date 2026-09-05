package com.example.cardservice.application.domain;

import com.example.cardservice.application.enums.ApplicationStatus;

import java.time.Instant;
import java.util.UUID;

public class CardApplication {

    private final UUID id;
    private final UUID customerId;
    private ApplicationStatus status;
    private final Instant createdAt;

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public CardApplication(
            UUID id,
            UUID customerId,
            ApplicationStatus status,
            Instant createdAt
    ) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public void setStatus(ApplicationStatus newStatus) {
        this.status = newStatus;
    }
}
