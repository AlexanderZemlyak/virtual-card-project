package com.example.pdf_document_service.consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ApplicationApprovedConsumer {

    private static final Logger log = LoggerFactory.getLogger(
                    ApplicationApprovedConsumer.class
            );

    @KafkaListener(
            topics = "application-approved",
            groupId = "docgen-service-group"
    )
    public void consume(ApplicationApprovedEvent event) {

        log.info(
                """
                Kafka event received: application approved.
                Application id: {}
                Customer id: {}
                """,
                event.applicationId(),
                event.customerId()
        );

        generatePDFDocument(event);
    }

    private void generatePDFDocument(
            ApplicationApprovedEvent event
    ) {

        log.info(
                "generating PDF document for application {}",
                event.applicationId()
        );
    }
}
