package com.example.cardservice.application.domain.unit;

import com.example.cardservice.application.api.dto.CardApplicationResponse;
import com.example.cardservice.application.db.CardApplicationRepository;
import com.example.cardservice.application.db.entities.CardApplicationEntity;
import com.example.cardservice.application.domain.CardApplication;
import com.example.cardservice.application.domain.CardApplicationService;
import com.example.cardservice.application.domain.events.ApplicationCreatedEvent;
import com.example.cardservice.application.domain.exceptions.ApplicationNotFoundException;
import com.example.cardservice.application.enums.ApplicationStatus;
import com.example.cardservice.application.helpers.CardApplicationMapper;
import com.example.cardservice.application.providers.CustomerInfo;
import com.example.cardservice.application.providers.CustomerProvider;
import com.example.cardservice.application.providers.dto.CustomerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardApplicationServiceTest {

    @Mock
    private CardApplicationMapper mapper;

    @Mock
    private CardApplicationRepository repository;

    @Mock
    private CustomerProvider customerProvider;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private CardApplicationService selfProxy;

    private CardApplicationService service;

    @BeforeEach
    void setUp() {
        service = new CardApplicationService(
                mapper,
                repository,
                customerProvider,
                selfProxy,
                eventPublisher
        );
    }

    @Test
    void createApplication_shouldGetCustomerAndDelegateToSelfProxy() {

        UUID customerId = UUID.randomUUID();

        CustomerResponse customerResponse =
                mock(CustomerResponse.class);

        CardApplicationResponse expectedResponse =
                mock(CardApplicationResponse.class);

        when(customerResponse.id())
                .thenReturn(customerId);

        when(customerResponse.age())
                .thenReturn(25);

        when(customerProvider.getCustomer(customerId))
                .thenReturn(customerResponse);

        when(selfProxy.saveApplication(any(CustomerInfo.class)))
                .thenReturn(expectedResponse);


        CardApplicationResponse result =
                service.createApplication(customerId);


        assertSame(expectedResponse, result);

        verify(customerProvider)
                .getCustomer(customerId);

        ArgumentCaptor<CustomerInfo> captor =
                ArgumentCaptor.forClass(CustomerInfo.class);

        verify(selfProxy)
                .saveApplication(captor.capture());

        CustomerInfo actualCustomerInfo =
                captor.getValue();

        assertEquals(customerId, actualCustomerInfo.customerId());
        assertEquals(25, actualCustomerInfo.customerAge());

        verifyNoInteractions(repository);
        verifyNoInteractions(mapper);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void createApplication_shouldPropagateException_whenCustomerProviderFails() {

        UUID customerId = UUID.randomUUID();

        RuntimeException exception =
                new RuntimeException("Customer not found");

        when(customerProvider.getCustomer(customerId))
                .thenThrow(exception);

        RuntimeException thrown =
                assertThrows(
                        RuntimeException.class,
                        () -> service.createApplication(customerId)
                );

        assertSame(exception, thrown);

        verify(customerProvider)
                .getCustomer(customerId);

        verifyNoInteractions(
                selfProxy,
                repository,
                mapper,
                eventPublisher
        );
    }


    @Test
    void getApplication_shouldReturnApplication_whenApplicationExists() {

        UUID applicationId = UUID.randomUUID();

        CardApplicationEntity entity =
                mock(CardApplicationEntity.class);

        CardApplication application =
                mock(CardApplication.class);

        CardApplicationResponse expectedResponse =
                mock(CardApplicationResponse.class);

        when(repository.findById(applicationId))
                .thenReturn(Optional.of(entity));

        when(mapper.toDomain(entity))
                .thenReturn(application);

        when(mapper.toDTO(application))
                .thenReturn(expectedResponse);

        CardApplicationResponse result =
                service.getApplication(applicationId);

        assertSame(expectedResponse, result);

        verify(repository)
                .findById(applicationId);

        verify(mapper)
                .toDomain(entity);

        verify(mapper)
                .toDTO(application);

        verifyNoInteractions(
                customerProvider,
                selfProxy,
                eventPublisher
        );
    }

    @Test
    void getApplication_shouldThrowException_whenApplicationDoesNotExist() {

        UUID applicationId = UUID.randomUUID();

        when(repository.findById(applicationId))
                .thenReturn(Optional.empty());

        assertThrows(
                ApplicationNotFoundException.class,
                () -> service.getApplication(applicationId)
        );

        verify(repository)
                .findById(applicationId);

        verifyNoInteractions(
                mapper,
                customerProvider,
                selfProxy,
                eventPublisher
        );
    }

    @Test
    void getCustomerApplications_shouldReturnAllCustomerApplications() {

        UUID customerId = UUID.randomUUID();

        CardApplicationEntity firstEntity =
                mock(CardApplicationEntity.class);

        CardApplicationEntity secondEntity =
                mock(CardApplicationEntity.class);

        CardApplication firstApplication =
                mock(CardApplication.class);

        CardApplication secondApplication =
                mock(CardApplication.class);

        CardApplicationResponse firstResponse =
                mock(CardApplicationResponse.class);

        CardApplicationResponse secondResponse =
                mock(CardApplicationResponse.class);

        when(repository.findAllByCustomerId(customerId))
                .thenReturn(
                        List.of(
                                firstEntity,
                                secondEntity
                        )
                );

        when(mapper.toDomain(firstEntity))
                .thenReturn(firstApplication);

        when(mapper.toDomain(secondEntity))
                .thenReturn(secondApplication);

        when(mapper.toDTO(firstApplication))
                .thenReturn(firstResponse);

        when(mapper.toDTO(secondApplication))
                .thenReturn(secondResponse);

        List<CardApplicationResponse> result =
                service.getCustomerApplications(customerId);

        assertEquals(
                List.of(
                        firstResponse,
                        secondResponse
                ),
                result
        );

        verify(repository)
                .findAllByCustomerId(customerId);

        verify(mapper)
                .toDomain(firstEntity);

        verify(mapper)
                .toDomain(secondEntity);

        verify(mapper)
                .toDTO(firstApplication);

        verify(mapper)
                .toDTO(secondApplication);

        verifyNoInteractions(
                customerProvider,
                selfProxy,
                eventPublisher
        );
    }

    @Test
    void getCustomerApplications_shouldReturnEmptyList_whenCustomerHasNoApplications() {

        UUID customerId = UUID.randomUUID();

        when(repository.findAllByCustomerId(customerId))
                .thenReturn(List.of());

        List<CardApplicationResponse> result =
                service.getCustomerApplications(customerId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository)
                .findAllByCustomerId(customerId);

        verifyNoInteractions(
                mapper,
                customerProvider,
                selfProxy,
                eventPublisher
        );
    }

    @Test
    void saveApplication_shouldSaveApplicationPublishEventAndReturnResponse() {

        UUID customerId = UUID.randomUUID();

        CustomerInfo customerInfo = mock(CustomerInfo.class);

        when(customerInfo.customerId())
                .thenReturn(customerId);

        CardApplicationEntity applicationEntity =
                mock(CardApplicationEntity.class);

        CardApplicationResponse expectedResponse =
                mock(CardApplicationResponse.class);

        when(mapper.toEntity(any(CardApplication.class)))
                .thenReturn(applicationEntity);

        when(mapper.toDTO(any(CardApplication.class)))
                .thenReturn(expectedResponse);


        CardApplicationResponse result =
                service.saveApplication(customerInfo);

        assertSame(expectedResponse, result);


        ArgumentCaptor<CardApplication> applicationCaptor =
                ArgumentCaptor.forClass(CardApplication.class);

        verify(mapper)
                .toEntity(applicationCaptor.capture());

        CardApplication capturedApplication =
                applicationCaptor.getValue();

        assertNotNull(capturedApplication.getId());
        assertEquals(customerId, capturedApplication.getCustomerId());
        assertEquals(
                ApplicationStatus.CREATED,
                capturedApplication.getStatus()
        );
        assertNotNull(capturedApplication.getCreatedAt());

        verify(repository)
                .save(applicationEntity);

        ArgumentCaptor<ApplicationCreatedEvent> eventCaptor =
                ArgumentCaptor.forClass(
                        ApplicationCreatedEvent.class
                );

        verify(eventPublisher)
                .publishEvent(eventCaptor.capture());

        ApplicationCreatedEvent event =
                eventCaptor.getValue();

        assertEquals(
                capturedApplication.getId(),
                event.applicationId()
        );

        assertSame(
                customerInfo,
                event.customerInfo()
        );

        verify(mapper)
                .toDTO(capturedApplication);

        verifyNoMoreInteractions(
                repository,
                eventPublisher
        );
    }
}
