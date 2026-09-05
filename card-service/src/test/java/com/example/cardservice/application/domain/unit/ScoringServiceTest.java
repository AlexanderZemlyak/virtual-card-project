package com.example.cardservice.application.domain.unit;

import com.example.cardservice.application.db.CardApplicationRepository;
import com.example.cardservice.application.db.entities.CardApplicationEntity;
import com.example.cardservice.application.domain.ScoringService;
import com.example.cardservice.application.domain.events.ApplicationCreatedEvent;
import com.example.cardservice.application.enums.ApplicationStatus;
import com.example.cardservice.application.providers.CustomerInfo;
import com.example.cardservice.outbox.OutboxService;
import com.example.cardservice.producers.events.ApplicationScoredEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScoringServiceTest {

    private static final String APPROVED_TOPIC =
            "application-approved";

    private static final String REJECTED_TOPIC =
            "application-rejected";

    @Mock
    private CardApplicationRepository repository;

    @Mock
    private OutboxService outboxService;

    @Mock
    private ApplicationCreatedEvent event;

    @Mock
    private CustomerInfo customerInfo;

    @Mock
    private CardApplicationEntity applicationEntity;

    private ScoringService scoringService;

    @BeforeEach
    void setUp() {
        scoringService = new ScoringService(
                repository,
                outboxService,
                0L
        );
    }


    @Test
    void scoreApplication_shouldApproveApplication_whenCustomerIsAdult() {

        UUID applicationId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        when(event.applicationId())
                .thenReturn(applicationId);

        when(event.customerInfo())
                .thenReturn(customerInfo);

        when(customerInfo.customerAge())
                .thenReturn(25);

        when(repository.findById(applicationId))
                .thenReturn(Optional.of(applicationEntity));

        when(applicationEntity.getId())
                .thenReturn(applicationId);

        when(applicationEntity.getCustomerId())
                .thenReturn(customerId);

        // вызов
        scoringService.scoreApplication(event);

        verify(applicationEntity)
                .setStatus(ApplicationStatus.APPROVED);

        ArgumentCaptor<ApplicationScoredEvent> eventCaptor =
                ArgumentCaptor.forClass(ApplicationScoredEvent.class);

        verify(outboxService)
                .saveEvent(eq(APPROVED_TOPIC),
                        eq("APPLICATION_APPROVED"),
                        eventCaptor.capture());

        ApplicationScoredEvent scoredEvent =
                eventCaptor.getValue();

        assertEquals(
                applicationId,
                scoredEvent.applicationId()
        );

        assertEquals(
                customerId,
                scoredEvent.customerId()
        );

        verify(repository)
                .findById(applicationId);
    }


    @Test
    void scoreApplication_shouldRejectApplication_whenCustomerIsUnder18() {

        UUID applicationId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        when(event.applicationId())
                .thenReturn(applicationId);

        when(event.customerInfo())
                .thenReturn(customerInfo);

        when(customerInfo.customerAge())
                .thenReturn(17);

        when(repository.findById(applicationId))
                .thenReturn(Optional.of(applicationEntity));

        when(applicationEntity.getId())
                .thenReturn(applicationId);

        when(applicationEntity.getCustomerId())
                .thenReturn(customerId);

        // вызов метода
        scoringService.scoreApplication(event);

        verify(applicationEntity)
                .setStatus(ApplicationStatus.REJECTED);

        ArgumentCaptor<ApplicationScoredEvent> eventCaptor =
                ArgumentCaptor.forClass(ApplicationScoredEvent.class);

        verify(outboxService)
                .saveEvent(eq(REJECTED_TOPIC),
                        eq("APPLICATION_REJECTED"),
                        eventCaptor.capture());

        ApplicationScoredEvent scoredEvent =
                eventCaptor.getValue();

        assertEquals(
                applicationId,
                scoredEvent.applicationId()
        );

        assertEquals(
                customerId,
                scoredEvent.customerId()
        );

        verify(repository)
                .findById(applicationId);
    }


    @Test
    void scoreApplication_shouldThrowException_whenApplicationDoesNotExist() {

        UUID applicationId = UUID.randomUUID();

        when(event.applicationId())
                .thenReturn(applicationId);

        when(repository.findById(applicationId))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> scoringService.scoreApplication(event)
                );

        assertEquals(
                "Ошибка в логике сервиса. Заявление должно быть.",
                exception.getMessage()
        );


        verify(repository)
                .findById(applicationId);

        verifyNoInteractions(
                outboxService
        );
    }
}