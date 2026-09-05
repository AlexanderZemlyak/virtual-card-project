package com.example.pdf_document_service.consumers;

import java.util.UUID;

public record ApplicationApprovedEvent(
        UUID applicationId,
        UUID customerId
) {
}