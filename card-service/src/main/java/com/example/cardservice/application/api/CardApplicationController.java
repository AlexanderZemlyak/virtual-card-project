package com.example.cardservice.application.api;

import com.example.cardservice.application.api.dto.CardApplicationResponse;
import com.example.cardservice.application.domain.CardApplicationService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @NotNull @RequestParam(name = "customerId") UUID customerId) {
        CardApplicationResponse response = applicationService.createApplication(customerId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardApplicationResponse> getApplication(@NotNull @PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(applicationService.getApplication(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CardApplicationResponse>> getCustomerApplications(
            @NotNull @PathVariable(name = "customerId") UUID customerId) {
        return ResponseEntity.ok(applicationService.getCustomerApplications(customerId));
    }
}
