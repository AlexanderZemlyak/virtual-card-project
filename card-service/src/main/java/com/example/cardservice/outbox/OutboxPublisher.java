package com.example.cardservice.outbox;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!test")
public class OutboxPublisher {

    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPublisher(
            OutboxEventRepository repository,
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publishEvents() {

        var events =
                repository.findByProcessedFalseOrderByCreatedAtAsc();

        for (OutboxEvent event : events) {

            try {

                kafkaTemplate
                        .send(
                                event.getTopic(),
                                event.getId().toString(),
                                event.getPayload()
                        )
                        .get();

                event.markProcessed();

            } catch (Exception e) {

                throw new RuntimeException(
                        "Не удалось отправить событие в Kafka",
                        e
                );
            }
        }
    }
}
