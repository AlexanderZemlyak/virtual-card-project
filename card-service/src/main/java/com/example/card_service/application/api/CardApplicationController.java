package com.example.card_service.application.api;

import com.example.card_service.application.api.dto.CardApplicationResponse;
import com.example.card_service.application.domain.CardApplicationService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
@Validated
public class CardApplicationController {

    private final CardApplicationService applicationService;

    public CardApplicationController(CardApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<CardApplicationResponse> createApplication(
            Authentication authentication
    ) {
        UUID customerId = UUID.fromString(authentication.getName());

        CardApplicationResponse response = applicationService.createApplication(customerId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardApplicationResponse> getApplication(
            @PathVariable(name = "id") UUID id,
            Authentication authentication
    ) {
        UUID customerId = UUID.fromString(authentication.getName());

        return ResponseEntity.ok(applicationService.getApplication(customerId, id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<CardApplicationResponse>> getMyApplications(
            Authentication authentication
    ) {
        UUID customerId = UUID.fromString(authentication.getName());

        return ResponseEntity.ok(applicationService.getCustomerApplications(customerId));
    }
}
