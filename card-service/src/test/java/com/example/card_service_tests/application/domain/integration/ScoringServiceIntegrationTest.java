package com.example.card_service_tests.application.domain.integration;

import com.example.card_service.application.db.CardApplicationRepository;
import com.example.card_service.application.db.entities.CardApplicationEntity;
import com.example.card_service.application.domain.events.ApplicationCreatedEvent;
import com.example.card_service.application.enums.ApplicationStatus;
import com.example.card_service.application.providers.CustomerInfo;
import com.example.card_service.outbox.OutboxEventRepository;
import com.example.card_service.producers.events.ApplicationScoredEvent;
import com.example.card_service_tests.integration.IntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;


class ScoringServiceIntegrationTest extends IntegrationTest {

    private static final String APPROVED_TOPIC =
            "application-approved";

    private static final String REJECTED_TOPIC =
            "application-rejected";

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private CardApplicationRepository repository;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
        outboxEventRepository.deleteAll();
    }

    private void scoreApplication(
            int age,
            ApplicationStatus expectedStatus,
            String expectedEventType,
            String expectedTopic
    ) throws JsonProcessingException {

        UUID applicationId = UUID.randomUUID();

        CustomerInfo customerInfo = new CustomerInfo(
                UUID.randomUUID(),
                age
        );

        repository.save(new CardApplicationEntity(
                applicationId,
                customerInfo.customerId(),
                ApplicationStatus.CREATED,
                Instant.now()
        ));

        eventPublisher.publishEvent(
                new ApplicationCreatedEvent(applicationId, customerInfo)
        );

        TestTransaction.flagForCommit();
        TestTransaction.end();

        await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    var savedEntity = repository.findById(applicationId)
                            .orElseThrow();

                    assertEquals(expectedStatus, savedEntity.getStatus());

                    var outboxEvents = outboxEventRepository.findAll();

                    assertEquals(1, outboxEvents.size());

                    var outboxEvent = outboxEvents.getFirst();

                    assertEquals(expectedEventType, outboxEvent.getEventType());
                    assertEquals(expectedTopic, outboxEvent.getTopic());

                    assertEquals(
                            objectMapper.writeValueAsString(
                                    new ApplicationScoredEvent(
                                            applicationId,
                                            customerInfo.customerId()
                                    )
                            ),
                            outboxEvent.getPayload()
                    );
                });
    }

    @Test
    @Transactional
    void scoreApplication_shouldApproveIfAgeIsGreater18() throws JsonProcessingException {
        scoreApplication(
                20,
                ApplicationStatus.APPROVED,
                "APPLICATION_APPROVED",
                APPROVED_TOPIC
        );
    }

    @Test
    @Transactional
    void scoreApplication_shouldRejectIfAgeIsLess18() throws JsonProcessingException {
        scoreApplication(
                10,
                ApplicationStatus.REJECTED,
                "APPLICATION_REJECTED",
                REJECTED_TOPIC
        );
    }
}
