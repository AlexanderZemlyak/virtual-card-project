package com.example.user_service.api.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record LoginRequest(
        @NotBlank
        @Length(min = 2, max = 50)
        String userName,
        @NotBlank
        @Length(min = 5, max = 100)
        String password
) { }
