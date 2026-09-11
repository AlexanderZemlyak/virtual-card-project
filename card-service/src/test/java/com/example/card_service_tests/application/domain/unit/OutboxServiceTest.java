package com.example.card_service_tests.application.domain.unit;

import com.example.card_service.outbox.OutboxEvent;
import com.example.card_service.outbox.OutboxEventRepository;
import com.example.card_service.outbox.OutboxService;
import com.example.card_service.producers.events.ApplicationScoredEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OutboxServiceTest {

    @Mock
    private OutboxEventRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    private OutboxService outboxService;

    private final Object event = new Object();
    private final String topic = "test_topic";
    private final String eventType = "test_event_type";

    @BeforeEach
    public void setUp() {
        outboxService = new OutboxService(
            repository,
            objectMapper
        );
    }

    @Test
    public void saveEvent_shouldSaveOutboxEvent() throws JsonProcessingException {

        String payload = "test_payload";

        when(objectMapper.writeValueAsString(event))
                .thenReturn(payload);

        outboxService.saveEvent(topic, eventType, event);

        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);

        verify(repository).save(captor.capture());

        var outboxEvent = captor.getValue();

        assertNotNull(outboxEvent.getId());
        assertEquals(topic, outboxEvent.getTopic());
        assertEquals(eventType, outboxEvent.getEventType());
        assertEquals(payload, outboxEvent.getPayload());
        assertNotNull(outboxEvent.getCreatedAt());
        assertFalse(outboxEvent.isProcessed());
    }

    @Test
    public void saveEvent_ShouldThrowRuntimeException_WhenSerializationCrushed() throws JsonProcessingException {

        when(objectMapper.writeValueAsString(event))
                .thenThrow(new JsonProcessingException("serialization error") {});

        assertThrows(
                RuntimeException.class,
                () -> outboxService.saveEvent(topic, eventType, event)
        );

        verify(repository, never()).save(any());
    }

}
