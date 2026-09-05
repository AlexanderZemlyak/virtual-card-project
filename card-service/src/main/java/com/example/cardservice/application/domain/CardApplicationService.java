package com.example.cardservice.application.domain;

import com.example.cardservice.application.domain.events.ApplicationCreatedEvent;
import com.example.cardservice.application.enums.ApplicationStatus;
import com.example.cardservice.application.helpers.CardApplicationMapper;
import com.example.cardservice.application.api.dto.CardApplicationResponse;
import com.example.cardservice.application.providers.CustomerInfo;
import com.example.cardservice.application.providers.CustomerProvider;
import com.example.cardservice.application.db.CardApplicationRepository;
import com.example.cardservice.application.domain.exceptions.ApplicationNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class CardApplicationService {

    private final CardApplicationMapper mapper;
    private final CardApplicationRepository repository;
    private final CustomerProvider customerProvider;
    private final CardApplicationService selfProxy;
    private final ApplicationEventPublisher eventPublisher;
    private static final Logger log = LoggerFactory.getLogger(CardApplicationService.class);

    public CardApplicationService(CardApplicationMapper mapper,
                                  CardApplicationRepository repository,
                                  CustomerProvider customerProvider,
                                  @Lazy CardApplicationService selfProxy,
                                  ApplicationEventPublisher eventPublisher) {
        this.mapper = mapper;
        this.repository = repository;
        this.customerProvider = customerProvider;
        this.selfProxy = selfProxy;
        this.eventPublisher = eventPublisher;
    }

    public CardApplicationResponse createApplication(UUID customerId) {
        // Запрашиваем информацию о пользователе
        var customerInfo = CustomerInfo.from(customerProvider.getCustomer(customerId));

        return selfProxy.saveApplication(customerInfo);
    }

    @Transactional
    public CardApplicationResponse saveApplication(
            CustomerInfo customerInfo
    ) {
        CardApplication application = new CardApplication(
                UUID.randomUUID(),
                customerInfo.customerId(),
                ApplicationStatus.CREATED,
                Instant.now()
        );

        var applicationEntity = mapper.toEntity(application);

        repository.save(applicationEntity);

        log.info("Заявление для пользователя с id {} зарегистрировано", customerInfo.customerId());

        eventPublisher.publishEvent(
                new ApplicationCreatedEvent(
                        application.getId(),
                        customerInfo
        ));

        return mapper.toDTO(application);
    }

    @Transactional(readOnly = true)
    public CardApplicationResponse getApplication(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain)
                .map(mapper::toDTO)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<CardApplicationResponse> getCustomerApplications(UUID customerId) {
        return repository.findAllByCustomerId(customerId)
                .stream()
                .map(mapper::toDomain)
                .map(mapper::toDTO)
                .toList();
    }
}