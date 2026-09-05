package com.example.cardservice.application.domain.integration;

import com.example.cardservice.application.domain.CardApplicationService;
import com.example.cardservice.application.api.dto.CardApplicationResponse;
import com.example.cardservice.application.db.CardApplicationRepository;
import com.example.cardservice.application.db.entities.CardApplicationEntity;
import com.example.cardservice.application.enums.ApplicationStatus;
import com.example.cardservice.application.providers.CustomerInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CardApplicationServiceIntegrationTest
        extends IntegrationTest {

    @Autowired
    private CardApplicationService service;

    @Autowired
    private CardApplicationRepository repository;


    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }


    @Test
    void saveApplication_shouldSaveApplicationToDatabase() {

        UUID customerId = UUID.randomUUID();

        CustomerInfo customerInfo =
                new CustomerInfo(
                        customerId,
                        25
                );

        CardApplicationResponse response =
                service.saveApplication(customerInfo);

        assertNotNull(response);
        assertNotNull(response.applicationId());

        var savedApplication =
                repository.findById(
                        response.applicationId()
                );

        assertTrue(savedApplication.isPresent());

        CardApplicationEntity entity =
                savedApplication.get();

        assertEquals(
                customerId,
                entity.getCustomerId()
        );

        assertEquals(
                ApplicationStatus.CREATED,
                entity.getStatus()
        );
    }

    @Test
    void getApplication_shouldReturnApplicationFromDatabase() {

        UUID customerId = UUID.randomUUID();

        CustomerInfo customerInfo =
                new CustomerInfo(
                        customerId,
                        25
                );

        CardApplicationResponse createdApplication =
                service.saveApplication(customerInfo);

        CardApplicationResponse result =
                service.getApplication(
                        createdApplication.applicationId()
                );

        assertNotNull(result);

        assertEquals(
                createdApplication.applicationId(),
                result.applicationId()
        );

        assertEquals(
                customerId,
                result.customerId()
        );

        assertEquals(
                ApplicationStatus.CREATED,
                result.status()
        );
    }
}
