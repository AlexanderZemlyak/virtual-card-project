package com.example.cardservice.application.db.entities;

import com.example.cardservice.application.enums.ApplicationStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "applications")
public class CardApplicationEntity {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ApplicationStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public CardApplicationEntity() {
    }

    public CardApplicationEntity(UUID id, UUID customerId, ApplicationStatus status, Instant createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.createdAt = createdAt;
    }

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

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
}
