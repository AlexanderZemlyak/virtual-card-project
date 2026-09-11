package com.example.card_service_tests.application.api;

import com.example.card_service.application.api.CardApplicationController;
import com.example.card_service.application.api.dto.CardApplicationResponse;
import com.example.card_service.application.domain.CardApplicationService;
import com.example.card_service.application.enums.ApplicationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CardApplicationControllerTest {

    @Mock
    private CardApplicationService applicationService;

    private MockMvc mockMvc;

    private UUID customerId;
    private UsernamePasswordAuthenticationToken authentication;

    @BeforeEach
    void setUp() {
        CardApplicationController controller =
                new CardApplicationController(applicationService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        customerId = UUID.randomUUID();

        authentication = new UsernamePasswordAuthenticationToken(
                customerId.toString(),
                null
        );
    }

    @Test
    void createApplication_shouldReturnCreatedApplication() throws Exception {
        UUID applicationId = UUID.randomUUID();

        var response = new CardApplicationResponse(
                applicationId,
                customerId,
                ApplicationStatus.CREATED
        );

        when(applicationService.createApplication(customerId))
                .thenReturn(response);

        mockMvc.perform(post("/api/applications")
                        .with(request -> {
                            request.setUserPrincipal(authentication);
                            return request;
                        }))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.applicationId").value(applicationId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.status").value("CREATED"));

        verify(applicationService).createApplication(customerId);
    }

    @Test
    void getApplication_shouldReturnApplication() throws Exception {
        UUID applicationId = UUID.randomUUID();

        var response = new CardApplicationResponse(
                applicationId,
                customerId,
                ApplicationStatus.CREATED
        );

        when(applicationService.getApplication(customerId, applicationId))
                .thenReturn(response);

        mockMvc.perform(get("/api/applications/{id}", applicationId)
                .with(request -> {
                    request.setUserPrincipal(authentication);
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.applicationId").value(applicationId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.status").value("CREATED"));

        verify(applicationService).getApplication(customerId, applicationId);
    }

    @Test
    void getCustomerApplications_shouldReturnApplications() throws Exception {
        UUID applicationId = UUID.randomUUID();

        var response = new CardApplicationResponse(
                applicationId,
                customerId,
                ApplicationStatus.CREATED
        );

        when(applicationService.getCustomerApplications(customerId))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/applications/my", customerId)
                        .with(request -> {
                            request.setUserPrincipal(authentication);
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].applicationId").value(applicationId.toString()))
                .andExpect(jsonPath("$[0].customerId").value(customerId.toString()))
                .andExpect(jsonPath("$[0].status").value("CREATED"));

        verify(applicationService).getCustomerApplications(customerId);
    }

    @Test
    void getApplication_shouldReturnBadRequestForInvalidId() throws Exception {
        mockMvc.perform(get("/api/applications/{id}", "invalid-uuid"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(applicationService);
    }
}

