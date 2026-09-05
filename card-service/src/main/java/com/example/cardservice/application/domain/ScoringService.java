package com.example.cardservice.application.domain;

import com.example.cardservice.application.domain.events.ApplicationCreatedEvent;
import com.example.cardservice.application.enums.ApplicationStatus;
import com.example.cardservice.application.providers.CustomerInfo;
import com.example.cardservice.application.db.CardApplicationRepository;
import com.example.cardservice.outbox.OutboxService;
import com.example.cardservice.producers.events.ApplicationScoredEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Profile("!test")
public class ScoringService {

    private static final String APPROVED_TOPIC =
            "application-approved";

    private static final String REJECTED_TOPIC =
            "application-rejected";

    private final CardApplicationRepository repository;
    private final OutboxService outboxService;

    private final long scoringDelay;

    public ScoringService(CardApplicationRepository repository,
                          OutboxService outboxService,
                          @Value("${scoring.delay-ms:30000}") long scoringDelay
    ) {
        this.repository = repository;
        this.outboxService = outboxService;
        this.scoringDelay = scoringDelay;
    }

    @Async
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void scoreApplication(ApplicationCreatedEvent e) {

        var applicationEntity = repository.findById(e.applicationId())
                .orElseThrow(() -> new RuntimeException("Ошибка в логике сервиса. Заявление должно быть."));

        boolean approved = performScoring(e.customerInfo());

        ApplicationScoredEvent event =
                new ApplicationScoredEvent(
                        applicationEntity.getId(),
                        applicationEntity.getCustomerId()
                );

        if (approved) {

            applicationEntity.setStatus(ApplicationStatus.APPROVED);

            // event для Кафки
            outboxService.saveEvent(
                    APPROVED_TOPIC,
                    "APPLICATION_APPROVED",
                    event
            );
        } else {

            applicationEntity.setStatus(ApplicationStatus.REJECTED);

            // event для Кафки
            outboxService.saveEvent(
                    REJECTED_TOPIC,
                    "APPLICATION_REJECTED",
                    event
            );
        }
    }

    private boolean performScoring(CustomerInfo customerInfo) {
        try {
            // Имитация долгой работы
            Thread.sleep(scoringDelay);

            if (customerInfo.customerAge() < 18)
                return false;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        return true;
    }
}
