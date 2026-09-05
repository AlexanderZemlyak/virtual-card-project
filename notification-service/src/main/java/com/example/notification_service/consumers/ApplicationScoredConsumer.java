package com.example.notification_service.consumers;

import com.example.notification_service.consumers.events.ApplicationScoredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ApplicationScoredConsumer {

    private static final Logger log = LoggerFactory.getLogger(
                    ApplicationScoredConsumer.class
            );

    @KafkaListener(
            topics = "application-approved",
            groupId = "notification-service-group"
    )
    public void consumeApproved(ApplicationScoredEvent event) {

        logEvent(event, "approved");

        sendApprovalNotification(event);
    }

    @KafkaListener(
            topics = "application-rejected",
            groupId = "notification-service-group"
    )
    public void consumeRejected(ApplicationScoredEvent event) {

        logEvent(event, "rejected");

        sendRejectionNotification(event);
    }

    private void logEvent(ApplicationScoredEvent event, String status) {
        log.info(
                """
                Kafka event received: application {}.
                Application id: {}
                Customer id: {}
                """,
                status,
                event.applicationId(),
                event.customerId()
        );
    }

    private void sendApprovalNotification(
            ApplicationScoredEvent event
    ) {

        log.info(
                "Sending APPROVAL notification for application {}",
                event.applicationId()
        );
    }

    private void sendRejectionNotification(
            ApplicationScoredEvent event
    ) {

        log.info(
                "Sending REJECTION notification for application {}",
                event.applicationId()
        );
    }
}
