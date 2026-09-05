package com.example.cardservice.application.domain.integration;

import com.example.cardservice.application.domain.integration.config.IntegrationTestConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(
        classes = IntegrationTestConfig.class
)
@TestPropertySource(properties = {
        "scoring.delay-ms=0"
})
@ActiveProfiles("test")
public abstract class IntegrationTest {
}