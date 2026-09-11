package com.example.card_service_tests.application.api;

import com.example.card_service.application.db.CardApplicationRepository;
import com.example.card_service.application.db.entities.CardApplicationEntity;
import com.example.card_service.application.enums.ApplicationStatus;
import com.example.card_service.application.providers.CustomerProvider;
import com.example.card_service.application.providers.dto.CustomerResponse;
import com.example.card_service.application.providers.exceptions.CustomerServiceUnavailableException;
import com.example.card_service_tests.application.api.config.CardApplicationControllerIntegrationTestConfig;
import com.example.card_service_tests.integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig({
        CardApplicationControllerIntegrationTestConfig.class
})
class CardApplicationControllerIntegrationTest extends IntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CardApplicationRepository repository;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private CustomerProvider customerProvider;

    private MockMvc mockMvc;

    private String createJwt(UUID customerId) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(customerId.toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        repository.deleteAll();
    }

    @Test
    void getApplication_shouldReturnUnauthorizedWithoutJwt() throws Exception {
        mockMvc.perform(
                        get("/api/applications/" + UUID.randomUUID())
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getApplication_shouldReturnUnauthorizedWithInvalidJwt() throws Exception {
        mockMvc.perform(
                        get("/api/applications/" + UUID.randomUUID())
                                .header("Authorization", "Bearer invalid.jwt.token")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getApplication_shouldReturnOkWithValidJwt() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID applicationId = UUID.randomUUID();

        repository.save(new CardApplicationEntity(
                applicationId,
                customerId,
                ApplicationStatus.CREATED,
                Instant.now()
        ));

        String jwt = createJwt(customerId);

        mockMvc.perform(
                        get("/api/applications/{id}", applicationId)
                                .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationId").value(applicationId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void getApplication_shouldReturnForbiddenForAnotherUser() throws Exception {
        UUID ownerId = UUID.randomUUID();
        UUID anotherUserId = UUID.randomUUID();
        UUID applicationId = UUID.randomUUID();

        repository.save(new CardApplicationEntity(
                applicationId,
                ownerId,
                ApplicationStatus.CREATED,
                Instant.now()
        ));

        String jwt = createJwt(anotherUserId);

        mockMvc.perform(
                        get("/api/applications/{id}", applicationId)
                                .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void createApplication_shouldReturnCreatedWithValidJwt() throws Exception {
        UUID customerId = UUID.randomUUID();

        String jwt = createJwt(customerId);

        when(customerProvider.getCustomer(customerId))
                .thenReturn(new CustomerResponse(
                        customerId,
                        "Vasya",
                        25
                ));

        mockMvc.perform(
                        post("/api/applications")
                                .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.status").value("CREATED"));

        var applications = repository.findAll();

        assertEquals(1, applications.size());
        assertEquals(customerId, applications.getFirst().getCustomerId());
        assertEquals(ApplicationStatus.CREATED, applications.getFirst().getStatus());
    }

    @Test
    void createApplication_shouldReturnServiceUnavailableWhenCustomerProviderThrowsError() throws Exception {
        UUID customerId = UUID.randomUUID();

        String jwt = createJwt(customerId);

        when(customerProvider.getCustomer(customerId))
                .thenThrow(new CustomerServiceUnavailableException(
                        "Сервис пользователей недоступен."
                ));

        mockMvc.perform(
                        post("/api/applications")
                                .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void createApplication_shouldReturnUnauthorizedWithoutJwt() throws Exception {
        mockMvc.perform(
                        post("/api/applications")
                )
                .andExpect(status().isUnauthorized());

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void getMyApplications_shouldReturnOnlyCurrentUserApplications() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID anotherCustomerId = UUID.randomUUID();

        CardApplicationEntity first = new CardApplicationEntity(
                UUID.randomUUID(),
                customerId,
                ApplicationStatus.CREATED,
                Instant.now()
        );

        CardApplicationEntity second = new CardApplicationEntity(
                UUID.randomUUID(),
                customerId,
                ApplicationStatus.APPROVED,
                Instant.now()
        );

        CardApplicationEntity anotherUserApplication = new CardApplicationEntity(
                UUID.randomUUID(),
                anotherCustomerId,
                ApplicationStatus.CREATED,
                Instant.now()
        );

        repository.save(first);
        repository.save(second);
        repository.save(anotherUserApplication);

        String jwt = createJwt(customerId);

        mockMvc.perform(
                        get("/api/applications/my")
                                .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].customerId").value(customerId.toString()))
                .andExpect(jsonPath("$[1].customerId").value(customerId.toString()));
    }

    @Test
    void getMyApplications_shouldReturnUnauthorizedWithoutJwt() throws Exception {
        mockMvc.perform(
                        get("/api/applications/my")
                )
                .andExpect(status().isUnauthorized());
    }
}
