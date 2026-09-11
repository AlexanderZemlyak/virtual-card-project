package com.example.card_service_tests.application.domain.integration;

import com.example.card_service.application.domain.CardApplicationService;
import com.example.card_service.application.api.dto.CardApplicationResponse;
import com.example.card_service.application.db.CardApplicationRepository;
import com.example.card_service.application.db.entities.CardApplicationEntity;
import com.example.card_service_tests.application.domain.integration.config.CardApplicationServiceTestConfig;
import com.example.card_service.application.enums.ApplicationStatus;
import com.example.card_service.application.providers.CustomerInfo;
import com.example.card_service_tests.integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig({
    CardApplicationServiceTestConfig.class
})
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
                        customerId,
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
