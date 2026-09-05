package com.example.user_service.api.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        Integer age
) {
}
